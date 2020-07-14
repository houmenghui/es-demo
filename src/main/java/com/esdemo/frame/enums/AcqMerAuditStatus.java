package com.esdemo.frame.enums;

import com.esdemo.frame.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：收单商户审核状态
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum AcqMerAuditStatus {

    IN_AUDIT("1", "审核中"),
    AUDIT_SUCC("2", "审核通过"),
    AUDIT_FAIL("3", "审核不通过"),
    INVALID("4", "已失效"),
    UNKNOW("0", "未知");

    private String status;
    private String statusZh;

    public static AcqMerAuditStatus getByStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return UNKNOW;
        }
        AcqMerAuditStatus[] values = AcqMerAuditStatus.values();
        for (AcqMerAuditStatus value : values) {
            if (status.equals(value.getStatus())) {
                return value;
            }
        }
        return UNKNOW;
    }
}