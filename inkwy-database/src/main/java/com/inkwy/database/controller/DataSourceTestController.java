package com.inkwy.database.controller;

import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.domain.Response;
import com.inkwy.database.service.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据源测试
 * @author: pjp
 * @date: 2022/3/1 10:29
 */
@RestController
@Api(tags = "数据源测试", value = "DataSourceTestController")
@RequestMapping(value = "/api/dataSource/test")
@Slf4j
public class DataSourceTestController {
    @Resource
    private DataSourceService dataSourceService;

    /**
     * 数据源测试
     **/
    @PostMapping("/test")
    @ApiOperation("数据源测试")
    public Response dataSourceTest(@Validated @RequestBody @ApiParam("oracle例子：{\n" +
            "  \"password\": \"55555\",\n" +
            "  \"type\": 2,\n" +
            "  \"url\": \"jdbc:oracle:thin:@localhost:1521:xxx\",\n" +
            "  \"username\": \"root\"\n" +
            "} \n " +
            "mysql列子：{\n" +
            "  \"password\": \"55555\",\n" +
            "  \"type\": 1,\n" +
            "  \"url\": \"jdbc:mysql://localhost:3306/dc_data_exc\",\n" +
            "  \"username\": \"root\"\n" +
            "}") DataSource dataSource) {
        try {
            Map<String, Object> map = new HashMap<>();
            Boolean flag = dataSourceService.dataSourceTest(dataSource);
            if (flag) {
                List<String> tableSchema = dataSourceService.getTableSchema(dataSource);
                map.put("tableSchema", tableSchema);
            } else {
                map.put("tableSchema", new ArrayList<>());
            }
            map.put("connect", flag);
            return Response.success(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.respFail(e.getMessage());
        }
    }

}
