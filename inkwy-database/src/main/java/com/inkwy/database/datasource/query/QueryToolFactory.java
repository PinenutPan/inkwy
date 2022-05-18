package com.inkwy.database.datasource.query;

import com.inkwy.database.datasource.constants.JdbcConstants;
import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.datasource.exception.RdbmsException;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * 工具类，获取单例实体

 * @ClassName QueryToolFactory
 * @Version 1.0
 * @create 2021-07-02 9:36
 */
@Slf4j
public class QueryToolFactory {

    public static BaseQueryTool getByDbType(DataSource jobDatasource) {
        //获取dbType
        String dataSource = String.valueOf(jobDatasource.getType());
        if (JdbcConstants.MYSQL_TYPE.equals(dataSource)) {
            return getMySQLQueryToolInstance(jobDatasource);
        } else if (JdbcConstants.ORACLE_TYPE.equals(dataSource)) {
            return getOracleQueryToolInstance(jobDatasource);
        }
        throw new UnsupportedOperationException("找不到该类型: ".concat(dataSource));
    }

    private static BaseQueryTool getMySQLQueryToolInstance(DataSource jdbcDatasource) {
        try {
            return new MySQLQueryTool(jdbcDatasource);
        } catch (Exception e) {
            log.error("error", e);
            throw RdbmsException.asConnException(JdbcConstants.MYSQL_TYPE,
                    e, jdbcDatasource.getUsername(), jdbcDatasource.getName());
        }
    }

    private static BaseQueryTool getOracleQueryToolInstance(DataSource jdbcDatasource) {
        try {
            return new OracleQueryTool(jdbcDatasource);
        } catch (SQLException e) {
            throw RdbmsException.asConnException(JdbcConstants.ORACLE_TYPE,
                    e, jdbcDatasource.getUsername(), jdbcDatasource.getName());
        }
    }

//    private static BaseQueryTool getHiveQueryToolInstance(DataSource jdbcDatasource) {
////        try {
////            return new HiveQueryTool(jdbcDatasource);
////        } catch (SQLException e) {
////            throw RdbmsException.asConnException(JdbcConstants.HIVE_TYPE,
////                    e, jdbcDatasource.getUsername(), jdbcDatasource.getName());
////        }
////    }
}
