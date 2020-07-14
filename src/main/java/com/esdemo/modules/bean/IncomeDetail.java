package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Title：agentApi2
 * @Description：收入明细(ES)
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Data
public class IncomeDetail {
    private BigDecimal totalIncome;
    private BigDecimal profitIncome;
    private BigDecimal hpbIncome;
    private BigDecimal subIncome;
    private BigDecimal subProfitIncome;
    private BigDecimal subHpbIncome;
    private BigDecimal leftIncome;
    private BigDecimal leftProfitIncome;
    private BigDecimal leftHpbIncome;

    public IncomeDetail() {
        this.totalIncome = BigDecimal.ZERO;
        this.profitIncome = BigDecimal.ZERO;
        this.hpbIncome = BigDecimal.ZERO;
        this.subIncome = BigDecimal.ZERO;
        this.subProfitIncome = BigDecimal.ZERO;
        this.subHpbIncome = BigDecimal.ZERO;
        this.leftIncome = BigDecimal.ZERO;
        this.leftProfitIncome = BigDecimal.ZERO;
        this.leftHpbIncome = BigDecimal.ZERO;
    }

    /**
     * 总收入
     * @return
     */
    public BigDecimal getTotalIncome() {
        return this.profitIncome.add(this.hpbIncome);
    }

    /**
     * 剩余金额
     * @return
     */
    public BigDecimal getLeftIncome() {
        return this.leftProfitIncome.add(this.leftHpbIncome);
    }

    /**
     * 下发分润收入
     * @return
     */
    public BigDecimal getSubProfitIncome() {
        return this.profitIncome.subtract(this.leftProfitIncome);
    }

    /**
     * 下发活动补贴
     * @return
     */
    public BigDecimal getSubHpbIncome() {
        return this.hpbIncome.subtract(this.leftHpbIncome);
    }

    /**
     * 总下发金额
     * @return
     */
    public BigDecimal getSubIncome() {
        return this.getSubProfitIncome().add(this.getSubHpbIncome());
    }
}
