package com.inkwy.database.datasource.meta;

import com.inkwy.database.datasource.domain.TableInfo;

import java.util.List;

public interface DatabaseInterface {

    /**
     * Returns the minimal SQL to launch in order to determine the layout of the resultset for a given database table
     *
     * @param tableName The name of the table to determine the layout for
     * @return The SQL to launch.
     */
    String getSQLQueryFields(String tableName);

    /**
     * 获取主键字段
     *
     * @return
     */
    String getSQLQueryPrimaryKey();

    String getSQLQueryTableNameComment();

    String getSQLQueryTablesNameComments();

    /**
     * 获取所有表名的sql
     *
     * @return
     */
    String getSQLQueryTables(String... tableSchema);

    /**
     * 获取所有表名的sql
     *
     * @return
     */
    String getSQLQueryTables();

    /**
     * 获取 Table schema
     *
     * @return
     */
    String getSQLQueryTableSchema(String... args);

    /**
     * 获取所有的字段的sql
     *
     * @return
     */
    String getSQLQueryColumns(String... args);

    /**
     * 获取表和字段注释的sql语句
     *
     * @return The SQL to launch.
     */
    String getSQLQueryComment(String schemaName, String tableName, String columnName);


    /**
     * 获取当前表maxId
     *
     * @param tableName
     * @param primaryKey
     * @return
     */
    String getMaxId(String tableName, String primaryKey);

    /**
     * 获取当前字段的最小值
     *
     * @param tableName
     * @param primaryKey
     * @return
     */
    String getMinValue(String tableName, String primaryKey);

    String getMaxDateValQuerySql(String tableName, List<String> fileds);

    /**
     * 根据schema查询表信息
     * @param schema
     * @return
     */
    String getTableInfoBySchema(String schema);

    /**
     * 根据schema、tableName查询表字段
     * @param schema
     * @param tableName
     * @return
     */
    String getColumnInfoBySchemaAndTableName(String schema, String tableName);

    /**
     * 根据schema、tableName查询表字段
     * @param schema
     * @param tableName
     * @return
     */
    String getCreateSqlByTableName(String schema, String tableName);
}
