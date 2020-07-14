package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 商户业务产品
 * @date 2019/5/22
 */
@Data
public class MerBusinessProduct {

    private String id;

    private String bpId;//业务产品ID(传)

    private String createTime;//申请时间

    private String merchantNo;//商户编号

    private String saleName;//所属销售(传)

    private Integer status;//状态(传,传O为待审核)
}
