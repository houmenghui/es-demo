package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询处理结果下发
 */
@Data
public class AgentShareRuleInfoVo {

    private String id;//服务合并ID
    private BigDecimal cost;//代理商成本
    private BigDecimal share;//分润百分比
    private Integer cashOutStatus;//是否是提现服务
    private String serviceName;//服务名称
    private Integer isPriceUpdate;//代理商底价仅可修改比例 0.否 1.是

    private AgentShareRuleInfoParentVo parentValue;//上级设置值


}
