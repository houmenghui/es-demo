package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 欢乐返活动
 * @date 2019/6/26
 */
@Data
public class HappyBackData {
    private String activityTypeNo; // 欢乐返子类型编号
    private String activityTypeName; // 欢乐返子类型名称
    private String activityCode; // 欢乐返类型
    private String transAmount; // 交易金额
    private String cashBackAmount; //下发返现金额
    private String taxRate; // 	税额百分比
    private String repeatRegisterAmount;//重复返现金额
    private String repeatRegisterRatio;//重复注册返现比例
    private Boolean fullPrizeLevelFlag;// 满奖功能开关标志
    private Boolean notFullDeductLevelFlag;// 不满扣功能开关标志
}
