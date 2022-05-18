package com.inkwy.database.datasource.query;


import com.inkwy.database.datasource.domain.ColumnInfo;
import com.inkwy.database.datasource.domain.TableInfo;
import com.inkwy.database.dto.TableDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 基础查询接口

 * @version 1.0
 * @create 2021-07-02
 */
public interface QueryToolInterface {
    /**
     * 构建 tableInfo对象
     *
     * @param tableName 表名
     * @return
     */
    TableInfo buildTableInfo(String tableName);

    /**
     * 获取指定表信息
     *
     * @return
     */
    List<Map<String, Object>> getTableInfo(String tableName);

    /**
     * 获取当前schema下的所有表
     *
     * @return
     */
    List<Map<String, Object>> getTables();

    /**
     * 根据表名获取所有字段
     *
     * @param tableName
     * @return2
     */
    List<ColumnInfo> getColumns(String tableName);


    /**
     * 根据表名和获取所有字段名称（不包括表名）
     *
     * @param tableName
     * @return2
     */
    List<String> getColumnNames(String tableName, String datasource);


    /**
     * 获取所有可用表名
     *
     * @return2
     */
    List<String> getTableNames(String schema);

    /**
     * 获取所有可用表名
     *
     * @return2
     */
    List<String> getTableNames();

    /**
     * 通过查询sql获取columns
     *
     * @param querySql
     * @return
     */
    List<ColumnInfo> getColumnsByQuerySql(String querySql) throws SQLException;

    List<String> getColumnNamesByQuerySql(String querySql) throws SQLException;

    /**
     * 获取当前表maxId
     *
     * @param tableName
     * @param primaryKey
     * @return
     */
    long getMaxIdVal(String tableName, String primaryKey);

    /**
     * 获取当前字段的最大值
     *
     * @param tableName
     * @param field
     * @return
     */
    String getMaxStringVal(String tableName, String field);

    /**
     * 获取当前字段的最小值
     *
     * @param tableName
     * @param field
     * @return
     */
    String getMinStringVal(String tableName, String field);

    /**
     * 通过schema查询此库的所有表
     * @param schema
     * @return
     */
    List<TableDto> getTableINfoBySchema(String schema);

    /**
     * 根据schema查询对应的建表语句
     * @param schema
     * @return
     */
    String getCreateSqlBySchema(String schema);

}
