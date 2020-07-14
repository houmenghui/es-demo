package com.esdemo.frame.utils.es;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-23 17:54
 */
@Slf4j
public class EsLog {

    public static void info(String message, SearchQuery searchQuery) {
        String queryStr = String.join("",
                Arrays.asList(Optional.ofNullable(searchQuery.getQuery()).map(Object::toString).orElse(""),
                        Optional.ofNullable(searchQuery.getFilter()).map(Object::toString).orElse("")))
                .replaceAll(" : ", ":")
                .replaceAll("\n *", "");
        log.info("{} 查询的索引 {}; 分页信息 {} ; 排序 {}", message, searchQuery.getIndices(), searchQuery.getPageable(), searchQuery.getSort());
        log.info("{} 查询条件 {}", message, queryStr);
        log.info("{} 汇总条件 {}", message, searchQuery.getAggregations());
    }
}
