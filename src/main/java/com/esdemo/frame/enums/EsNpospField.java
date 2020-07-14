package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常用es field名字
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-14 08:54
 */
@AllArgsConstructor
@Getter
public enum EsNpospField {

    TYPE_NAME("type_name"),             // 类型 es join类型
    TYPE("type"),                       // 类型 es text类型
    AGENT_NO("agent_no"),               // 代理商编号 es keyword
    PARENT_ID("parent_id"),             // 父级代理商编号 es keyword
    AGENT_NODE("agent_node"),           // 代理商节点 es text
    AGENT_NAME("agent_name"),           // 代理商名称 es text
    MERCHANT_NO("merchant_no"),         // 商户号     es keyword
    MERCHANT_NAME("merchant_name"),     // 商户名字    es keyword
    BP_ID("bp_id"),                     // 业务产品id es keyword
    MOBILE_PHONE("mobilephone"),        // 手机号 es text
    PROVINCE("province"),               // 省
    CITY("city"),                       // 市
    DISTRICT("district"),               // 区
    STATUS("status"),                   // 状态
    BP_NAME("bp_name"),                 // 业务产品名称 es keyword
    TEAM_ID("team_id"),                 // 组织id es keyword
    ORDER_NO("order_no"),               // 订单id es keyword
    CREATE_TIME("create_time"),         // 创建时间 es date
    TRANS_TIME("trans_time"),           // 交易时间 es date
    PAY_METHOD("pay_method"),           // 交易方式 es date
    SETTLE_STATUS("settle_status"),     // 结算状态 es keyword
    DEVICE_SN("device_sn"),             // 机具SN es keyword
    TRANS_STATUS("trans_status"),       // 交易状态 es keyword
    HLF_ACTIVE("hlf_active"),           // 商户欢乐反激活状态 es keyword
    TRANS_AMOUNT("trans_amount"),       // 交易金额 es ?
    ACCOUNT_NO("account_no"),           // 交易卡号 es ?
    CARD_TYPE("card_type"),             // 卡片种类 es ?
    RECOMMENDED_SOURCE("recommended_source"),   // 商户注册来源： 1：商户APP 2：代理商APP 3：代理商WEB
    RISK_STATUS("risk_status"),                 // 商户冻结状态字段：1 正常，2 只进不出，3 不进不出
    SETTLEMENT_METHOD("settlement_method"),     // 结算方式 0 t0 ,1 t1
    SETTLE_TYPE("settle_type"),                 // 出款类型
    ROUTING("_routing"),                        // 路由
    ACQ_MERCHANT_NO("acq_merchant_no"),         // 收单商户号（一户一码特约商户）
    ORDER_TYPE("order_type");                   // 订单类型，等于5表示云闪付查询

    private String fieldName;

    public String getKey() {
        return this.fieldName + ".key";
    }

    public String getKeyword() {
        return this.fieldName + ".keyword";
    }
}
