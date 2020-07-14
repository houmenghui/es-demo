package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityEnum {
    //大的活动类别是有序的要求 新欢乐送放在前面
    newHappyGive("newHappyGive","2"), // 新欢乐送活动
    happyBack("happyBack","1"); //欢乐返 活动
    private String activityDataType; //数据统计类型
    private String activitySubType;// 活动类型

}
