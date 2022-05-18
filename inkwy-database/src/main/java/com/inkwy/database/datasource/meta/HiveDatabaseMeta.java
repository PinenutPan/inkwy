package com.inkwy.database.datasource.meta;

/**
 * hive元数据信息

 * @ClassName HiveDatabaseMeta
 * @Version 1.0
 * @create 2021-07-02 15:45
 */
public class HiveDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {
    private volatile static HiveDatabaseMeta single;

    public static HiveDatabaseMeta getInstance() {
        if (single == null) {
            synchronized (HiveDatabaseMeta.class) {
                if (single == null) {
                    single = new HiveDatabaseMeta();
                }
            }
        }
        return single;
    }

    @Override
    public String getSQLQueryTables() {
        return "show tables";
    }
}
