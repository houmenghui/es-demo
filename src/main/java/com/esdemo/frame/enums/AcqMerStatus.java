package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否特约商户标识
 */
@AllArgsConstructor
@Getter
public enum AcqMerStatus {
    ACQ_MER_SUCCESS("1"), //显示特约商户
    ACQ_AUDIT_FAIL("2"),//特约商户审核不通过
    ACQ_IN_AUDIT("3"),//特约商户审核中
    ACQ_INVALID("4");//特约商户失效

    private String status;
}