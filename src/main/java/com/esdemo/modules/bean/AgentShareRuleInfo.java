package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 下级查询少量服务费率
 */
@Data
public class AgentShareRuleInfo {

    private String id;//服务合并ID
    private String profitType;//分润方式:5-商户签约费率与代理商成本费率差额百分比分润
    private String costRateType;//代理商成本费率类型:1-每笔固定金额，2-扣率，3-扣率+保底封顶
    private BigDecimal shareProfitPercent;//代理商固定分润百分比
    private BigDecimal perFixCost;//代理商成本每笔固定值
    private BigDecimal costRate;//代理商成本扣率
    private Integer cashOutStatus;//是否是提现服务 1提现 2否
    private String serviceName;//服务名称
    private String linkService;//关联服务ID
    private String serviceType;//服务类型
    private String linkServiceType;//提现
    private Integer isPriceUpdate;//代理商底价仅可修改比例 0.否 1.是

    private String bpId;//业务产品ID
    private String serviceId;//服务ID
    private String cardType;//银行卡种类:0-不限，1-只信用卡，2-只储蓄卡
    private String holidaysMark;//节假日标志:0-不限，1-只工作日，2-只节假日

    private String shareId;//分润ID
    private Date efficientDate;//生效日期

    private String serviceType2;//服务类型组合

}
