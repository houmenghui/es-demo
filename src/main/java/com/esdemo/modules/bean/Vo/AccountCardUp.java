package com.esdemo.modules.bean.Vo;

import lombok.Data;

/**
 * 绑定结算卡实体
 */
@Data
public class AccountCardUp {

    private String agentNo;//代理商编号
    private String accountName;//开户名
    private String idCardNo;//身份证号
    private String accountType;//账号类型 1对公 2对私
    private String accountNo;//银行卡号
    private String bankName;//开户行全称
    private String accountPhone;//银行预留手机号
    private String accountProvince;//开户行地区：省
    private String accountCity;//开户行地区：市
    private String subBank;//支行
    private String cnapsNo;//联行行号

}
