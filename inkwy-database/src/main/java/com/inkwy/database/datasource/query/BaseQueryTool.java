package com.inkwy.database.datasource.query;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.inkwy.database.datasource.constants.Constants;
import com.inkwy.database.datasource.constants.JdbcConstants;
import com.inkwy.database.datasource.domain.ColumnInfo;
import com.inkwy.database.datasource.domain.DasColumn;
import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.datasource.domain.TableInfo;
import com.inkwy.database.datasource.meta.DatabaseInterface;
import com.inkwy.database.datasource.meta.DatabaseMetaFactory;
import com.inkwy.database.datasource.utils.SpringContextUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * 抽象查询工具

 * @ClassName BaseQueryTool
 * @Version 1.0
 * @create 2021-07-02 9:22
 */
public abstract class BaseQueryTool implements QueryToolInterface {
    private String aseKey = (String) SpringContextUtil.getProperty("aes.key");
    protected static final Logger logger = LoggerFactory.getLogger(BaseQueryTool.class);
    /**
     * 用于获取查询语句
     */
    protected DatabaseInterface sqlBuilder;

    private javax.sql.DataSource datasource;

    protected Connection connection;
    /**
     * 当前数据库名
     */
    private String currentSchema;
    private String currentDatabase;

    /**
     * 构造方法
     *
     * @param jobDatasource
     */
    BaseQueryTool(DataSource jobDatasource) throws SQLException {
        getDataSource(jobDatasource);
        sqlBuilder = DatabaseMetaFactory.getByDbType(String.valueOf(jobDatasource.getType()));
        currentSchema = getSchema(jobDatasource.getUsername());
        currentDatabase = String.valueOf(jobDatasource.getType());
    }

    /**
     * 获取数据源
     *
     * @param jobDatasource
     * @throws SQLException
     */
    private void getDataSource(DataSource jobDatasource) throws SQLException {
        String userName = jobDatasource.getUsername();

        //这里默认使用 hikari 数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(userName);
        dataSource.setPassword(jobDatasource.getPassword());
        dataSource.setJdbcUrl(jobDatasource.getUrl());
        dataSource.setDriverClassName(getDriverClassName(String.valueOf(jobDatasource.getType())));
        dataSource.setMaximumPoolSize(1);
        dataSource.setMinimumIdle(0);
        dataSource.setConnectionTimeout(30000);
        this.datasource = dataSource;
        this.connection = this.datasource.getConnection();
    }

    /**
     * 根据connection获取schema
     *
     * @param jdbcUsername
     * @return
     */
    private String getSchema(String jdbcUsername) {
        String res = null;
        try {
            res = connection.getCatalog();
        } catch (SQLException e) {
            try {
                res = connection.getSchema();
            } catch (SQLException e1) {
                logger.error("[SQLException getSchema Exception] --> "
                        + "the exception message is:", e1);
            }
            logger.error("[getSchema Exception] --> "
                    + "the exception message is:", e);
        }
        // 如果res是null，则将用户名当作 schema
        if (StringUtils.isBlank(res) && StringUtils.isNotBlank(jdbcUsername)) {
            res = jdbcUsername.toUpperCase();
        }
        return res;
    }

    private String getDriverClassName(String dataType) {
        switch (dataType) {
            case JdbcConstants.MYSQL_TYPE:
                return JdbcConstants.MYSQL_DRIVER;
            case JdbcConstants.ORACLE_TYPE:
                return JdbcConstants.ORACLE_DRIVER;
            case JdbcConstants.HIVE_TYPE:
                return JdbcConstants.HIVE_DRIVER;
            default:
                return "";
        }
    }

    /**
     * 构建表信息
     *
     * @param tableName 表名
     * @return
     */
    @Override
    public TableInfo buildTableInfo(String tableName) {
        //获取表信息
        List<Map<String, Object>> tableInfos = getTableInfo(tableName);
        if (tableInfos.isEmpty()) {
            throw new NullPointerException("查询出错! ");
        }

        TableInfo tableInfo = new TableInfo();
        //表名，注释
        List tValues = new ArrayList(tableInfos.get(0).values());

        tableInfo.setName(StrUtil.toString(tValues.get(0)));
        tableInfo.setComment(StrUtil.toString(tValues.get(1)));


        //获取所有字段
        List<ColumnInfo> fullColumn = getColumns(tableName);
        tableInfo.setColumns(fullColumn);

        //获取主键列
        List<String> primaryKeys = getPrimaryKeys(tableName);
        logger.info("主键列为：{}", primaryKeys);

        //设置ifPrimaryKey标志
        fullColumn.forEach(e -> {
            boolean isprimaryKey = primaryKeys.contains(e.getName()) ? true : false;
            e.setIfPrimaryKey(isprimaryKey);
        });
        return tableInfo;
    }

    /**
     * 无论怎么查，返回结果都应该只有表名和表注释，遍历map拿value值即可
     *
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> getTableInfo(String tableName) {
        String sqlQueryTableNameComment = sqlBuilder.getSQLQueryTableNameComment();
        logger.info(sqlQueryTableNameComment);
        List<Map<String, Object>> res = null;
        try {
            res = JdbcUtils.executeQuery(connection, sqlQueryTableNameComment, ImmutableList.of(currentSchema, tableName));
        } catch (SQLException e) {
            logger.error("[getTableInfo Exception] --> "
                    + "the exception message is:", e);
        }
        return res;
    }

    /**
     * 查询表
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getTables() {
        String sqlQueryTables = sqlBuilder.getSQLQueryTables();
        logger.info(sqlQueryTables);
        List<Map<String, Object>> res = null;
        try {
            res = JdbcUtils.executeQuery(connection, sqlQueryTables, ImmutableList.of(currentSchema));
        } catch (SQLException e) {
            logger.error("[getTables Exception] --> "
                    + "the exception message is:", e);
        }
        return res;
    }

    /**
     * 根据表名查询表字段详细信息
     *
     * @param tableName
     * @return
     */
    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;
        List<ColumnInfo> fullColumn = Lists.newArrayList();
        //获取指定表的所有字段
        try {
            //获取查询指定表所有字段的sql语句
            String querySql = sqlBuilder.getSQLQueryFields(tableName);
            logger.info("querySql: {}", querySql);

            //获取所有字段
            statement = connection.createStatement();
            resultSet = statement.executeQuery(querySql);
            ResultSetMetaData metaData = resultSet.getMetaData();

            List<DasColumn> dasColumns = buildDasColumn(tableName, metaData);

            //构建 fullColumn
            fullColumn = buildFullColumn(dasColumns);

        } catch (SQLException e) {
            logger.error("[getColumns Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(statement);
            JdbcUtils.close(resultSet);
        }
        return fullColumn;
    }

    private List<ColumnInfo> buildFullColumn(List<DasColumn> dasColumns) {
        List<ColumnInfo> res = Lists.newArrayList();
        dasColumns.forEach(e -> {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setIndex(e.getColumnIndex());
            columnInfo.setName(e.getColumnName());
            columnInfo.setComment(e.getColumnComment());
            // 统一转小写
            columnInfo.setType(e.getColumnTypeName().toLowerCase());
            columnInfo.setIfPrimaryKey(e.isIsprimaryKey());
            columnInfo.setIsnull(e.getIsNull());
            res.add(columnInfo);
        });
        return res;
    }

    /**
     * 构建DasColumn对象
     *
     * @param tableName
     * @param metaData
     * @return
     */
    private List<DasColumn> buildDasColumn(String tableName, ResultSetMetaData metaData) {
        List<DasColumn> res = Lists.newArrayList();
        Statement statement = null;
        try {
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                DasColumn dasColumn = new DasColumn();
                dasColumn.setColumnIndex(i - 1);
                dasColumn.setColumnClassName(metaData.getColumnClassName(i));
                dasColumn.setColumnTypeName(metaData.getColumnTypeName(i));
                dasColumn.setColumnName(metaData.getColumnName(i));
                dasColumn.setIsNull(metaData.isNullable(i));

                res.add(dasColumn);
            }

            statement = connection.createStatement();

            if (currentDatabase.equals(JdbcConstants.MYSQL_TYPE) || currentDatabase.equals(JdbcConstants.ORACLE_TYPE)) {
                DatabaseMetaData databaseMetaData = connection.getMetaData();

                ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);

                while (resultSet.next()) {
                    String name = resultSet.getString("COLUMN_NAME");
                    res.forEach(e -> {
                        boolean isprimaryKey = e.getColumnName().equals(name) ? true : false;
                        e.setIsprimaryKey(isprimaryKey);
                    });
                }
                for (DasColumn e : res) {
                    ResultSet resultSetComment = null;

                    String sqlQueryComment = sqlBuilder.getSQLQueryComment(currentSchema, tableName, e.getColumnName());
                    //查询字段注释
                    try {
                        resultSetComment = statement.executeQuery(sqlQueryComment);
                        while (resultSetComment.next()) {
                            e.setColumnComment(resultSetComment.getString(1));
                        }
                    } catch (SQLException e1) {
                        logger.error("[buildDasColumn executeQuery Exception] --> "
                                + "the exception message is:", e1);
                    } finally {
                        JdbcUtils.close(resultSetComment);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("[buildDasColumn Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(statement);
        }
        return res;
    }

    /**
     * 获取指定表的主键，可能是多个，所以用list
     *
     * @param tableName
     * @return
     */
    public List<String> getPrimaryKeys(String tableName) {
        ResultSet rs = null;
        List<String> primaryKeys = new ArrayList<>();
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            rs = dbMeta.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                primaryKeys.add(rs.getString("column_name"));
            }
            // 去重
            return new ArrayList<>(new LinkedHashSet<>(primaryKeys));
        } catch (SQLException e) {
            logger.error("[getPrimaryKeys Exception] --> "
                    + "the exception message is:", e);
        }
        return primaryKeys;
    }

    /**
     * 根据表名，datasource查询表字段名称
     *
     * @param tableName
     * @param datasource
     * @return
     */
    @Override
    public List<String> getColumnNames(String tableName, String datasource) {
        List<String> res = Lists.newArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //获取查询指定表所有字段的sql语句
            String querySql = sqlBuilder.getSQLQueryFields(tableName);
            logger.info("querySql: {}", querySql);

            //获取所有字段
            stmt = connection.createStatement();
            rs = stmt.executeQuery(querySql);
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                if (JdbcConstants.HIVE_TYPE.equals(datasource)) {
                    if (columnName.contains(Constants.SPLIT_POINT)) {
                        res.add(i - 1 + Constants.SPLIT_SCOLON + columnName.substring(columnName.indexOf(Constants.SPLIT_POINT) + 1) + Constants.SPLIT_SCOLON + metaData.getColumnTypeName(i));
                    } else {
                        res.add(i - 1 + Constants.SPLIT_SCOLON + columnName + Constants.SPLIT_SCOLON + metaData.getColumnTypeName(i));
                    }
                } else {
                    res.add(columnName);
                }

            }
        } catch (SQLException e) {
            logger.error("[getColumnNames Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return res;
    }

    /**
     * oracle 根据schema查询表名
     *
     * @param tableSchema
     * @return
     */
    @Override
    public List<String> getTableNames(String tableSchema) {
        List<String> tables = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = getSQLQueryTables(tableSchema);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
            tables.sort(Comparator.naturalOrder());
        } catch (SQLException e) {
            logger.error("[getTableNames(tableSchema) Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return tables;
    }

    /**
     * 查询表名
     *
     * @return
     */
    @Override
    public List<String> getTableNames() {
        List<String> tables = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = getSQLQueryTables();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
        } catch (SQLException e) {
            logger.error("[getTableNames Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return tables;
    }

    /**
     * 数据源测试连接
     *
     * @return
     */
    public Boolean dataSourceTest() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData.getDatabaseProductName().length() > 0) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("[dataSourceTest Exception] --> "
                    + "the exception message is:", e);
        }
        return false;
    }


    protected String getSQLQueryTables(String tableSchema) {
        return sqlBuilder.getSQLQueryTables(tableSchema);
    }

    /**
     * 不需要其他参数的可不重写
     *
     * @return
     */
    protected String getSQLQueryTables() {
        return sqlBuilder.getSQLQueryTables();
    }

    /**
     * 解析querySql得到表名
     *
     * @param querySql
     * @return
     * @throws SQLException
     */
    @Override
    public List<String> getColumnNamesByQuerySql(String querySql) throws SQLException {

        List<String> res = Lists.newArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        try {
//            //动态sql处理
//            GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", (name) -> {
//                return "'1'";
//            });
//            querySql = genericTokenParser.parse(querySql);

            querySql = querySql.replace(";", "");
            //拼装sql语句，在后面加上 where 1=0 即可
            String sql = querySql.concat(" where 1=0");
            //判断是否已有where，如果是，则加 and 1=0
            //从最后一个 ) 开始找 where，或者整个语句找
            if (querySql.contains(")")) {
                if (querySql.substring(querySql.indexOf(")")).contains("where")) {
                    sql = querySql.concat(" and 1=0");
                }
            } else {
                if (querySql.contains("where")) {
                    sql = querySql.concat(" and 1=0");
                }
            }
            //获取所有字段
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                res.add(metaData.getColumnName(i));
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return res;
    }

    /**
     * 解析querySql得到详细字段信息
     *
     * @param querySql
     * @return
     * @throws SQLException
     */
    @Override
    public List<ColumnInfo> getColumnsByQuerySql(String querySql) throws SQLException {

        List<ColumnInfo> fullColumn = Lists.newArrayList();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            querySql = querySql.replace(";", "");
            //拼装sql语句，在后面加上 where 1=0 即可
            String sql = querySql.concat(" where 1=0");
            //判断是否已有where，如果是，则加 and 1=0
            //从最后一个 ) 开始找 where，或者整个语句找
            if (querySql.contains(")")) {
                if (querySql.substring(querySql.indexOf(")")).contains("where")) {
                    sql = querySql.concat(" and 1=0");
                }
            } else {
                if (querySql.contains("where")) {
                    sql = querySql.concat(" and 1=0");
                }
            }
            //获取所有字段
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();

            List<DasColumn> dasColumns = new ArrayList<>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                DasColumn dasColumn = new DasColumn();
                dasColumn.setColumnIndex(i - 1);
                dasColumn.setColumnClassName(metaData.getColumnClassName(i));
                dasColumn.setColumnTypeName(metaData.getColumnTypeName(i));
                dasColumn.setColumnName(metaData.getColumnName(i));
                dasColumn.setIsNull(metaData.isNullable(i));

                dasColumns.add(dasColumn);
            }

            //构建 fullColumn
            fullColumn = buildFullColumn(dasColumns);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return fullColumn;
    }

    @Override
    public long getMaxIdVal(String tableName, String primaryKey) {
        Statement stmt = null;
        ResultSet rs = null;
        long maxVal = 0;
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = getSQLMaxID(tableName, primaryKey);
            rs = stmt.executeQuery(sql);
            rs.next();
            maxVal = rs.getLong(1);
        } catch (SQLException e) {
            logger.error("[getMaxIdVal Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return maxVal;
    }

    @Override
    public String getMaxStringVal(String tableName, String field) {
        Statement stmt = null;
        ResultSet rs = null;
        String maxVal = null;
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = getSQLMaxID(tableName, field);
            rs = stmt.executeQuery(sql);
            rs.next();
            maxVal = rs.getString(1);
        } catch (SQLException e) {
            logger.error("[getMaxStringVal Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return maxVal;
    }

    @Override
    public String getMinStringVal(String tableName, String field) {
        Statement stmt = null;
        ResultSet rs = null;
        String maxVal = null;
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = sqlBuilder.getMinValue(tableName, field);
            rs = stmt.executeQuery(sql);
            rs.next();
            maxVal = rs.getString(1);
        } catch (SQLException e) {
            logger.error("[getMinStringVal Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return maxVal;
    }


    private String getSQLMaxID(String tableName, String primaryKey) {
        return sqlBuilder.getMaxId(tableName, primaryKey);
    }


    public void executeCreateTableSql(String querySql) {
        if (StringUtils.isBlank(querySql)) {
            return;
        }
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(querySql);
        } catch (SQLException e) {
            logger.error("[executeCreateTableSql Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(stmt);
        }
    }

    public List<String> getTableSchema() {
        List<String> schemas = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = getSQLQueryTableSchema();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString(1);
                schemas.add(tableName);
            }
        } catch (SQLException e) {
            logger.error("[getTableSchema Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return schemas;
    }

    protected String getSQLQueryTableSchema() {
        return sqlBuilder.getSQLQueryTableSchema();
    }


    public String getMaxVal(String tableName, List<String> fileds) {
        Statement stmt = null;
        ResultSet rs = null;
        String date = "";
        try {
            stmt = connection.createStatement();
            //获取sql
            String sql = sqlBuilder.getMaxDateValQuerySql(tableName, fileds);
            rs = stmt.executeQuery(sql);
            rs.next();
            date = rs.getString(1);
        } catch (SQLException e) {
            logger.error("[getMaxVal Exception] --> "
                    + "the exception message is:", e);
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return date;
    }
}
