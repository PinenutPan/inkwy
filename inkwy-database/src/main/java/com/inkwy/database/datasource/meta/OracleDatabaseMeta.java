package com.inkwy.database.datasource.meta;

import com.inkwy.database.datasource.domain.TableInfo;

import java.util.List;

/**
 * Oracle数据库 meta信息查询

 * @ClassName MySQLDatabaseMeta
 * @Version 1.0
 * @create 2021-07-02 15:48
 */
public class OracleDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {

    private volatile static OracleDatabaseMeta single;

    public static OracleDatabaseMeta getInstance() {
        if (single == null) {
            synchronized (OracleDatabaseMeta.class) {
                if (single == null) {
                    single = new OracleDatabaseMeta();
                }
            }
        }
        return single;
    }


    @Override
    public String getSQLQueryComment(String schemaName, String tableName, String columnName) {
        return String.format("select B.comments \n" +
                "  from user_tab_columns A, user_col_comments B\n" +
                " where a.COLUMN_NAME = b.column_name\n" +
                "   and A.Table_Name = B.Table_Name\n" +
                "   and A.Table_Name = upper('%s')\n" +
                "   AND A.column_name  = '%s'", tableName, columnName);
    }

    @Override
    public String getSQLQueryPrimaryKey() {
        return "select cu.column_name from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.owner = ? and au.constraint_type = 'P' and au.table_name = ?";
    }

    @Override
    public String getSQLQueryTablesNameComments() {
        return "select table_name,comments from user_tab_comments";
    }

    @Override
    public String getSQLQueryTableNameComment() {
        return "select table_name,comments from user_tab_comments where table_name = ?";
    }

    @Override
    public String getSQLQueryTables(String... tableSchema) {
        if (!"database".equals(tableSchema[0])) {
            return "select table_name from dba_tables where owner='" + tableSchema[0] + "'";
        }
        return "select TABLE_NAME from user_tables";
    }

    @Override
    public String getSQLQueryTableSchema(String... args) {
        return "select username from sys.dba_users";
    }


    @Override
    public String getSQLQueryTables() {
        return "select table_name from user_tab_comments";
    }

    @Override
    public String getSQLQueryColumns(String... args) {
        return "select table_name,comments from user_tab_comments where table_name = ?";
    }

    @Override
    public String getMaxDateValQuerySql(String tableName, List<String> fileds) {
        if (fileds.size() == 2) {
            return String.format("select max(CONCAT(CONCAT(TO_CHAR(%s,'yyyy-MM-dd'), ' '), %s)) from %s ", fileds.get(0), fileds.get(1), tableName);
        }
        return String.format("select max(%s) from %s", fileds.get(0), tableName);
    }

    @Override
    public String getTableInfoBySchema(String schema) {
        return String.format("select TABLE_NAME  from all_tables where OWNER = upper('%s')",schema);
    }

    @Override
    public String getColumnInfoBySchemaAndTableName(String schema, String tableName) {
        return String.format("select a.TABLE_NAME, a.COLUMN_NAME, b.COMMENTS, a.DATA_TYPE, a.DATA_LENGTH, a.NULLABLE, a.DATA_DEFAULT\n" +
                "from ALL_TAB_COLUMNS a,\n" +
                "     user_col_comments B\n" +
                "where OWNER = upper('%s')\n" +
                "  and a.COLUMN_NAME = b.column_name\n" +
                "  and A.Table_Name = B.Table_Name\n" +
                "  and a.TABLE_NAME = '%s'",schema,tableName);
    }

    @Override
    public String getCreateSqlByTableName(String schema, String tableName) {
        return String.format("select dbms_metadata.get_ddl('TABLE', '%s',upper('%s')) from dual",tableName,schema);
    }
}
