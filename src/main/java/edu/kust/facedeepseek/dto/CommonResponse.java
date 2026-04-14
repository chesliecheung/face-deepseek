package edu.kust.facedeepseek.dto;

import lombok.Data;

@Data
public class CommonResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(200);
        response.setMsg("操作成功");
        response.setData(data);
        return response;
    }

    public static <T> CommonResponse<T> fail(String msg) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(500);
        response.setMsg(msg);
        return response;
    }
}
    