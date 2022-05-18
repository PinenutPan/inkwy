package com.inkwy.database.datasource.meta;


import com.inkwy.database.datasource.constants.JdbcConstants;

/**
 * meta信息工厂

 * @ClassName DatabaseMetaFactory
 * @Version 1.0
 * @create 2021-07-02 15:55
 */
public class DatabaseMetaFactory {

    //根据数据库类型返回对应的接口
    public static DatabaseInterface getByDbType(String dbType) {
        if (JdbcConstants.MYSQL_TYPE.equals(dbType)) {
            return MySQLDatabaseMeta.getInstance();
        } else if (JdbcConstants.ORACLE_TYPE.equals(dbType)) {
            return OracleDatabaseMeta.getInstance();
        } else if (JdbcConstants.HIVE_TYPE.equals(dbType)) {
            return HiveDatabaseMeta.getInstance();
        } else {
            throw new UnsupportedOperationException("暂不支持的类型：".concat(dbType));
        }
    }
}
