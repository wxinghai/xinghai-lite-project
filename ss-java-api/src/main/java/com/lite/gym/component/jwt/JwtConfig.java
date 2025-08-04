package com.lite.gym.component.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * 密钥
     */
    private String secret = "lite-gym-secret";
    
    /**
     * 过期时间（秒）
     */
    private long expireTime = 86400; // 默认24小时
}
