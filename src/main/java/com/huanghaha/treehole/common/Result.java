package com.huanghaha.treehole.common;

import lombok.Data;

/**
 * 统一返回结果封装
 * 所有接口均返回此格式，code=200 表示成功，code=500 表示失败
 *
 * @param <T> 返回数据类型
 */
@Data
public class Result<T> {
    /** 状态码：200成功，500失败 */
    private Integer code;
    /** 提示信息 */
    private String msg;
    /** 返回数据 */
    private T data;

    /** 成功（无数据） */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        return result;
    }

    /** 成功（有数据） */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /** 失败 */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}