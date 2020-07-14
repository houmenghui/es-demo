package com.esdemo.frame.enums;

import com.esdemo.modules.service.ActivityDataQueryService;
import com.esdemo.modules.service.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActivityDataEnum {

    fullRewardAmount("fullRewardAmount",new String[]{"full_prize_amount","repeat_full_prize_amount"} ,"", FullRewardAmountQueryServiceImpl.class), //欢乐返满奖
    deductionAmount("deductionAmount" ,new String[]{"not_full_deduct_amount","repeat_not_full_deduct_amount"},"", DeductionAmountQueryServiceImpl.class),//欢乐返不满扣
    newOneRewardAmount("newOneRewardAmount" ,new String[]{},"one_limit_days", NewRewardMerCountQueryServiceImpl.class),//    新欢乐送第1次考核奖励
    newTwoRewardAmount("newTwoRewardAmount" ,new String[]{},"two_limit_days",NewRewardMerCountQueryServiceImpl.class),//    新欢乐送第2次考核奖励
    newThreeRewardAmount("newThreeRewardAmount",new String[]{},"three_limit_days",NewRewardMerCountQueryServiceImpl.class),//    新欢乐送第3次考核奖励
    newFourRewardAmount("newFourRewardAmount",new String[]{},"four_limit_days",NewRewardMerCountQueryServiceImpl.class),//    新欢乐送第4次考核奖励
    newDeductionAmount("newDeductionAmount" , new String[]{},"deduction_limit_days", NewDeductionAmountQueryServiceImpl.class),//    新欢乐送不达标扣款
    newMerchantRewardAmount("newMerchantRewardAmount" , new String[]{"merchant_reward_amount","merchant_repeat_reward_amount"},"merchant_limit_days", NewMerchantRewardAmountQueryServiceImpl.class);//    新欢乐送商户奖励
    private String activityDataType;
    private String[] activityDataLink; //对应的字段
    private String sureLink; //对应的字段
    private Class<? extends ActivityDataQueryService> queryService;


}
