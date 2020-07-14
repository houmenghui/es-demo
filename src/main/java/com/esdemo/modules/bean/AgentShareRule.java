package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AgentShareRule {
    private Long id;

    private String agentId;

    private String agentNo;

    private String bpName;//业务产品名称

    private String serviceId;
    
    private String serviceName;

    private String cardType;

    private String holidaysMark;

    private Date efficientDate;

    private Date disabledDate;

    private String profitType;

    private String profitTypeZh;

    private BigDecimal perFixIncome;

    private BigDecimal perFixInrate;

    private BigDecimal safeLine;

    private BigDecimal capping;

    private BigDecimal shareProfitPercent;

    private String ladder;

    private String costRateType;

    private BigDecimal perFixCost;

    private BigDecimal costRate;

    private BigDecimal costCapping;

    private BigDecimal costSafeline;

    private String checkStatus;

    private String lockStatus;

    private String shareSet;
    
    private String income;
    
    private BigDecimal ladder1Rate;

	private BigDecimal ladder1Max;

	private BigDecimal ladder2Rate;

	private BigDecimal ladder2Max;

	private BigDecimal ladder3Rate;

	private BigDecimal ladder3Max;

	private BigDecimal ladder4Rate;

	private BigDecimal ladder4Max;

	private String serviceType;

    public void setAgentId(String agentId) {
        this.agentId = agentId == null ? null : agentId.trim();
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId == null ? null : serviceId.trim();
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public void setHolidaysMark(String holidaysMark) {
        this.holidaysMark = holidaysMark == null ? null : holidaysMark.trim();
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType == null ? null : profitType.trim();
    }

    public void setLadder(String ladder) {
        this.ladder = ladder == null ? null : ladder.trim();
    }

    public void setCostRateType(String costRateType) {
        this.costRateType = costRateType == null ? null : costRateType.trim();
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus == null ? null : checkStatus.trim();
    }

    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus == null ? null : lockStatus.trim();
    }
    
}