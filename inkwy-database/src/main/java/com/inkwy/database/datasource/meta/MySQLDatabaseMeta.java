package com.inkwy.database.datasource.meta;

/**
 * MySQL数据库 meta信息查询

 * @ClassName MySQLDatabaseMeta
 * @Version 1.0
 * @create 2021-07-02 15:48
 */
public class MySQLDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {

    private volatile static MySQLDatabaseMeta single;

    public static MySQLDatabaseMeta getInstance() {
        if (single == null) {
            synchronized (MySQLDatabaseMeta.class) {
                if (single == null) {
                    single = new MySQLDatabaseMeta();
                }
            }
        }
        return single;
    }

    @Override
    public String getSQLQueryTableSchema(String... args) {
        return "select SCHEMA_NAME from information_schema.SCHEMATA where SCHEMA_NAME not in ('information_schema','mysql')";
    }

    @Override
    public String getSQLQueryComment(String schemaName, String tableName, String columnName) {
        return String.format("SELECT COLUMN_COMMENT FROM information_schema.COLUMNS where TABLE_SCHEMA = '%s' and TABLE_NAME = '%s' and COLUMN_NAME = '%s'", schemaName, tableName, columnName);
    }

    @Override
    public String getSQLQueryPrimaryKey() {
        return "select column_name from information_schema.columns where table_schema=? and table_name=? and column_key = 'PRI'";
    }

    @Override
    public String getSQLQueryTables() {
        return "show tables";
    }

    @Override
    public String getSQLQueryColumns(String... args) {
        return "select column_name from information_schema.columns where table_schema=? and table_name=?";
    }

    @Override
    public String getTableInfoBySchema(String schema) {
        return String.format("select table_comment,table_name from information_schema.TABLES where table_schema='%s'",schema);
    }

    @Override
    public String getColumnInfoBySchemaAndTableName(String schema, String tableName) {
        return String.format("select column_name, data_type, column_type, column_comment, column_default, is_nullable\n" +
                "from information_schema.COLUMNS\n" +
                "where table_schema = '%s'\n" +
                "  and table_name = '%s'",schema,tableName);
    }

    @Override
    public String getCreateSqlByTableName(String schema, String tableName) {
        return String.format("show create table `%s`",tableName);
    }
}
