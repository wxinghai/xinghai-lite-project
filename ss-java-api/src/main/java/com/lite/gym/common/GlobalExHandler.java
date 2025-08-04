package com.lite.gym.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExHandler {

    /**
     * 处理业务逻辑异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(LogicalException.class)
    public ApiResult<String> handleLogicalException(LogicalException e) {
        log.error("业务逻辑异常：{}", e.getMessage());
        return ApiResult.failed(e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResult<String> handleValidException(Exception e) {
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        
        String message = "参数错误";
        if (bindingResult != null && bindingResult.hasErrors()) {
            message = bindingResult.getFieldError().getDefaultMessage();
        }
        
        log.error("参数校验异常：{}", message);
        return ApiResult.failed(message);
    }

    /**
     * 处理其他异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public ApiResult<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return ApiResult.failed("系统繁忙，请稍后再试");
    }
}
