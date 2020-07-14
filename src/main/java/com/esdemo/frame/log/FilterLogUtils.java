package com.esdemo.frame.log;

import ch.qos.logback.classic.spi.LoggingEvent;

import java.lang.reflect.Field;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-09-16 08:52
 */
public final class FilterLogUtils {
    private FilterLogUtils() {}

    public static boolean filterLoggingEvent(LoggingEvent e) {
        String formattedMessage = translate(e.getFormattedMessage());
        if (formattedMessage.toLowerCase().matches(".*?(cvn2|icmsg|trackmsg|trackmsgone|trackmsgtwo|password|pwd|newPwd|oldPwd|trackmsgthree|cvv|cvn|paypwd).*?")) {
            return true;
        }
        try {
            Field argumentArray = LoggingEvent.class.getDeclaredField("argumentArray");
            argumentArray.setAccessible(true);
            argumentArray.set(e, null);

            Field message = LoggingEvent.class.getDeclaredField("message");
            message.setAccessible(true);
            message.set(e, formattedMessage);

            Field formattedMessageField = LoggingEvent.class.getDeclaredField("formattedMessage");
            formattedMessageField.setAccessible(true);
            formattedMessageField.set(e, formattedMessage);
        } catch (Exception ignored) {}
        return false;
    }


    public static String translate(String message) {

        return message
                // 邮箱
                .replaceAll("([a-zA-Z0-9_-]{3})[a-zA-Z0-9_-]+@([a-zA-Z0-9_-]+(?:\\.[a-zA-Z0-9_-]+)+)(?!\\d)", "$1***@$2")
                // 手机号码
                .replaceAll("(?<!\\d)(1\\d{2})(\\d{4})(\\d{4})(?!\\d)", "$1****$3")
                // 15位身份证 xxxxxx    yy MM dd   75 0     十五位
                .replaceAll("(?<!\\d)([1-9]\\d{5})\\d{2}(?:(?:0[1-9])|(?:10|11|12))(?:(?:[0-2][1-9])|10|20|30|31)(\\d{3})(?!\\d)", "$1******$2")
                // 18位身份证 xxxxxx yyyy MM dd 375 0     十八位
                .replaceAll("(?<!\\d)([1-9]\\d{5})(?:18|19|(?:[23]\\d))\\d{2}(?:(?:0[1-9])|(?:10|11|12))(?:(?:[0-2][1-9])|10|20|30|31)(\\d{3}[0-9Xx])(?!\\d)", "$1********$2")
                // 银行卡号15 ~ 19 位
                .replaceAll("(?<!\\d)(622155|622576|622252|625996|622689|622253|625965|622575|625958|498451|622848|622623|622658|625808|625969|625247|625809|625976|621700|622688)\\d{4}(\\d{5,9})(?!\\d)", "$1****$2");
    }
}
