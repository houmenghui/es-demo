package com.esdemo.frame.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 14:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBean<T> {
    private int code;
    private String message;
    private T data;
    private long count;
    private boolean success;

    public static ResponseBean success() {
        return success(null, 0, "");
    }

    public static ResponseBean success(Object data) {
        return success(data, 0, "");
    }

    public static ResponseBean success(Object data, long count) {
        return success(data, count, "");
    }

    public static ResponseBean success(Object data, String message) {
        return success(data, 0, message);
    }

    public static ResponseBean success(Object data, long count, String message) {
        return new ResponseBean(200, message, data, count, true);
    }

    public static ResponseBean error(int code, String message, Object data) {
        return new ResponseBean(code, message, data, 0, false);
    }
    public static ResponseBean error(int code, String message) {
        return error(code, message, null);
    }

    public static ResponseBean error(String message) {
        return error(400, message, null);
    }
    public static ResponseBean error(String message, Object data) {
        return error(400, message, data);
    }

    public static ResponseBean of(boolean success, String message, Object data) {
        return success ? success(data, message) : error(message, data);
    }

    public static ResponseBean of(boolean success, String message) {
        return success ? success(null, message) : error(message);
    }

    public static ResponseBean of(boolean success) {
        return of(success, "");
    }
}
