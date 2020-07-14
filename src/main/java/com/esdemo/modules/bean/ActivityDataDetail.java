package com.esdemo.modules.bean;

import com.esdemo.frame.enums.ActivityDataEnum;
import com.esdemo.frame.enums.ActivityEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityDataDetail {

    private String merchantNo; //商户编号
    private String merchantName; // 商户名称
    private String phone; //手机号
    private ActivityEnum subType;  // 活动类型  1 欢乐返  2 新欢乐送
    private String activityTypeNo; // 活动子类型 编号
    private String activityTypeName; // 活动子类型 名称
    private ActivityDataEnum activityData;  //参与活动
    private String activityDataName;  //参与活动名称
    private BigDecimal transAmount;  // 活动已收款
    private BigDecimal reachStandAmount;//活动达标值
    private String activityEndTime; // 活动结束时间
    private String transType; // 统计的交易类型
    private String transTypeDesc; // 统计的交易类型描述
    private String transTypeRemark; // 统计的交易类型描述
    private String activityDataStatus;  //参与子活动状态 0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款

}
