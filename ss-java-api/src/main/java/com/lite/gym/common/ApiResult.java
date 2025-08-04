package com.lite.gym.common;

import lombok.Data;

/**
 * API统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
public class ApiResult<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功返回结果
     *
     * @param <T> 数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param <T>       数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> failed(ResultCode errorCode) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(false);
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMessage());
        return result;
    }

    /**
     * 失败返回结果
     *
     * @param message 错误信息
     * @param <T>     数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> failed(String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(false);
        result.setCode(ResultCode.FAILED.getCode());
        result.setMessage(message);
        return result;
    }
}
