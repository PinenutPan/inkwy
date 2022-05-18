package com.inkwy.database.service.impl;

import com.inkwy.database.dto.*;
import com.inkwy.database.param.DatabaseParam;
import com.inkwy.database.service.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatabaseServiceImpl implements DatabaseService {

    @Override
    public List<CompareAllResultDto> compareBySchema(DatabaseParam databaseParam) {
        Long startTime = System.currentTimeMillis();
        // 源数据库的所有表
        List<TableDto> sourceTables = this.getTableDtos(databaseParam.getSourceUrl(), databaseParam.getSourceUsername(),
                databaseParam.getSourcePassword(), databaseParam.getSourceSchema());
        // 目标数据库的所有表
        List<TableDto> targetTables = this.getTableDtos(databaseParam.getTargetUrl(), databaseParam.getTargetUsername(),
                databaseParam.getTargetPassword(), databaseParam.getTargetSchema());
        Long endTime = System.currentTimeMillis();
        log.info("获取源数据库的所有库、表，耗时：" + (endTime - startTime) / 1000 + "s");
        List<CompareAllResultDto> result = new ArrayList<>();
        CompareAllResultDto compareAllResultDto = new CompareAllResultDto();
        compareAllResultDto.setSchemaName("目标schema：" + databaseParam.getTargetSchema() + ",源schema：" + databaseParam.getSourceSchema());
        compareAllResultDto.setMsg("success");
        compareAllResultDto.setCompareResultDtoList(doTableCompare(sourceTables, targetTables));
        result.add(compareAllResultDto);
        return result;
    }

    @Override
    public List<CompareAllResultDto> compare(DatabaseParam databaseParam) {
        Long startTime = System.currentTimeMillis();

        // 源数据库的所有库、表
        List<SchemaDto> sourceSchemas = this.getSchemaDtos(databaseParam.getSourceUrl(), databaseParam.getSourceUsername(), databaseParam.getSourcePassword());
        // 目标数据库的所有库、表
        List<SchemaDto> targetSchemas = this.getSchemaDtos(databaseParam.getTargetUrl(), databaseParam.getTargetUsername(), databaseParam.getTargetPassword());
        // 获取比较的结果
        List<CompareAllResultDto> resultDtos = doSchemaCompare(sourceSchemas, targetSchemas);

        Long endTime = System.currentTimeMillis();
        log.info("获取源数据库的所有库、表，耗时：" + (endTime - startTime) / 1000 + "s");
        return resultDtos;
    }


    /**
     * 获取scheme的表数据
     *
     * @param url
     * @param username
     * @param password
     * @param schema
     * @return
     */
    private List<TableDto> getTableDtos(String url, String username, String password, String schema) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);

            String tableSql = "select\n" +
                    "  table_comment,\n" +
                    "  table_name\n" +
                    "from information_schema.TABLES\n" +
                    "where table_schema = '" + schema + "'";

            PreparedStatement preparedStatement = conn.prepareStatement(tableSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<TableDto> tableDtoList = new ArrayList<>();
            while (resultSet.next()) {
                TableDto tableDto = new TableDto();
                tableDto.setTableName(resultSet.getString("table_name"));
                tableDto.setTableComment(resultSet.getString("table_comment"));
                tableDtoList.add(tableDto);
            }

            if (!CollectionUtils.isEmpty(tableDtoList)) {
                for (TableDto tableDto : tableDtoList) {
                    tableSql = "select\n" +
                            "  column_name,\n" +
                            "  data_type,\n" +
                            "  column_type,\n" +
                            "  column_comment\n" +
                            "from information_schema.COLUMNS\n" +
                            "where table_name = '" + tableDto.getTableName() + "'" +
                            "and table_schema = '" + schema + "'";
                    PreparedStatement ps = conn.prepareStatement(tableSql);
                    ResultSet rs = ps.executeQuery();
                    List<ColumnDto> columnDtoList = new ArrayList<>();
                    while (rs.next()) {
                        ColumnDto columnDto = new ColumnDto();
                        columnDto.setTableName(tableDto.getTableName());
                        columnDto.setColumnName(Optional.ofNullable(rs.getString("column_name")).orElse(""));
                        columnDto.setColumnComment(Optional.ofNullable(rs.getString("column_comment")).orElse(""));
                        columnDto.setDataType(Optional.ofNullable(rs.getString("data_type")).orElse(""));
                        columnDto.setColumnType(Optional.ofNullable(rs.getString("column_type")).orElse(""));
                        columnDtoList.add(columnDto);
                    }
                    tableDto.setColumnDtoList(columnDtoList);
                }
            }
            return tableDtoList;
        } catch (SQLException e) {
            log.error("query sql error", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("close connection error", e);
            }
        }
        return null;
    }

    /**
     * 比较两个schema表差异
     *
     * @param sourceTables
     * @param targetTables
     * @return
     */
    private static List<CompareResultDto> doTableCompare(List<TableDto> sourceTables, List<TableDto> targetTables) {

        List<CompareResultDto> resultDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sourceTables) && !CollectionUtils.isEmpty(targetTables)) {
            /** 按表名分组 */
            Map<String, List<TableDto>> sourceMap = sourceTables.stream().collect(Collectors.groupingBy(TableDto::getTableName));
            Map<String, List<TableDto>> targetMap = targetTables.stream().collect(Collectors.groupingBy(TableDto::getTableName));
            sourceMap.forEach((key, value) -> {
                String tableName = value.get(0).getTableName();
                if (targetMap.containsKey(key)) {
                    /** 获取两个表的字段 */
                    List<String> sourceColumnNames = value.get(0).getColumnDtoList().stream().map(ColumnDto::getColumnName).collect(Collectors.toList());
                    List<String> targetColumnNames = targetMap.get(key).get(0).getColumnDtoList().stream().map(ColumnDto::getColumnName).collect(Collectors.toList());
                    /** 比较两个表的字段 */
                    sourceColumnNames.removeAll(targetColumnNames);
                    if (!CollectionUtils.isEmpty(sourceColumnNames)) {
                        /** 如果sourceTables中存在targetTables中没有的表没有的字段，返回表名+字段 */
                        CompareResultDto compareResultDto = new CompareResultDto();
                        compareResultDto.setTableName(tableName);
                        compareResultDto.setMsg(tableName.concat("表缺失字段：".concat(sourceColumnNames.toString())));
                        resultDtos.add(compareResultDto);
                        log.info(tableName.concat("表缺失字段：".concat(sourceColumnNames.toString())));
                    }
                } else {
                    /** 如果sourceTables中存在targetTables中没有的表，直接返回表名 */
                    CompareResultDto compareResultDto = new CompareResultDto();
                    compareResultDto.setTableName(tableName);
                    compareResultDto.setMsg(tableName.concat("表缺失，请添加！"));
                    resultDtos.add(compareResultDto);
                    log.info(tableName.concat("表缺失，请添加！"));
                }
            });
        }
        return resultDtos;
    }


    /**
     * 获取所有scheme的表数据
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    private List<SchemaDto> getSchemaDtos(String url, String username, String password) {
        Connection conn = null;
        PreparedStatement schemaPs = null;
        ResultSet schemaRs = null;
        try {
            conn = DriverManager.getConnection(url, username, password);

            // 通过schema查询所有的表
            String schemaSql = "select SCHEMA_NAME from information_schema.SCHEMATA where SCHEMA_NAME not in ('information_schema','mysql')";
            schemaPs = conn.prepareStatement(schemaSql);
            schemaRs = schemaPs.executeQuery();
            List<SchemaDto> schemaDtoList = new ArrayList<>();
            while (schemaRs.next()) {
                String schemaName = schemaRs.getString("schema_name");
                SchemaDto schemaDto = new SchemaDto();
                schemaDto.setSchemaName(schemaName);
                schemaDtoList.add(schemaDto);
            }
            if (!CollectionUtils.isEmpty(schemaDtoList)) {
                for (SchemaDto schemaDto : schemaDtoList) {
                    Long startTime = System.currentTimeMillis();
                    log.info("******正在获取schema：" + schemaDto.getSchemaName() + "所有表数据......******");

                    // 通过schema查询所有的表
                    String tableSql = "select\n" +
                            "  table_comment,\n" +
                            "  table_name\n" +
                            "from information_schema.TABLES\n" +
                            "where table_schema = '" + schemaDto.getSchemaName() + "'";

                    PreparedStatement tablePs = conn.prepareStatement(tableSql);
                    ResultSet tableRs = tablePs.executeQuery();
                    List<TableDto> tableDtoList = new ArrayList<>();
                    while (tableRs.next()) {
                        TableDto tableDto = new TableDto();
                        tableDto.setTableName(tableRs.getString("table_name"));
                        tableDto.setTableComment(tableRs.getString("table_comment"));
                        tableDtoList.add(tableDto);
                    }

                    if (!CollectionUtils.isEmpty(tableDtoList)) {
                        for (TableDto tableDto : tableDtoList) {
                            PreparedStatement columnPs = null;
                            ResultSet columnRs = null;
                            try {
                                // 通过schema查询所有的表
                                tableSql = "select\n" +
                                        "  column_name,\n" +
                                        "  data_type,\n" +
                                        "  column_type,\n" +
                                        "  column_comment\n" +
                                        "from information_schema.COLUMNS\n" +
                                        "where table_name = '" + tableDto.getTableName() + "'" +
                                        "and table_schema = '" + schemaDto.getSchemaName() + "'";
                                columnPs = conn.prepareStatement(tableSql);
                                columnRs = columnPs.executeQuery();
                                List<ColumnDto> columnDtoList = new ArrayList<>();
                                while (columnRs.next()) {
                                    ColumnDto columnDto = new ColumnDto();
                                    columnDto.setTableName(tableDto.getTableName());
                                    columnDto.setColumnName(Optional.ofNullable(columnRs.getString("column_name")).orElse(""));
                                    columnDto.setColumnComment(Optional.ofNullable(columnRs.getString("column_comment")).orElse(""));
                                    columnDto.setDataType(Optional.ofNullable(columnRs.getString("data_type")).orElse(""));
                                    columnDto.setColumnType(Optional.ofNullable(columnRs.getString("column_type")).orElse(""));
                                    columnDtoList.add(columnDto);
                                }
                                tableDto.setColumnDtoList(columnDtoList);
                            } finally {
                                try {
                                    columnPs.close();
                                    columnRs.close();
                                } catch (SQLException e) {
                                    log.error("close connection error", e);
                                }
                            }
                        }
                        schemaDto.setTableDtoList(tableDtoList);
                    }

                    Long endTime = System.currentTimeMillis();
                    log.info("******获取schema：" + schemaDto.getSchemaName() + "所有表数据结束,耗时：" + (endTime - startTime) + "ms******");
                }

                return schemaDtoList;
            }

        } catch (SQLException e) {
            log.error("query sql error", e);
        } finally {
            try {
                conn.close();
                schemaPs.close();
                schemaRs.close();
            } catch (SQLException e) {
                log.error("close connection error", e);
            }
        }
        return null;
    }

    /**
     * 比较两个schema表差异
     *
     * @param sourceSchemas
     * @param targetSchemas
     * @return
     */
    private static List<CompareAllResultDto> doSchemaCompare(List<SchemaDto> sourceSchemas, List<SchemaDto> targetSchemas) {
        List<CompareAllResultDto> result = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sourceSchemas) && !CollectionUtils.isEmpty(targetSchemas)) {
            // 按库名分组
            Map<String, List<SchemaDto>> sourceSchemaMap = sourceSchemas.stream().collect(Collectors.groupingBy(SchemaDto::getSchemaName));
            Map<String, List<SchemaDto>> targetSchemaMap = targetSchemas.stream().collect(Collectors.groupingBy(SchemaDto::getSchemaName));
            sourceSchemaMap.forEach((sKey, sValue) -> {
                String schemaName = sValue.get(0).getSchemaName();
                if (targetSchemaMap.containsKey(sKey)) {
                    List<TableDto> sourceTables = sValue.get(0).getTableDtoList();
                    List<TableDto> targetTables = targetSchemaMap.get(sKey).get(0).getTableDtoList();
                    if (CollectionUtils.isEmpty(sourceTables) || CollectionUtils.isEmpty(targetTables)) {
                        log.info("schema：" + sValue.get(0).getSchemaName() + "中无存在的表");
                    } else {
                        /** 按表名分组 */
                        Map<String, List<TableDto>> sourceMap = sourceTables.stream().collect(Collectors.groupingBy(TableDto::getTableName));
                        Map<String, List<TableDto>> targetMap = targetTables.stream().collect(Collectors.groupingBy(TableDto::getTableName));
                        List<CompareResultDto> resultDtos = new ArrayList<>();
                        Long startTime = System.currentTimeMillis();
                        log.info("******正在比较库：" + schemaName + "中所有表字段数据......******");
                        sourceMap.forEach((key, value) -> {
                            String tableName = value.get(0).getTableName();
                            if (targetMap.containsKey(key)) {
                                /** 获取两个表的字段 */
                                List<String> sourceColumnNames = value.get(0).getColumnDtoList().stream().map(ColumnDto::getColumnName).collect(Collectors.toList());
                                List<String> targetColumnNames = targetMap.get(key).get(0).getColumnDtoList().stream().map(ColumnDto::getColumnName).collect(Collectors.toList());
                                /** 比较两个表的字段 */
                                sourceColumnNames.removeAll(targetColumnNames);
                                if (!CollectionUtils.isEmpty(sourceColumnNames)) {
                                    /** 如果sourceTables中存在targetTables中没有的表没有的字段，返回表名+字段 */
                                    CompareResultDto compareResultDto = new CompareResultDto();
                                    compareResultDto.setTableName(tableName);
                                    compareResultDto.setMsg(tableName.concat("表缺失字段：".concat(sourceColumnNames.toString())));
                                    resultDtos.add(compareResultDto);
                                    log.info("表缺失字段：".concat(sourceColumnNames.toString()));
                                }
                            } else {
                                /** 如果sourceTables中存在targetTables中没有的表，直接返回表名 */
                                CompareResultDto compareResultDto = new CompareResultDto();
                                compareResultDto.setTableName(tableName);
                                compareResultDto.setMsg(tableName.concat("表缺失，请添加！"));
                                resultDtos.add(compareResultDto);
                                log.info(tableName.concat("表缺失，请添加！"));
                            }
                        });
                        if (!CollectionUtils.isEmpty(resultDtos)) {
                            CompareAllResultDto compareAllResultDto = new CompareAllResultDto();
                            compareAllResultDto.setSchemaName(schemaName);
                            compareAllResultDto.setCompareResultDtoList(resultDtos);
                            result.add(compareAllResultDto);
                        }

                        Long endTime = System.currentTimeMillis();
                        log.info("******库：" + schemaName + "所有字段数据比较结束,耗时：" + (endTime - startTime) + "ms******");
                    }
                } else {
                    /** 如果sourceTables中存在targetTables中没有的表，直接返回表名 */
                    CompareAllResultDto compareAllResultDto = new CompareAllResultDto();
                    compareAllResultDto.setSchemaName(schemaName);
                    compareAllResultDto.setMsg(schemaName.concat("库缺失，请添加！"));
                    result.add(compareAllResultDto);
                    log.info(schemaName.concat("库缺失，请添加！"));
                }
            });
        }
        return result;
    }


}
