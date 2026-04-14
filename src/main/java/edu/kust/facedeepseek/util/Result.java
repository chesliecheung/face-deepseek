package edu.kust.facedeepseek.util;

import java.io.Serializable;

/**
 * 通用返回结果包装类
 */
public class Result<T> implements Serializable {
    private boolean success;
    private String msg;
    private T data;

    // 构造函数私有化，避免直接 new
    private Result(boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    // ======= 工厂方法 =======

    // 成功（带消息和数据）
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(true, msg, data);
    }

    // 成功（带数据，默认提示）
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data);
    }

    // 成功（只带消息）
    public static <T> Result<T> success(String msg) {
        return new Result<>(true, msg, null);
    }

    // 成功（无参）
    public static <T> Result<T> success() {
        return new Result<>(true, "操作成功", null);
    }

    // 失败（带消息）
    public static <T> Result<T> error(String msg) {
        return new Result<>(false, msg, null);
    }

    // 失败（带消息和数据）
    public static <T> Result<T> error(String msg, T data) {
        return new Result<>(false, msg, data);
    }


    // 在 Result.java 里加上：
    public static <T> Result<T> fail(String msg) {
        return new Result<>(false, msg, null);
    }



    // ======= Getter =======
    public boolean isSuccess() { return success; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
}
