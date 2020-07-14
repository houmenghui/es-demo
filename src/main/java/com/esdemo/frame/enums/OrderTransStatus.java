package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.esdemo.frame.utils.StringUtils;

/**
 * @Title：agentApi2
 * @Description：订单交易状态
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum OrderTransStatus {

    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    INIT("INIT", "初始化"),
    REVERSED("REVERSED", "已冲正"),
    REVOKED("REVOKED", "已撤销"),
    SETTLE("SETTLE", "已结算"),
    OVERLIMIT("OVERLIMIT", "超限"),
    REFUND("REFUND", "已退款"),
    SENDORDER("SENDORDER", "已提交"),
    COMPLETE("COMPLETE", "已完成"),
    CLOSED("CLOSED", "关闭");

    private String status;
    private String statusZh;

    public static String getZhByStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return "";
        }
        OrderTransStatus[] values = OrderTransStatus.values();
        for (OrderTransStatus value : values) {
            if (status.equals(value.getStatus())) {
                return value.getStatusZh();
            }
        }
        return status;
    }
}