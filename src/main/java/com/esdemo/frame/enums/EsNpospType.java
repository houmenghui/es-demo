package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-14 08:38
 */
@AllArgsConstructor
@Getter
public enum EsNpospType {
    AGENT("agent"),
    MERCHANT("merchant"),
    ORDER("order"),
    MBP("mbp"),
    BP("bp");

    private String typeName;

}