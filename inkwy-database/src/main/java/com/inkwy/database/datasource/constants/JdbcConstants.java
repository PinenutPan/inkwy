package com.inkwy.database.datasource.constants;


/**
 * JdbcConstants

 * @ClassName JdbcConstants
 * @Version 1.0
 * @create 2021-07-02 09:15
 */
public interface JdbcConstants {
    String MYSQL = "mysql";
    String MYSQL_TYPE = "1";
    String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    String ORACLE = "oracle";
    String ORACLE_TYPE = "2";
    String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

    String HIVE = "hive";
    String HIVE_TYPE = "3";
    String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    String AWS = "aws";
    String AWS_TYPE = "4";

    String FTP = "ftp";
    String FTP_TYPE = "5";

    String HBASE = "hbase";
    String HBASE_TYPE = "6";

    String TXT = "txtfile";
    String TXT_TYPE = "7";

}
