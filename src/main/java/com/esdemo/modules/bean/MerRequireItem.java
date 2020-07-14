package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 结算信息资料项
 * @date 2019/5/22
 */
@Data
public class MerRequireItem {
    private String id;
    private String merchant_no;//商户ID
    private String mri_id;//进件要求项ID
    private String content;//附件名称包含后缀
    private String status;//状态：0待审核；1通过；2审核失败
    private String check_status;
    private String merchantNo;
    private String mriId;
    private String last_update_time;
    private String subBank;
    private String itemName;
    private String exampleType;
    private String checkMsg;
    private String dataAll;
    private String checkStatus;
    private String photoAddress;
    private String remark;
}
