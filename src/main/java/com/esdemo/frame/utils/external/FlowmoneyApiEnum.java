package com.esdemo.frame.utils.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlowmoneyApiEnum {

    NOW_TRANSFER("/flowmoney/transfer/nowTransfer");

    private String path;
}