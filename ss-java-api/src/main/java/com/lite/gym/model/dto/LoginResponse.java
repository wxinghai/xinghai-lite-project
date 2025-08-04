package com.lite.gym.model.dto;

import com.lite.gym.model.User;
import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    
    /**
     * 用户信息
     */
    private User user;
    
    /**
     * 访问令牌
     */
    private String token;
} 