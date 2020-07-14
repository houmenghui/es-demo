package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author tgh
 * @description 业务产品
 * @date 2019/6/4
 */
@Data
public class BusinessProductDefine {
    private Long id;

    private String bpId;

    private String bpName;

    private Date saleStarttime;

    private Date saleEndtime;

    private String proxy;

    private String bpType;

    private String isOem;

    private String oemId;

    private String ownBpId;

    private String twoCode;

    private String remark;
}
