package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author tgh
 * @description 服务,参数传递
 * @date 2019/5/22
 */
@Data
public class ServiceInfo {
    private String serviceId;//服务ID
    private String serviceName;//服务名称
    private String serviceType;//服务类型:1-POS刷卡，2-扫码支付，3-快捷支付，4-账户提现
    private String hardwareIs;//是否与硬件相关:1-是，0-否
    private String bankCard;//可用银行卡集合:1-信用卡，2-银行卡，0-不限
    private String exclusive;//可否单独申请:1-可，0-否
    private String business;//业务归属
    private String saleStarttime;//可销售起始日期
    private String saleEndtime;//可销售终止日期
    private String useStarttime;//可使用起始日期
    private String useEndtime;//可使用终止日期
    private String proxy;//可否代理:1-可，0-否
    private String getcashId;//提现服务ID
    private String rateCard;//费率是否区分银行卡种类:1-是，0-否
    private String rateHolidays;//费率是否区分节假日:1-是，0-否
    private String quotaHolidays;//限额是否区分节假日:1-是，0-否
    private String quotaCard;//限额是否区分银行卡种类:1-是，0-否
    private String oemId;//OEM ID
    private String remark;//备注
    private String tFlag;//T0T1标志：0-不涉及，1-T0，2-T1
    private String cashSubject;//仅服务类型为账户提现，存储科目，账号
    private String fixedRate;//费率固定标志:1-固定，0-不固定
    private String fixedQuota;//额度固定标志:1-固定，0-不固定
    private String serviceStatus;//服务状态：1开启，0关闭
    private String tradStart;//交易开始时间
    private String tradEnd;//交易截至时间
    private String merchantNo;
    private String bpId;
}
