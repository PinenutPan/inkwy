package com.inkwy.database.datasource.meta;

/**
 * MySQL数据库 meta信息查询

 * @ClassName MySQLDatabaseMeta
 * @Version 1.0
 * @since 2021/7/8 15:48
 */
public class HBaseDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {

    private volatile static HBaseDatabaseMeta single;

    public static HBaseDatabaseMeta getInstance() {
        if (single == null) {
            synchronized (HBaseDatabaseMeta.class) {
                if (single == null) {
                    single = new HBaseDatabaseMeta();
                }
            }
        }
        return single;
    }


    @Override
    public String getSQLQueryTables(String... tableSchema) {
        return null;
    }
}
