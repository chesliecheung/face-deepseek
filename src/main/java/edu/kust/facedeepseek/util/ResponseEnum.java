package edu.kust.facedeepseek.util;


/**
 * 全局响应状态枚举（统一管理响应码和提示消息，避免硬编码混乱）
 */
public enum ResponseEnum {
    // 通用状态（所有接口都可能用到）
    SUCCESS(200, "操作成功"),          // 成功
    PARAM_ERROR(400, "参数错误"),       // 参数为空/格式错误
    SERVER_ERROR(500, "服务器处理失败"); // 服务层/API调用异常

    // 可根据业务补充（如图片生成相关：IMAGE_GENERATE_FAIL(5001, "图片生成失败")）
    // IMAGE_GENERATE_FAIL(5001, "图片生成失败"),
    // IMAGE_URL_EMPTY(5002, "生成的图片URL为空");

    // 响应码（遵循HTTP语义：200成功，4xx客户端错，5xx服务端错）
    private final int code;
    // 响应提示消息
    private final String msg;

    // 枚举构造器（默认private，无需手动写）
    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // Getter方法（枚举值不可修改，无需Setter）
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}