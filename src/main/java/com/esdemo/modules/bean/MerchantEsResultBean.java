package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-21 11:53
 */
@Data
public class MerchantEsResultBean {
    private String merchantNo;
    private String merchantName;
    private String mobilePhone;
    private String transMoney;
    private String agentNo;
    private String agentName;
    private String rate;
    private long noTransDays;
    private boolean isDirectMerchant;
}
