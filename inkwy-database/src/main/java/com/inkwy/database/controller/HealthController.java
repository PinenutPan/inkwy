package com.inkwy.database.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> map = new HashMap<>(1);
        map.put("status", "UP");
        return map;
    }
}
