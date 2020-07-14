package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 14:12
 */
@Data
public class ServiceRate {
    private Long id;

    private Long serviceId;

    private String serviceName;

    private String serviceTypeName;

    private String holidaysMark;

    private String cardType;

    private String quotaLevel;

    private String agentNo;

    private String rateType;//费率类型

    private BigDecimal singleNumAmount;

    private BigDecimal rate;

    private BigDecimal capping;

    private BigDecimal safeLine;

    private Integer isGlobal;

    private String checkStatus;

    private String lockStatus;
    private BigDecimal ladder1Rate;
    private BigDecimal ladder1Max;
    private BigDecimal ladder2Rate;
    private BigDecimal ladder2Max;
    private BigDecimal ladder3Rate;
    private BigDecimal ladder3Max;
    private BigDecimal ladder4Rate;
    private BigDecimal ladder4Max;

    private String merRate;//商户费率表达式

    private String oneMerRate;//商户费率

    private int fixedRate;

    private String bpId; //业务产品id
    private String bpName; //业务产品名称
    private Integer allowIndividualApply;//业务产品允许单独申请，1：是，0：否
    private Integer serviceType;//服务类型
    private Integer serviceType2;//主服务类型
}
