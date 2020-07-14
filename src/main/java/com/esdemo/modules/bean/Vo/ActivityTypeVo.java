package com.esdemo.modules.bean.Vo;

import lombok.Data;

@Data
public class ActivityTypeVo {

    private String activityTypeNo;// 欢乐返子类型编号
    private Integer oneLimitDays;//激活后第一个周期,多少天内
    private Integer twoLimitDays;//激活后第一个周期,多少天内
    private Integer threeLimitDays;//激活后第一个周期,多少天内
    private Integer fourLimitDays;//激活后第一个周期,多少天内

    private Integer deductionLimitDays;//代理商不达标扣款,多少天内
}
