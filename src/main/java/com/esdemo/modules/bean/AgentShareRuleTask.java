package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tgh
 * @description
 * @date 2019/6/13
 */
@Data
public class AgentShareRuleTask {
    private Integer id;

    private Long shareId;
    //    @JSONField(format = "yyyy-MM-dd")
    private Date efficientDate;

    private Integer effectiveStatus;

    private Integer profitType;

    private BigDecimal perFixIncome;

    private BigDecimal perFixInrate;

    private BigDecimal safeLine;

    private BigDecimal capping;

    private BigDecimal shareProfitPercent;

    private String ladder;

    private String entityId;

    private String agentNo;

    private BigDecimal shareProfitPercentHistory;//修改前分润比例

    private BigDecimal costHistory;//修改前代理商成本

    private String costRateType;

    private BigDecimal perFixCost;

    private BigDecimal costRate;

    private BigDecimal costCapping;

    private BigDecimal costSafeline;

    private BigDecimal ladder1Rate;

    private BigDecimal ladder1Max;

    private BigDecimal ladder2Rate;

    private BigDecimal ladder2Max;

    private BigDecimal ladder3Rate;

    private BigDecimal ladder3Max;

    private BigDecimal ladder4Rate;

    private BigDecimal ladder4Max;

    private String income;

    private String cost;

    private String ladderRate;

    private Integer checkStatus;
    private String serviceType;
    private String serviceId;
    private String cardType;
    private String holidaysMark;

    public void setLadder(String ladder) {
        this.ladder = ladder == null ? null : ladder.trim();
    }

    public void setCostRateType(String costRateType) {
        this.costRateType = costRateType == null ? null : costRateType.trim();
    }
}
