package com.huanghaha.treehole.exception;

/**
 * 业务异常
 * 用于业务校验失败时抛出（如参数不合法、权限不足等），
 * 由 GlobalExceptionHandler 统一捕获并以 warn 级别记录日志
 */
public class BusinessException extends RuntimeException {

    /** 错误状态码，默认 500 */
    private final Integer code;

    /**
     * 使用默认状态码 500 构造
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 指定状态码构造
     *
     * @param code    错误状态码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
