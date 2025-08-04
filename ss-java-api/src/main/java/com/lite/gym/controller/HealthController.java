package com.lite.gym.controller;

import com.lite.gym.common.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Slf4j
@RestController
public class HealthController {
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResult<Map<String, Object>> health() {
        log.info("健康检查API调用");
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("message", "服务正常运行中");
        info.put("timestamp", System.currentTimeMillis());
        return ApiResult.success(info);
    }
} 