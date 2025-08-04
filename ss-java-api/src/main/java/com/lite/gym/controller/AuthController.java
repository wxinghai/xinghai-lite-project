package com.lite.gym.controller;

import com.lite.gym.common.ApiResult;
import com.lite.gym.common.LogicalException;
import com.lite.gym.component.jwt.JwtComponent;
import com.lite.gym.model.User;
import com.lite.gym.model.dto.LoginRequest;
import com.lite.gym.service.UserService;
import com.lite.gym.util.CodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtComponent jwtComponent;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        log.debug("登录请求参数: {}", loginRequest);
        
        if (!StringUtils.hasText(loginRequest.getUsername()) || !StringUtils.hasText(loginRequest.getPassword())) {
            throw new LogicalException("用户名和密码不能为空");
        }
        
        // 尝试通过用户名查找用户
        User user = userService.findByUsername(loginRequest.getUsername());
        
        // 如果找不到，尝试通过邮箱查找
        if (user == null) {
            user = userService.findByEmail(loginRequest.getUsername());
        }
        
        // 用户不存在
        if (user == null) {
            throw new LogicalException("用户名或密码错误");
        }
        
        // 验证密码
        if (!loginRequest.getPassword().equals(user.getHashedPassword())) {
            throw new LogicalException("用户名或密码错误");
        }
        
        // 检查用户是否激活
        if (!user.getIsActive()) {
            throw new LogicalException("账号已被禁用");
        }
        
        // 生成JWT令牌
        String token = jwtComponent.createToken(user.getId().toString());
        
        // 隐藏敏感字段
        user.setHashedPassword(null);
        
        // 构建响应 - 适配前端格式
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", token);
        response.put("user", user);
        
        return ApiResult.success(response);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResult<Map<String, Object>> register(@RequestBody User user) {
        log.debug("注册请求参数: {}", user);
        
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getHashedPassword())) {
            throw new LogicalException("用户名和密码不能为空");
        }
        
        if (!StringUtils.hasText(user.getEmail())) {
            throw new LogicalException("邮箱不能为空");
        }
        
        // 注册用户
        User registeredUser = userService.register(user);
        
        // 生成JWT令牌
        String token = jwtComponent.createToken(registeredUser.getId().toString());
        
        // 隐藏敏感字段
        registeredUser.setHashedPassword(null);
        
        // 构建响应 - 适配前端格式
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", token);
        response.put("user", registeredUser);
        
        return ApiResult.success(response);
    }
} 