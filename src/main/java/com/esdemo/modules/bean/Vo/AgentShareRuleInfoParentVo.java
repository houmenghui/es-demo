package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentShareRuleInfoParentVo {

    private BigDecimal cost;//代理商成本
    private BigDecimal share;//分润百分比

    public AgentShareRuleInfoParentVo(BigDecimal cost, BigDecimal share) {
        this.cost = cost;
        this.share = share;
    }
}
