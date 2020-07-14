package com.esdemo.modules.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现分润日数据汇总
 *
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/8/2 16:16
 * @Version：1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettleDayBatch {

    private Long id;
    private String agentNo;
    private String parentId;
    private String agentNode;
    private int totalCount;
    private BigDecimal totalMoney;
    private BigDecimal accMoney;
    private Date collecTime;
    private Date createTime;
}