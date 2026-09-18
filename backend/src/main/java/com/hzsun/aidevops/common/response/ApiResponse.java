package com.hzsun.aidevops.common.response;

import lombok.Getter;
import lombok.Setter;

/**
 * HTTP 200 统一响应结构。
 *
 * <p>仅承载 {code, message, data} 三个字段：成功时 code=200、message 固定为“请求成功”；
 * 失败时 data 必须为 null。非 200 的 HTTP 响应不返回响应体。</p>
 *
 * @param <T> 业务数据类型
 */
@Getter
@Setter
public class ApiResponse<T> {

    /** 成功状态码。 */
    public static final int CODE_SUCCESS = 200;

    /** 通用失败状态码。 */
    public static final int CODE_FAILURE = 500;

    /** 成功文案。 */
    private static final String MESSAGE_SUCCESS = "请求成功";

    /** 通用失败文案。 */
    private static final String MESSAGE_FAILURE = "请求失败，请稍后重试";

    /** 数字型状态码。 */
    private Integer code;

    /** 可直接展示给终端用户的文案。 */
    private String message;

    /** 业务数据，失败时为 null。 */
    private T data;

    /**
     * 构造成功响应。
     *
     * @param data 业务数据，允许为 null
     * @param <T>  业务数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(CODE_SUCCESS);
        response.setMessage(MESSAGE_SUCCESS);
        response.setData(data);
        return response;
    }

    /**
     * 构造失败响应。
     *
     * @param code    错误码
     * @param message 可展示文案，为空时回退为通用失败文案
     * @param <T>     业务数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> failure(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message == null || message.isBlank() ? MESSAGE_FAILURE : message);
        response.setData(null);
        return response;
    }
}
