package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 欢乐返设置 满奖不满扣
 * @date 2019/7/2
 */
@Data
public class SupportRankDto {
    private Integer fullPrizeLevel = 1 ;
    private Integer notFullDeductLevel = 1 ;
}
