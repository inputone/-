package com.huanghaha.treehole.handler;

import com.huanghaha.treehole.common.BusinessException;
import com.huanghaha.treehole.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 区分处理三类异常：
 * - BusinessException：业务异常，warn 级别记录，返回具体错误信息
 * - RuntimeException：运行时异常，error 级别记录（含堆栈），返回具体错误信息
 * - Exception：未知系统异常，error 级别记录（含堆栈），返回通用提示
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** 处理业务异常 */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /** 处理运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /** 处理未知系统异常，隐藏内部细节 */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
