package com.inkwy.database.controller;

import com.inkwy.database.domain.Response;
import com.inkwy.database.param.DatabaseParam;
import com.inkwy.database.service.DatabaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jianpingpan
 * @className DatabaseController
 * @description 比较两个数据库的表信息
 * @date 2018/12/21 13:18
 */
@RestController
@Api(tags = "比较两个数据库的表信息", value = "DatabaseController")
@RequestMapping(value = "/api/database/compare")
@Slf4j
public class CompareController {

    @Resource
    private DatabaseService databaseService;

    @ApiOperation(value = "比较两个库表差异")
    @PostMapping("/datasource")
    public Response compare(@ApiParam(name = "databaseParam",
            value = "示例：type:1 mysql; 2 oracle；sourceUrl——jdbc:mysql://localhost:3306；username-root；password-root；schema——dc_user \n" +
                    "{\n" +
                    "  \"sourcePassword\": \"root\",\n" +
                    "  \"sourceSchema\": \"dc_sidecar_manager\",\n" +
                    "  \"sourceUrl\": \"jdbc:mysql://localhost:3306\",\n" +
                    "  \"sourceUsername\": \"root\",\n" +
                    "  \"targetPassword\": \"55555\",\n" +
                    "  \"targetSchema\": \"dc_sidecar_manager\",\n" +
                    "  \"targetUrl\": \"jdbc:mysql://localhost:3306\",\n" +
                    "  \"targetUsername\": \"root\"\n" +
                    "}")
                            @RequestBody DatabaseParam databaseParam) {
        try {
            Assert.notNull(databaseParam, "参数错误");
            Assert.notNull(databaseParam.getSourceUrl(), "源数据库地址不能为空");
            Assert.isTrue(databaseParam.getSourceUrl().startsWith("jdbc:mysql://"), "源数据库地址格式错误，请参考示例");
            Assert.notNull(databaseParam.getSourceUsername(), "源数据用户名不能为空");
            Assert.notNull(databaseParam.getSourcePassword(), "源数据密码不能为空");
            Assert.notNull(databaseParam.getTargetUrl(), "目标数据库地址不能为空");
            Assert.isTrue(databaseParam.getTargetUrl().startsWith("jdbc:mysql://"), "目标数据库地址格式错误，请参考示例");
            Assert.notNull(databaseParam.getTargetUsername(), "目标数据库用户名不能为空");
            Assert.notNull(databaseParam.getTargetPassword(), "目标数据库密码不能为空");
            if (StringUtils.isNotBlank(databaseParam.getTargetSchema())){
                Assert.notNull(databaseParam.getSourceSchema(),"目标端选了schema,源端也必须选schema");
                return Response.respSuccess(databaseService.compareBySchema(databaseParam));
            }else {
                return Response.respSuccess(databaseService.compare(databaseParam));
            }
        } catch (IllegalArgumentException e) {
            return Response.respFail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Response.respFail(e.getMessage());
        }
    }

    @ApiOperation(value = "已知schema比较两个数据库表差异",hidden = true)
    @PostMapping("/schema")
    public Response compareSchema(@ApiParam(name = "databaseParam",
            value = "示例：url-jdbc:mysql://cherriesmall-test.mysql.rds.aliyuncs.com:3306；username-root；password-root；schema-dc_user")
                            @RequestBody DatabaseParam databaseParam) {
        try {
            Assert.notNull(databaseParam, "参数错误");
            Assert.notNull(databaseParam.getSourceSchema(), "源数据库库名不能为空");
            Assert.notNull(databaseParam.getSourceUrl(), "源数据库地址不能为空");
            Assert.isTrue(databaseParam.getSourceUrl().startsWith("jdbc:mysql://"), "源数据库地址格式错误，请参考示例");
            Assert.notNull(databaseParam.getSourceUsername(), "源数据用户名不能为空");
            Assert.notNull(databaseParam.getSourcePassword(), "源数据密码不能为空");
            Assert.notNull(databaseParam.getTargetUrl(), "目标数据库地址不能为空");
            Assert.notNull(databaseParam.getTargetSchema(), "目标数据库库名不能为空");
            Assert.isTrue(databaseParam.getTargetUrl().startsWith("jdbc:mysql://"), "目标数据库地址格式错误，请参考示例");
            Assert.notNull(databaseParam.getTargetUsername(), "目标数据库用户名不能为空");
            Assert.notNull(databaseParam.getTargetPassword(), "目标数据库密码不能为空");
            return Response.respSuccess(databaseService.compareBySchema(databaseParam));
        } catch (IllegalArgumentException e) {
            return Response.respFail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Response.respFail("系统开小差请稍后重试");
        }
    }

}
