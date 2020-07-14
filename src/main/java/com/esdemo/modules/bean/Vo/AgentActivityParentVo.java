package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentActivityParentVo {

    private BigDecimal cashBackAmount;// 返现金额
    private BigDecimal taxRate;// 税额百分比
    private BigDecimal fullPrizeAmount;//首次注册满奖金额
    private BigDecimal notFullDeductAmount;//首次注册不满扣金额

    private BigDecimal oneRewardAmount;//    新欢乐送第1次考核奖励
    private BigDecimal twoRewardAmount;//    新欢乐送第2次考核奖励
    private BigDecimal threeRewardAmount;//    新欢乐送第3次考核奖励
    private BigDecimal fourRewardAmount;//    新欢乐送第4次考核奖励
    private BigDecimal deductionAmount;//    新欢乐送不达标扣款
    private BigDecimal rewardRate;//满奖比例

    private String agentNo;
    private String agentNode;
}
