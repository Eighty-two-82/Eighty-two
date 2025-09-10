package com.careapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthController {

    // 根路径，打开 Heroku 域名时不会 404
    @GetMapping
    public String index() {
        return "CareApp Backend is running!";
    }

    // 健康检查接口，给前端/监控调用
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "careapp-backend");
        status.put("timestamp", Instant.now().toString());
        return status;
    }
}
