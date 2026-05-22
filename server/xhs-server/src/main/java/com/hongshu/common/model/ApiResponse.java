package com.hongshu.common.model;

/**
 * 统一响应结果
 */
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public ApiResponse() {}

    public ApiResponse(int code, String message, T data, long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public int getCode() { return code; } public void setCode(int c) { this.code = c; }
    public String getMessage() { return message; } public void setMessage(String m) { this.message = m; }
    public T getData() { return data; } public void setData(T d) { this.data = d; }
    public long getTimestamp() { return timestamp; } public void setTimestamp(long t) { this.timestamp = t; }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return fail(500, message);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return fail(400, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return fail(401, message);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return fail(403, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return fail(404, message);
    }
}
