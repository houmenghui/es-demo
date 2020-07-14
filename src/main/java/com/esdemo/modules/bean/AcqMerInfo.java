package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 收单商户进件模型
 * @date 2019/5/24
 */
@Data
public class AcqMerInfo {
    private String id ;
    private String merchant_no ;//普通商户编号
    private String change_mer_business_info;//切换商户的业务产品信息
    private String merchant_type ;//进件类型:1个体收单商户，2-企业收单商户
    private String merchant_name ;//商户名称
    private String legal_person ;//法人姓名
    private String legal_person_id ;//法人身份证号
    private String up_legal_person_id_md5;//法人身份证号Md5（修改后）
    private String legal_person_id_md5;//法人身份证号Md5（修改前）
    private String id_valid_start ;//身份证有效期开始时间
    private String id_valid_end ;//身份证有效期结束时间
    private String province ;//经营地址(省)
    private String city ;//经营地址（市）
    private String district ;//经营地址（区）
    private String address ;//详细地址
    private String one_scope;//一级经营范围
    private String two_scope ;//二级经营范围
    private String charter_name ;//营业执照名称
    private String charter_no ;//营业执照编号
    private String charter_valid_start ;// 营业执照有效开始时间
    private String charter_valid_end ;// 营业执照有效期结束时间
    private String account_type ;//账户类型 1 对私 2对公
    private String bank_no ;//银行卡号
    private String up_bank_no_md5;//银行卡号Md5（修改后）
    private String bank_no_md5;//银行卡号Md5（修改前）
    private String account_name ;//开户名
    private String account_bank ;//开户银行
    private String account_province ;//开户地区（省）
    private String account_city ;//开户地区（市）
    private String account_district ;//开户地区（区）
    private String bank_branch ;//支行
    private String line_number ;//联行号
    private String acq_into_no ;//进件编号
    private String into_source ;//进件来源
    private String audit_status ;//审核状态 1.正常 2.审核通过 3 审核不通过
    private String audit_time ;//审核时间
    private String create_time ;//进件时间
    private String agent_no ;//所属代理商
    private String one_agent_no ;//所属一级代理商
    private String examination_opinions ;//审核意见
    private String mcc ;//mcc码
    private String one_scope_name;//一级经营范围名称
    private String two_scope_name;//二级经营范围名称
    private String update_time;//修改时间

    @Override
    public String toString() {
        return "AcqMerInfo{" +
                "id='" + id + '\'' +
                ", merchant_no='" + merchant_no + '\'' +
                ", merchant_type='" + merchant_type + '\'' +
                ", merchant_name='" + merchant_name + '\'' +
                ", legal_person='" + legal_person + '\'' +
                ", legal_person_id='" + legal_person_id + '\'' +
                ", id_valid_start='" + id_valid_start + '\'' +
                ", id_valid_end='" + id_valid_end + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", one_scope='" + one_scope + '\'' +
                ", two_scope='" + two_scope + '\'' +
                ", charter_name='" + charter_name + '\'' +
                ", charter_no='" + charter_no + '\'' +
                ", charter_valid_start='" + charter_valid_start + '\'' +
                ", charter_valid_end='" + charter_valid_end + '\'' +
                ", account_type='" + account_type + '\'' +
                ", bank_no='" + bank_no + '\'' +
                ", account_name='" + account_name + '\'' +
                ", account_bank='" + account_bank + '\'' +
                ", account_province='" + account_province + '\'' +
                ", account_city='" + account_city + '\'' +
                ", account_district='" + account_district + '\'' +
                ", bank_branch='" + bank_branch + '\'' +
                ", line_number='" + line_number + '\'' +
                ", acq_into_no='" + acq_into_no + '\'' +
                ", into_source='" + into_source + '\'' +
                ", audit_status='" + audit_status + '\'' +
                ", audit_time='" + audit_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", agent_no='" + agent_no + '\'' +
                ", one_agent_no='" + one_agent_no + '\'' +
                ", examination_opinions='" + examination_opinions + '\'' +
                ", mcc='" + mcc + '\'' +
                ", one_scope_name='" + one_scope_name + '\'' +
                ", two_scope_name='" + two_scope_name + '\'' +
                ", update_time='" + update_time + '\'' +
                '}';
    }
}
