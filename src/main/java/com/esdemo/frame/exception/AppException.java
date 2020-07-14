package com.esdemo.frame.exception;

import lombok.Getter;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:16
 */
@Getter
public class AppException extends RuntimeException {
    private final int code;

    public AppException(String message) {
        this(400, message);
    }

    public AppException(int code, String message) {
        super(message);
        this.code = code;
    }
}
