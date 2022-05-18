package com.inkwy.database.datasource.meta;

import com.inkwy.database.datasource.domain.TableInfo;

import java.util.List;

/**
 * meta信息interface

 * @ClassName BaseDatabaseMeta
 * @Version 1.0
 * @create 2021-07-02 15:45
 */
public abstract class BaseDatabaseMeta implements DatabaseInterface {

    @Override
    public String getSQLQueryFields(String tableName) {
        return "SELECT * FROM " + tableName + " where 1=0";
    }

    @Override
    public String getSQLQueryTablesNameComments() {
        return "select table_name,table_comment from information_schema.tables where table_schema=?";
    }

    @Override
    public String getSQLQueryTableNameComment() {
        return "select table_name,table_comment from information_schema.tables where table_schema=? and table_name = ?";
    }

    @Override
    public String getSQLQueryPrimaryKey() {
        return null;
    }

    @Override
    public String getSQLQueryComment(String schemaName, String tableName, String columnName) {
        return null;
    }

    @Override
    public String getSQLQueryColumns(String... args) {
        return null;
    }

    @Override
    public String getMaxId(String tableName, String primaryKey) {
        return String.format("select max(%s) from %s", primaryKey, tableName);
    }

    @Override
    public String getMinValue(String tableName, String primaryKey) {
        return String.format("select min(%s) from %s", primaryKey, tableName);
    }

    @Override
    public String getSQLQueryTableSchema(String... args) {
        return null;
    }

    @Override
    public String getSQLQueryTables() {
        return null;
    }

    @Override
    public String getSQLQueryTables(String... tableSchema) {
        return null;
    }

    @Override
    public String getMaxDateValQuerySql(String tableName, List<String> fileds) {
        if (fileds.size() == 2) {
            return String.format("select max(concat(%s, ' ', %s)) from %s ", fileds.get(0), fileds.get(1), tableName);
        }
        return String.format("select max(%s) from %s", fileds.get(0), tableName);
    }

    @Override
    public String getTableInfoBySchema(String schema) {
        return null;
    }

    @Override
    public String getColumnInfoBySchemaAndTableName(String schema, String tableName) {
        return null;
    }

    @Override
    public String getCreateSqlByTableName(String schema,String tableName) {
        return null;
    }
}
