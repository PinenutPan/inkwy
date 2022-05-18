package com.inkwy.database.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.inkwy.database.datasource.domain.DataSource;
import com.inkwy.database.dto.ColumnDto;
import com.inkwy.database.dto.TableDto;
import com.inkwy.database.service.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.util.List;

@RestController
@Api(tags = "下载数据源数据", value = "DownloadController")
@RequestMapping(value = "/api/dataSource/download")
@Slf4j
public class DownloadController {

    @Resource
    private DataSourceService dataSourceService;

    /**
     * 下载数据源的所有表信息
     **/
    @PostMapping("/tableInfo")
    @ApiOperation("下载数据源的所有表信息")
    public void dataSourceTest(HttpServletResponse response, @Validated @RequestBody @ApiParam("oracle例子：{\n" +
            "  \"password\": \"55555\",\n" +
            "  \"schema\": \"xxx\",\n" +
            "  \"type\": 2,\n" +
            "  \"url\": \"jdbc:oracle:thin:@localhost:1521:xxx\",\n" +
            "  \"username\": \"root\"\n" +
            "} \n " +
            "mysql列子：{\n" +
            "  \"password\": \"55555\",\n" +
            "  \"schema\": \"dc_data_exc\",\n" +
            "  \"type\": 1,\n" +
            "  \"url\": \"jdbc:mysql://localhost:3306/dc_data_exc\",\n" +
            "  \"username\": \"root\"\n" +
            "}") DataSource dataSource) {
        ServletOutputStream out = null;
        try {
            Assert.notNull(dataSource.getSchema(), "schema不能为空");
            List<TableDto> tableDtos = dataSourceService.getTableInfoBySchema(dataSource);
            // 通过工具类创建writer，默认创建xls格式
            ExcelWriter writer = ExcelUtil.getWriter();
            if (CollectionUtils.isNotEmpty(tableDtos)) {
                for (int i = 0; i < tableDtos.size(); i++) {
                    TableDto tableDto = tableDtos.get(i);
                    writer.merge(6, (StringUtils.isEmpty(tableDto.getTableComment()) ? "" : (tableDto.getTableComment() + ":")) + tableDto.getTableName());
                    //自定义标题别名
                    writer.addHeaderAlias("tableName", "表名");
                    writer.addHeaderAlias("columnName", "列名");
                    writer.addHeaderAlias("columnComment", "列名描述");
                    writer.addHeaderAlias("dataType", "数据类型");
                    writer.addHeaderAlias("columnType", "列长度");
                    writer.addHeaderAlias("columnDefault", "列默认值");
                    writer.addHeaderAlias("isNullable", "是否空值");
                    // 一次性写出内容，使用默认样式，强制输出标题
                    tableDto.getColumnDtoList().add(new ColumnDto());
                    writer.write(tableDto.getColumnDtoList(), true);

                }
            }
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //*_table_info.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", String.format("attachment;filename=%s_table_info.xls",dataSource.getSchema()));
            //out为OutputStream，需要写出到的目标流
            out = response.getOutputStream();
            writer.flush(out, true);
            // 关闭writer，释放内存
            writer.close();
            //此处记得关闭输出Servlet流
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IoUtil.close(out);
        }
    }

    /**
     * 下载对应schema的建表语句
     **/
    @PostMapping("/createSql")
    @ApiOperation("下载对应schema的建表语句")
    public void createSql(HttpServletResponse response,@Validated @RequestBody @ApiParam("oracle例子：{\n" +
            "  \"password\": \"55555\",\n" +
            "  \"schema\": \"xxx\",\n" +
            "  \"type\": 2,\n" +
            "  \"url\": \"jdbc:oracle:thin:@localhost:1521:xxx\",\n" +
            "  \"username\": \"root\"\n" +
            "} \n " +
            "mysql列子：{\n" +
            "  \"password\": \"55555\",\n" +
            "  \"schema\": \"dc_data_exc\",\n" +
            "  \"type\": 1,\n" +
            "  \"url\": \"jdbc:mysql://localhost:3306/dc_data_exc\",\n" +
            "  \"username\": \"root\"\n" +
            "}") DataSource dataSource) {
        BufferedOutputStream buff = null;
        ServletOutputStream out = null;
        try {
            Assert.notNull(dataSource.getSchema(), "schema不能为空");
            String createSql = dataSourceService.getCreateSqlBySchema(dataSource);

            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", String.format("attachment;filename=%s_create_sql.txt",dataSource.getSchema()));
            out = response.getOutputStream();
            buff = new BufferedOutputStream(out);
            buff.write(createSql.getBytes("UTF-8"));
            buff.flush();
            buff.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IoUtil.close(buff);
            IoUtil.close(out);
        }
    }

}
