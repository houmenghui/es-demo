package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.esdemo.frame.utils.StringUtils;

/**
 * @Title：agentApi2
 * @Description：订单交易类型
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum CardType {

    DEBIT("1", "贷记卡"),
    CREDIT("2", "借记卡"),
    UNKNOW("3", "未知");

    private String type;
    private String typeZh;

    public static String getZhByType(String type) {
        if (StringUtils.isBlank(type)) {
            return "";
        }
        CardType[] values = CardType.values();
        for (CardType value : values) {
            if (type.equals(value.getType())) {
                return value.getTypeZh();
            }
        }
        return type;
    }
}