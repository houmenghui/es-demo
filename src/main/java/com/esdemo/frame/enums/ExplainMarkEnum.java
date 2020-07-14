package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问号提示语
 */
@AllArgsConstructor
@Getter
public enum ExplainMarkEnum {

    INDEX_CURR_DAY_MARK("1. 收入包含本级所有的收入，包含已下发和未下发的金额\n" +
            "2. 今日业绩中的其它数据均包含本级及所有下级的数据", "我知道了"), //今日业绩问号说明
    SIX_MONTH_ORDER_MARK("近6月交易量和交易笔数为不包含\n当天往前推180天的数据。", "我知道了"); //数据-近6月交易量问号说明
    private String context;
    private String buttonText;
}
