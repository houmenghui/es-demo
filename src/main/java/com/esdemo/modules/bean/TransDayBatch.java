package com.esdemo.modules.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理商交易分润日数据汇总
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
public class TransDayBatch {

    private Long id;
    private String agentNo;
    private String agentNode;
    private String parentId;
    private int totalCount;
    private BigDecimal totalTransAmount;
    private BigDecimal totalMoney;
    private BigDecimal accMoney;
    private Date collecTime;
    private Date createTime;
}