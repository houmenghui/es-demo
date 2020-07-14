package com.esdemo.modules.bean;

import lombok.Data;

import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 10:25
 */
@Data
public class MerchantBpBean {
    private String mbpId;
    private String bpId;
    private String bpName;
    private String newBpId;
    private String bpStatus;
    private String transAmount;
    private String merchantNo;
    private String currBpId; //下拉选择时的当前默认值
    private String currBpName;//下拉选择时的当前默认值
    private List<MerchantBpBean> canReplaceBpList;
}
