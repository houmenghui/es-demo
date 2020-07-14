package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 商户每一个服务对应的费率值
 * @date 2019/5/22
 */
@Data
public class MerServiceRate {
    private String capping;//封顶
    private String card_type;//银行卡种类:0-不限，1-只信用卡，2-只储蓄卡(传)
    private String disabled_date;//失效时间
    private String efficient_date;//生效时间
    private String holidays_mark;//节假日标志:1-只工作日，2-只节假日，0-不限(传)
    private String id;//id
    private String merchant_no;//商户ID
    private String rate;//扣率
    private String rate_type;//费率类型:1-每笔固定金额，2-扣率，3-扣率带保底封顶，4-扣率+固定金额,5-单笔阶梯 扣率(传)
    private String safe_line;//保底
    private String service_id ;//服务ID(传)
    private String single_num_amount;//每笔固定值
    private String ladder1_rate; //阶梯区间1费率
    private String ladder1_max; //阶梯区间1上限
    private String ladder2_rate;//阶梯区间2费率
    private String ladder2_max ;//阶梯区间2上限
    private String ladder3_rate  ; //阶梯区间3费率
    private String ladder3_max;//阶梯区间3上限
    private String ladder4_rate;//阶梯区间4费率
    private String ladder4_max;//阶梯区间4上限
    private String service_name;//服务名称
    private String check_status;
}
