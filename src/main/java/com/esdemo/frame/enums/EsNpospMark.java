package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：ES常用符号
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum EsNpospMark {

    GTE("gte"),
    LTE("lte"),
    DESC("desc"),
    ASC("asc");

    private String fieldName;
}
