package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tgh
 * @description 欢乐返活动
 * @date 2019/7/2
 */
@Data
public class AgentActivity {
    private Long id;
    private String activityTypeNo;// 欢乐返子类型编号
    private String activityTypeName;// 欢乐返子类型名称
    private String activityCode;// 欢乐返类型
    private BigDecimal transAmount;// 交易金额
    private String agentNo;//代理编号
    private String agentNode;//代理商节点
    private BigDecimal cashBackAmount;// 返现金额
    private BigDecimal taxRate;// 税额百分比
    private Date createTime;// 创建时间
    private String remark;
    private Date currentTime;//返回当前时间
    private BigDecimal repeatRegisterAmount;//重复返现金额
    private BigDecimal repeatRegisterRatio;//重复注册返现比例

    private BigDecimal fullPrizeAmount;//首次注册满奖金额
    private BigDecimal notFullDeductAmount;//首次注册不满扣金额

    private BigDecimal repeatFullPrizeAmount;//重复注册满奖金额
    private BigDecimal repeatNotFullDeductAmount;//重复注册不满扣金额

    private boolean showFullPrizeAmount;
    private boolean showNotFullDeductAmount;


    private BigDecimal oneRewardAmount;//    新欢乐送第1次考核奖励
    private BigDecimal twoRewardAmount;//    新欢乐送第2次考核奖励
    private BigDecimal threeRewardAmount;//    新欢乐送第3次考核奖励
    private BigDecimal fourRewardAmount;//    新欢乐送第4次考核奖励

    private BigDecimal oneRepeatRewardAmount;//    重复注册新欢乐送第1次考核奖励
    private BigDecimal twoRepeatRewardAmount;//    重复注册新欢乐送第2次考核奖励
    private BigDecimal threeRepeatRewardAmount;//  重复注册 新欢乐送第3次考核奖励
    private BigDecimal fourRepeatRewardAmount;//   重复注册新欢乐送第4次考核奖励

    private BigDecimal deductionAmount;//    新欢乐送不达标扣款
    private BigDecimal repeatDeductionAmount;//    代理商不达标扣款,重复注册扣款金额

    private BigDecimal merchantRewardAmount;// **   新欢乐送商户奖励

    private BigDecimal rewardRate;//奖励比例
    private String subType;//欢乐返子类型类型,1:原来的欢乐返,2:欢乐返新活动


    private Long teamId;// 组织ID
    private String teamName;// 组织名称
    private String groupNo;// 分组编号

    private Integer fullPrizeSwitch;// 欢乐返，满奖开关 1-打开，0-关闭
    private Integer notFullDeductSwitch;// 欢乐返，不满扣开关 1-打开，0-关闭

    private Integer rewardLevel;// 新欢乐送层级 1.显示第一次考核 2.显示第一，二次考核，如此类推
}
