package com.inkwy.database.datasource.exception;


import com.inkwy.database.datasource.constants.Constants;
import com.inkwy.database.datasource.constants.JdbcConstants;

/**
 * RdbmsException

 * @ClassName RdbmsException
 * @Version 1.0
 * @create 2021-07-02 09:15
 */
public class RdbmsException extends DataException {


    public RdbmsException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public static DataException asConnException(String dataBaseType, Exception e, String userName, String dbName) {
        if (dataBaseType.equals(JdbcConstants.MYSQL_TYPE)) {
            DBUtilErrorCode dbUtilErrorCode = mySqlConnectionErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_CONN_DB_ERROR && dbName != null) {
                return DataException.asDataXException(dbUtilErrorCode, "该数据库名称为：" + dbName + " 具体错误信息为：" + e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.MYSQL_CONN_USERPWD_ERROR) {
                return DataException.asDataXException(dbUtilErrorCode, "该数据库用户名为：" + userName + " 具体错误信息为：" + e);
            }
            return DataException.asDataXException(dbUtilErrorCode, " 具体错误信息为：" + e);
        }

        if (dataBaseType.equals(JdbcConstants.ORACLE_TYPE)) {
            DBUtilErrorCode dbUtilErrorCode = oracleConnectionErrorAna(e.getMessage());
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_CONN_DB_ERROR && dbName != null) {
                return DataException.asDataXException(dbUtilErrorCode, "该数据库名称为：" + dbName + " 具体错误信息为：" + e);
            }
            if (dbUtilErrorCode == DBUtilErrorCode.ORACLE_CONN_USERPWD_ERROR) {
                return DataException.asDataXException(dbUtilErrorCode, "该数据库用户名为：" + userName + " 具体错误信息为：" + e);
            }
            return DataException.asDataXException(dbUtilErrorCode, " 具体错误信息为：" + e);
        }
        if (dataBaseType.equals(JdbcConstants.FTP_TYPE)) {
            return DataException.asDataXException(DBUtilErrorCode.FTP_CONN_DB_ERROR, " 具体错误信息为：" + e.getMessage());
        }
        if (dataBaseType.equals(JdbcConstants.AWS_TYPE)) {
            return DataException.asDataXException(DBUtilErrorCode.AWS_CONN_DB_ERROR, " 具体错误信息为：" + e.getMessage());
        }
        return DataException.asDataXException(DBUtilErrorCode.CONN_DB_ERROR, " 具体错误信息为：" + e.getMessage());
    }

    public static DBUtilErrorCode mySqlConnectionErrorAna(String e) {
        if (e.contains(Constants.MYSQL_DATABASE)) {
            return DBUtilErrorCode.MYSQL_CONN_DB_ERROR;
        }

        if (e.contains(Constants.MYSQL_CONNEXP)) {
            return DBUtilErrorCode.MYSQL_CONN_IPPORT_ERROR;
        }

        if (e.contains(Constants.MYSQL_ACCDENIED)) {
            return DBUtilErrorCode.MYSQL_CONN_USERPWD_ERROR;
        }

        return DBUtilErrorCode.CONN_DB_ERROR;
    }

    public static DBUtilErrorCode oracleConnectionErrorAna(String e) {
        if (e.contains(Constants.ORACLE_DATABASE)) {
            return DBUtilErrorCode.ORACLE_CONN_DB_ERROR;
        }

        if (e.contains(Constants.ORACLE_CONNEXP)) {
            return DBUtilErrorCode.ORACLE_CONN_IPPORT_ERROR;
        }

        if (e.contains(Constants.ORACLE_ACCDENIED)) {
            return DBUtilErrorCode.ORACLE_CONN_USERPWD_ERROR;
        }

        return DBUtilErrorCode.CONN_DB_ERROR;
    }
}
