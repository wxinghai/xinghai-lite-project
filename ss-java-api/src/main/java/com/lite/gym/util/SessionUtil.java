package com.lite.gym.util;

import com.lite.gym.common.LogicalException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 会话工具类
 */
public class SessionUtil {
    
    private static final String USER_ID_ATTRIBUTE = "userId";
    
    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new LogicalException("未获取到请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String userIdStr = (String) request.getAttribute(USER_ID_ATTRIBUTE);
        
        if (!StringUtils.hasText(userIdStr)) {
            throw new LogicalException("未获取到当前登录用户信息");
        }
        
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new LogicalException("用户ID格式错误");
        }
    }
    
    /**
     * 设置当前登录用户ID
     *
     * @param userId 用户ID
     */
    public static void setCurrentUserId(Long userId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute(USER_ID_ATTRIBUTE, userId);
        }
    }
}
