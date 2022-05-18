package com.inkwy.database.service.impl;

import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.datasource.query.BaseQueryTool;
import com.inkwy.database.datasource.query.QueryToolFactory;
import com.inkwy.database.dto.TableDto;
import com.inkwy.database.service.DataSourceService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class DataSourceServiceImpl implements DataSourceService {
    /**
     * 测试数据源是否能连接成功
     *
     * @param dataSource
     * @return
     * @throws IOException
     */
    @Override
    public Boolean dataSourceTest(DataSource dataSource){
        // 测试连接每次删除数据源cache
//        LocalCacheUtil.remove(dataSource.getName() + "_" + dataSource.getId());

        dataSource.setUsername(dataSource.getUsername());
        dataSource.setPassword(dataSource.getPassword());
        BaseQueryTool queryTool = QueryToolFactory.getByDbType(dataSource);
        return queryTool.dataSourceTest();
    }

    @Override
    public List<String> getTableSchema(DataSource dataSource) {
        BaseQueryTool qTool = QueryToolFactory.getByDbType(dataSource);
        List<String> tableSchemas = qTool.getTableSchema();
        // 当查询不到tableSchemas时默认
        tableSchemas = CollectionUtils.isEmpty(tableSchemas) ? Arrays.asList("database") : tableSchemas;
        return tableSchemas;
    }

    @Override
    public List<TableDto> getTableInfoBySchema(DataSource dataSource) {
        dataSource.setUsername(dataSource.getUsername());
        dataSource.setPassword(dataSource.getPassword());
        BaseQueryTool queryTool = QueryToolFactory.getByDbType(dataSource);
        return queryTool.getTableINfoBySchema(dataSource.getSchema());
    }

    @Override
    public String getCreateSqlBySchema(DataSource dataSource) {
        dataSource.setUsername(dataSource.getUsername());
        dataSource.setPassword(dataSource.getPassword());
        BaseQueryTool queryTool = QueryToolFactory.getByDbType(dataSource);
        return queryTool.getCreateSqlBySchema(dataSource.getSchema());
    }
}
