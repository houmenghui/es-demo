package com.esdemo.frame.db;

import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 切换读/写模式
 * 利用ThreadLocal保存当前线程是否处于哪种模式
 */
@Slf4j
public class DataSourceContextHolder {

    private DataSourceContextHolder(){}

    private static final ThreadLocal<Deque<String>> local = new ThreadLocal<>();

    public static ThreadLocal<Deque<String>> getLocal() {
        return local;
    }

    public static void config(DataSourceType dataSourceType) {
        Deque<String> queue = local.get();
        if (queue == null) {
            queue = new LinkedBlockingDeque<>();
            local.set(queue);
        }
        queue.addLast(dataSourceType.getType());
        log.info("切换到{}...", dataSourceType.getName());
    }

    //清除local中的值，用于数据源切换失败的问题
    public static void clear() {
        Deque<String> queue = local.get();
        if (queue != null && !queue.isEmpty()) {
            queue.removeLast();
            if (queue.isEmpty()) {
                local.set(null);
            }
        }
    }

    public static String getJdbcType() {
        Deque<String> queue = local.get();
        return queue != null ? queue.peekLast() : null;
    }
}