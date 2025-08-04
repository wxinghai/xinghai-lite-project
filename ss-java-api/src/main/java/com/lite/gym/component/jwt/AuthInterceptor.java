package com.lite.gym.component.jwt;

import com.lite.gym.common.LogicalException;
import com.lite.gym.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtComponent jwtComponent;
    
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录所有请求头，用于调试
        log.info("请求路径: {} {}", request.getMethod(), request.getRequestURI());
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            List<String> headerList = Collections.list(headerNames);
            log.info("所有请求头: {}", headerList);
            for (String name : headerList) {
                log.info("Header - {}: {}", name, request.getHeader(name));
            }
        }
        
        // 获取请求头中的Token
        String token = request.getHeader(HEADER_AUTHORIZATION);
        log.info("接收到的Authorization请求头: {}", token);
        
        // 如果是OPTIONS请求，直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            log.info("OPTIONS预检请求，直接放行");
            return true;
        }
        
        // 检查Token是否存在
        if (!StringUtils.hasText(token)) {
            // 尝试从其他位置获取token
            token = request.getParameter("token");
            log.info("尝试从参数中获取token: {}", token);
            
            if (!StringUtils.hasText(token)) {
                throw new LogicalException("未提供访问令牌");
            }
        }
        
        // 移除Bearer前缀
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }
        
        try {
            Long userId = Long.valueOf(jwtComponent.parseToken(token));
            log.info("JWT解析成功，用户ID: {}", userId);
            
            // 设置用户ID到请求上下文
            SessionUtil.setCurrentUserId(userId);
            
            return true;
        } catch (Exception e) {
            log.error("Token验证失败", e);
            throw new LogicalException("访问令牌无效或已过期");
        }
    }
}
