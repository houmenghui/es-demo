package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentActivityVo {

    private String activityTypeNo;// 欢乐返子类型编号
    private String subType;// 欢乐返子类型
    private String agentNo;
    private String agentNode;
    private BigDecimal cashBackAmount;// 返现金额
    private BigDecimal taxRate;// 税额百分比
    private String createTime;// 创建时间
    private BigDecimal fullPrizeAmount;//首次注册满奖金额
    private BigDecimal notFullDeductAmount;//首次注册不满扣金额

    private BigDecimal oneRewardAmount;//    新欢乐送第1次考核奖励
    private BigDecimal twoRewardAmount;//    新欢乐送第2次考核奖励
    private BigDecimal threeRewardAmount;//    新欢乐送第3次考核奖励
    private BigDecimal fourRewardAmount;//    新欢乐送第4次考核奖励
    private BigDecimal deductionAmount;//    新欢乐送不达标扣款
    private BigDecimal rewardRate;//满奖比例

    private BigDecimal transAmount;//交易金额
    private String activityTypeName;// 欢乐返子类型名称
    private Long teamId;// 组织ID
    private String teamName;// 组织名称
    private String groupNo;// 分组编号

    private Integer fullPrizeSwitch;// 欢乐返，满奖开关 1-打开，0-关闭
    private Integer notFullDeductSwitch;// 欢乐返，不满扣开关 1-打开，0-关闭

    private Integer rewardLevel;// 新欢乐送层级 1.显示第一次考核 2.显示第一，二次考核，如此类推
    private Integer deductionStatus;// 新欢乐送 不达标扣款设置 1.显示 0不显示

    private Integer activityValueSameStatus;// 活动同组数据是否一致，1一致，0不一致

    private AgentActivityParentVo parentValue;//父级数据

    private Integer lockStatus;//当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选

}
