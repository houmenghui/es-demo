package com.esdemo.modules.bean;

import lombok.Data;

@Data
public class ActivityData {

    private String activity;//活动 happyBack 欢乐返   newHappyGive  新欢乐送


    private String activityDataName;  // 子活动名称

    private String activityData; // 子活动表示 对应 明细筛选字段的参加活动
    private String statusType = "rewardStatus"; // 考核类型 // rewardStatus 奖励考核  deductionStatus 扣款考核

    private  int  examine; // 考核中

    private  int reachStandard;// 已达标

    private int notStandard;//未达标
    private int notBegin;//未开始

    private String examineName = "考核中";
    private String reachStandardName = "已达标";
    private String notStandardName = "未达标";
    private String notBeginName = "未开始";



}
