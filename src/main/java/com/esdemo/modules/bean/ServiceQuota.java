package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 14:15
 */
@Data
public class ServiceQuota {
    private Long id;

    private Long serviceId;

    private String serviceName;

    private String holidaysMark;

    private String cardType;

    private String quotaLevel;

    private String agentNo;

    private BigDecimal singleDayAmount;

    private BigDecimal serviceManageQuotacol;

    private BigDecimal singleCountAmount;

    private BigDecimal singleMinAmount;

    private BigDecimal singleDaycardAmount;

    private Integer singleDaycardCount;

    private String checkStatus;

    private String lockStatus;

    private Integer isGlobal;
    private int fixedQuota;
}
