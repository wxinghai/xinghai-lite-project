package com.lite.gym.common;

import lombok.Getter;

/**
 * 返回状态码
 */
@Getter
public enum ResultCode {
    
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 操作失败
     */
    FAILED(500, "操作失败"),
    
    /**
     * 参数校验失败
     */
    VALIDATE_FAILED(400, "参数校验失败"),
    
    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证或认证已过期"),
    
    /**
     * 未授权
     */
    FORBIDDEN(403, "无访问权限");
    
    /**
     * 状态码
     */
    private final int code;
    
    /**
     * 提示信息
     */
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}