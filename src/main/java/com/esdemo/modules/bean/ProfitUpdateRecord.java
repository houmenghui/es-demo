package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tgh
 * @description 分润修改记录
 * @date 2019/6/14
 */
@Data
public class ProfitUpdateRecord {

    private Long id;

    private String shareId;//分润规则id

    private String costHistory;//修改前代理商成本

    private String cost;//修改后代理商成本

    private BigDecimal shareProfitPercentHistory;//修改前分润比例

    private BigDecimal shareProfitPercent;//修改后分润比例

    private Date efficientDate;//生效日期

    private String effectiveStatus;//是否生效:0-未生效,1-已生效

    private Date updateDate;//修改日期

    private String auther;//修改人

    private Integer shareTaskId;//对应agent_share_rule_task表id

}
