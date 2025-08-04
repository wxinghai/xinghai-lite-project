package com.lite.gym.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 登录请求DTO
 */
@Data
@ToString
public class LoginRequest {
    
    /**
     * 用户名或邮箱
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
} 