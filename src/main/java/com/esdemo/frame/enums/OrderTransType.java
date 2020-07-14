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
public enum OrderTransType {

    PURCHASE("PURCHASE", "消费"),
    REVERSED("REVERSED", "冲正"),
    PURCHASE_VOID("PURCHASE_VOID", "消费撤销"),
    PRE_AUTH("PRE_AUTH", "预授权"),
    PRE_AUTH_VOID("PRE_AUTH_VOID", "预授权撤销"),
    PRE_AUTH_COMPLETA("PRE_AUTH_COMPLETA", "预授权完成"),
    PRE_AUTH_COMPLETE_VOID("PRE_AUTH_COMPLETE_VOID", "预授权完成撤销"),
    PURCHASE_REFUND("PURCHASE_REFUND", "退货"),
    BALANCE_QUERY("BALANCE_QUERY", "查余额"),
    TRANSFER_ACCOUNTS("TRANSFER_ACCOUNTS", "转账");

    private String type;
    private String typeZh;

    public static String getZhByType(String type) {
        if (StringUtils.isBlank(type)) {
            return "";
        }
        OrderTransType[] values = OrderTransType.values();
        for (OrderTransType value : values) {
            if (type.equals(value.getType())) {
                return value.getTypeZh();
            }
        }
        return type;
    }
}