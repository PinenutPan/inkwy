package com.inkwy.database.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
/**
 * @description: 健康检查
 * @author: pjp
 * @date: 2022/5/20 9:28
 */
@RestController
@Api(tags = "健康检查", value = "HealthController")
public class HealthController {

    @GetMapping("/health")
    @ApiOperation("健康检查")
    public Map<String, String> health() {
        Map<String, String> map = new HashMap<>(1);
        map.put("status", "UP");
        return map;
    }
}
