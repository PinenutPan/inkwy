package com.inkwy.database.datasource.query;

import com.alibaba.druid.util.JdbcUtils;
import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.dto.ColumnDto;
import com.inkwy.database.dto.TableDto;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Oracle数据库使用的查询工具

 * @author pjp20
 * @ClassName MySQLQueryTool
 * @Version 1.0
 * @create 2021-07-02 9:31
 */
public class OracleQueryTool extends BaseQueryTool implements QueryToolInterface {

    public OracleQueryTool(DataSource jobDatasource) throws SQLException {
        super(jobDatasource);
        //Cannot access com.galaxy.dataexc.manager.script.query.QueryToolInterface
    }

    @Override
    public List<TableDto> getTableINfoBySchema(String schema) {
        Statement stmt = null;
        ResultSet tableRs = null;
        List<TableDto> tableDtoList = new ArrayList<>();
        try {
            Long startTime = System.currentTimeMillis();
            stmt = connection.createStatement();
            //获取sql
            String sql = sqlBuilder.getTableInfoBySchema(schema);
            logger.info(String.format("正在查询库%s所有的表,sql：%s",schema,sql));
            tableRs = stmt.executeQuery(sql);
            while (tableRs.next()) {
                TableDto tableDto = new TableDto();
                tableDto.setTableName(tableRs.getString(1));
                tableDtoList.add(tableDto);
            }
            if (!CollectionUtils.isEmpty(tableDtoList)) {
                for (TableDto tableDto : tableDtoList) {
                    PreparedStatement columnPs = null;
                    ResultSet columnRs = null;
                    try {
                        // 通过schema查询所有的表
                        String tableSql = sqlBuilder.getColumnInfoBySchemaAndTableName(schema, tableDto.getTableName());
                        logger.info(String.format("正在查询表%s信息,sql：%s",tableDto.getTableName(),tableSql));
                        columnPs = connection.prepareStatement(tableSql);
                        columnRs = columnPs.executeQuery();
                        List<ColumnDto> columnDtoList = new ArrayList<>();
                        while (columnRs.next()) {
                            ColumnDto columnDto = new ColumnDto();
                            columnDto.setTableName(tableDto.getTableName());
                            columnDto.setColumnName(Optional.ofNullable(columnRs.getString("COLUMN_NAME")).orElse(""));
                            columnDto.setColumnComment(Optional.ofNullable(columnRs.getString("COMMENTS")).orElse(""));
                            columnDto.setDataType(Optional.ofNullable(columnRs.getString("DATA_TYPE")).orElse(""));
                            columnDto.setColumnType(Optional.ofNullable(columnRs.getString("DATA_LENGTH")).orElse(""));
                            columnDto.setColumnDefault(Optional.ofNullable(columnRs.getString("DATA_DEFAULT")).orElse(""));
                            columnDto.setIsNullable(Optional.ofNullable(columnRs.getString("NULLABLE")).orElse(""));
                            columnDtoList.add(columnDto);
                        }
                        tableDto.setColumnDtoList(columnDtoList);
                    } finally {
                        try {
                            columnPs.close();
                            columnRs.close();
                        } catch (SQLException e) {
                            logger.error("close connection error", e);
                        }
                    }
                }
            }

            Long endTime = System.currentTimeMillis();
            logger.info(String.format("******获取schema：%s共%s张表的表字段信息数据结束,耗时：%sms******",schema,tableDtoList.size(),(endTime - startTime)));
        } catch (SQLException e) {
            logger.error("[getMaxVal Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(tableRs);
            JdbcUtils.close(stmt);
        }

        return tableDtoList;
    }

    @Override
    public String getCreateSqlBySchema(String schema) {
        Statement stmt = null;
        ResultSet tableRs = null;
        StringBuilder createSqlBuild = new StringBuilder();
        List<TableDto> tableDtoList = new ArrayList<>();
        try {
            Long startTime = System.currentTimeMillis();
            stmt = connection.createStatement();
            //获取sql
            String sql = sqlBuilder.getTableInfoBySchema(schema);
            logger.info(String.format("正在查询库%s所有的表,sql：%s",schema,sql));
            tableRs = stmt.executeQuery(sql);
            while (tableRs.next()) {
                TableDto tableDto = new TableDto();
                tableDto.setTableName(tableRs.getString(1));
                tableDtoList.add(tableDto);
            }
            if (!CollectionUtils.isEmpty(tableDtoList)) {
                for (TableDto tableDto : tableDtoList) {
                    PreparedStatement createTablePs = null;
                    ResultSet createTableRs = null;
                    try {
                        // 通过表名查询建表语句
                        String createTableSql = sqlBuilder.getCreateSqlByTableName(schema,tableDto.getTableName());
                        logger.info(String.format("正在查询表%s建表语句,sql：%s",tableDto.getTableName(),createTableSql));
                        createTablePs = connection.prepareStatement(createTableSql);
                        createTableRs = createTablePs.executeQuery();
                        while (createTableRs.next()) {
                            createSqlBuild.append(Optional.ofNullable(createTableRs.getString(1)).orElse(""))
                                    .append("\r\n").append("\r\n");
                        }
                    } finally {
                        try {
                            createTablePs.close();
                            createTableRs.close();
                        } catch (SQLException e) {
                            logger.error("close connection error", e);
                        }
                    }
                }
            }

            Long endTime = System.currentTimeMillis();
            logger.info(String.format("******获取schema：%s共%s张表的建表语句数据结束,耗时：%sms******",schema,tableDtoList.size(),(endTime - startTime)));
        } catch (SQLException e) {
            logger.error("[getMaxVal Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(tableRs);
            JdbcUtils.close(stmt);
        }

        return createSqlBuild.toString();
    }
}
