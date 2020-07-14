package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否特约商户标识
 */
@AllArgsConstructor
@Getter
public enum SpecialMerFlag {
    YES("1"),
    NOT("0");

    private String flag;

}