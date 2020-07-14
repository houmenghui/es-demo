package com.esdemo.modules.bean;

import java.math.BigDecimal;

/**
 * Created by 666666 on 2017/10/27.
 */
public class ProviderBean {
    private String agentNo;
    private String agentName;
    private String agentLevel;
    private String mobilephone;
    private BigDecimal rate;
    private BigDecimal singleAmount;
    private BigDecimal fullRepayRate;
    private BigDecimal fullRepaySingleAmount;
    private String cost;
    private String fullRepayCost;
    private String parentId;
    private String accountRatio;
    private BigDecimal perfectRepayRate;
    private BigDecimal perfectRepaySingleAmount;
    private String perfectRepayCost;
    private String nfcOrigCode;
    private String commonCodeUrl;

    public String getAccountRatio() {
        return accountRatio;
    }

    public ProviderBean setAccountRatio(String accountRatio) {
        this.accountRatio = accountRatio;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public String getAgentLevel() {
        return agentLevel;
    }

    public ProviderBean setAgentLevel(String agentLevel) {
        this.agentLevel = agentLevel;
        return this;
    }

    public ProviderBean setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public ProviderBean() {
    }

    public ProviderBean(String agentNo, BigDecimal rate, BigDecimal singleAmount, BigDecimal fullRepayRate, BigDecimal fullRepaySingleAmount) {
        this.agentNo = agentNo;
        this.rate = rate;
        this.singleAmount = singleAmount;
        this.fullRepayRate = fullRepayRate;
        this.fullRepaySingleAmount = fullRepaySingleAmount;
    }

    public ProviderBean(String agentNo, BigDecimal rate, BigDecimal singleAmount, BigDecimal fullRepayRate, BigDecimal fullRepaySingleAmount,
                        BigDecimal perfectRepayRate, BigDecimal perfectRepaySingleAmount) {
        this.agentNo = agentNo;
        this.rate = rate;
        this.singleAmount = singleAmount;
        this.fullRepayRate = fullRepayRate;
        this.fullRepaySingleAmount = fullRepaySingleAmount;
        this.perfectRepayRate = perfectRepayRate;
        this.perfectRepaySingleAmount = perfectRepaySingleAmount;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAgentNo() {
        return agentNo;
    }

    public void setAgentNo(String agentNo) {
        this.agentNo = agentNo;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(BigDecimal singleAmount) {
        this.singleAmount = singleAmount;
    }

    public BigDecimal getFullRepayRate() {
        return fullRepayRate;
    }

    public ProviderBean setFullRepayRate(BigDecimal fullRepayRate) {
        this.fullRepayRate = fullRepayRate;
        return this;
    }

    public BigDecimal getFullRepaySingleAmount() {
        return fullRepaySingleAmount;
    }

    public ProviderBean setFullRepaySingleAmount(BigDecimal fullRepaySingleAmount) {
        this.fullRepaySingleAmount = fullRepaySingleAmount;
        return this;
    }

    public String getFullRepayCost() {
        return fullRepayCost;
    }

    public ProviderBean setFullRepayCost(String fullRepayCost) {
        this.fullRepayCost = fullRepayCost;
        return this;
    }

    public BigDecimal getPerfectRepayRate() {
        return perfectRepayRate;
    }

    public void setPerfectRepayRate(BigDecimal perfectRepayRate) {
        this.perfectRepayRate = perfectRepayRate;
    }

    public BigDecimal getPerfectRepaySingleAmount() {
        return perfectRepaySingleAmount;
    }

    public void setPerfectRepaySingleAmount(BigDecimal perfectRepaySingleAmount) {
        this.perfectRepaySingleAmount = perfectRepaySingleAmount;
    }

    public String getPerfectRepayCost() {
        return perfectRepayCost;
    }

    public void setPerfectRepayCost(String perfectRepayCost) {
        this.perfectRepayCost = perfectRepayCost;
    }

    public String getNfcOrigCode() {
        return nfcOrigCode;
    }

    public void setNfcOrigCode(String nfcOrigCode) {
        this.nfcOrigCode = nfcOrigCode;
    }

    public String getCommonCodeUrl() {
        return commonCodeUrl;
    }

    public void setCommonCodeUrl(String commonCodeUrl) {
        this.commonCodeUrl = commonCodeUrl;
    }
}
