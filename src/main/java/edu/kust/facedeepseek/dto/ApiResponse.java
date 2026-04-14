package edu.kust.facedeepseek.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> ok(T data){
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> fail(String msg){
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.msg = msg;
        return r;
    }
}
