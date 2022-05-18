package com.inkwy.database.service;

import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.dto.TableDto;

import java.util.List;

/**
 *
 * 数据源测试DataSourceService
 *
 **/

public interface DataSourceService{
    /**
     * 测试数据源是否正确
     * @param dataSource
     * @return
     */
    Boolean dataSourceTest(DataSource dataSource);

    /**
     * 查询数据的库
     * @param dataSource
     * @return
     */
    List<String> getTableSchema(DataSource dataSource);

    /**
     * 根据schema查询对应的表信息
     * @param dataSource
     * @return
     */
    List<TableDto> getTableInfoBySchema(DataSource dataSource);

    /**
     * 根据schema查询对应的建表语句
     * @param dataSource
     * @return
     */
    String getCreateSqlBySchema(DataSource dataSource);
}
