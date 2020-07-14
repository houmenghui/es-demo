package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 14:01
 */
@Data
public class MerchantBusinessProductHistory {
    private Long id;
    private String sourceBpId;
    private String newBpId;
    private String operationType;
    private String operationPersonType;
    private Date createTime;
    private String operationPersonNo;
    private String merchantNo;
}
