package com.lite.gym.config;

import com.lite.gym.component.jwt.AuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * MVC配置
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册认证拦截器");
        // 拦截需要认证的接口，排除登录和注册接口
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login", 
                        "/register",
                        "/health"
                );
    }

    @Bean
    public CorsFilter corsFilter() {
        log.info("配置CORS过滤器");
        CorsConfiguration config = new CorsConfiguration();
        // 允许的来源列表，区分本地开发和生产环境
        config.setAllowedOriginPatterns(Arrays.asList(
                // 本地开发环境
                "http://localhost:*",
                "http://127.0.0.1:*",
                // 生产环境域名 - 根据实际情况修改
                "https://your-production-domain.com"
        ));
        // 允许的HTTP请求方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        // 暴露的响应头
        config.setExposedHeaders(Arrays.asList("Authorization", "token", "Content-Type"));
        // 允许发送Cookie
        config.setAllowCredentials(true);
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);
        
        // 配置CORS映射路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        // 返回CORS过滤器
        return new CorsFilter(source);
    }
}
