package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * nposp索引join type类型
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-14 08:38
 */
@AllArgsConstructor
@Getter
public enum EsNpospJoinType {
    AGENT("agent"),             // 代理商
    MERCHANT("merchant"),       // 商户
    ORDER("order"),             // 订单
//        ACTIVITY("activity"),       // 商户欢乐返活动
    MBP("mbp");                 // 商户进件

    private String typeName;

}