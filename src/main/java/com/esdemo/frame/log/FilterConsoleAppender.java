package com.esdemo.frame.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-09-12 14:26
 */
public class FilterConsoleAppender<E> extends ConsoleAppender<E> {

    @Override
    protected void subAppend(E event) {
        if (event instanceof LoggingEvent && FilterLogUtils.filterLoggingEvent((LoggingEvent) event)) {
            return;
        }
        super.subAppend(event);
    }
}
