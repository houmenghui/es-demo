package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 机具申请记录查询
 * @date 2019/6/3
 */
@Data
public class TerminalApplyRecord {
    private String id;// 记录ID
    private String merchant_no;//商户号
    private String status ;// 状态 0:待直属处理 1:已处理 2:待一级处理
    private String product_type;// 机具类型
    private String create_time;//申请时间
    private String address;//商户地址
    private String mobilephone;// 手机号
    private String remark;// 备注
    private String update_time;//最后处理时间
    private String merchant_name;// 商户名称
    private String need_operation;//是否需要处理(Y需要/N不需要)
    private String agent_no_one;//一级代理商编号
    private String agent_name_one;// 一级代理商名称
    private String agent_no_parent;//所属代理商编号
    private String agent_name_parent;// 所属代理商名称
    private String bp_name;// 机具类型对应的中文
    private String sn;// 处理时填写的机具SN号
}
