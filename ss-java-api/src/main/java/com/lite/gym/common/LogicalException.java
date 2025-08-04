package com.lite.gym.common;

/**
 * 业务逻辑异常
 */
public class LogicalException extends RuntimeException {

    /**
     * 错误码
     */
    private int code = ResultCode.FAILED.getCode();

    /**
     * 默认构造函数
     */
    public LogicalException() {
        super();
    }

    /**
     * 带消息的构造函数
     *
     * @param message 错误消息
     */
    public LogicalException(String message) {
        super(message);
    }

    /**
     * 带错误码和消息的构造函数
     *
     * @param message 错误消息
     * @param code    错误码
     */
    public LogicalException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}
