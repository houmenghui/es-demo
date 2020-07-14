package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 业务产品列表
 * @date 2019/6/11
 */
@Data
public class PbdInfo {

    private String bp_id;//业务产品ID;
    private String bp_name;//业务产品姓名;
    private String bp_type;//业务产品类型;
    private String remark;//业务产品简介;
    private String allowIndividualApply; 	// 允许单独申请，1：是，0：否'
    private String groupNo;					//产品组号

}
