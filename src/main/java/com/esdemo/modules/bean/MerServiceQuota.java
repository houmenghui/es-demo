package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 商户每一个服务对应的商户限额
 * @date 2019/5/22
 */
@Data
public class MerServiceQuota {
    private String card_type;//银行卡种类:0-不限，1-只信用卡，2-只储蓄卡(传)
    private String disabled_date;//失效时间
    private String efficient_date;//生效时间
    private String holidays_mark;//节假日标志:1-只工作日，2-只节假日，0-不限(传)
    private String id;//id
    private String merchant_no;//商户ID
    private String service_id;//服务ID(传)
    private String single_count_amount;//单笔最大交易额(传)
    private String single_daycard_amount;//单日单卡最大交易额(传)
    private String single_daycard_count;//单日单卡最大交易笔数(传)
    private String single_day_amount;//单日最大交易额(传)
    private String useable;
    private String fixed_mark;
    private String service_name;//服务名称
    private String single_min_amount;//单笔最小交易额(传)
    private String check_status;
    private String singleDayAmount;
    private String singleCountAmount;
    private String singleDaycardAmount;
    private String singleDaycardCount;
    private String singleMinAmount;
    private String cardType;
    private String serviceId;
    private String holidaysMark;
}
