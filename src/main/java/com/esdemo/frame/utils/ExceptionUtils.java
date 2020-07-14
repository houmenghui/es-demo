package com.esdemo.frame.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtils {
    /**
     * 打印错误堆栈
     */
    public static final String collectExceptionStackMsg(Throwable throwable) {
        try {
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw, true));
            return sw.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
