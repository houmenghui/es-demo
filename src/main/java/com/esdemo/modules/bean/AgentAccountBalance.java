package com.esdemo.modules.bean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/5/4.
 */
public class AgentAccountBalance {
    private String msg;
    private double settlingAmount;
    private String balance;
    private String accountNo;
    private String name;
    private BigDecimal avaliBalance;
    private BigDecimal preFreezeAmount;
    private BigDecimal controlAmount;
    private boolean status;
    private ServiceRate serviceRate;

    public ServiceRate getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(ServiceRate serviceRate) {
        this.serviceRate = serviceRate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public double getSettlingAmount() {
        return settlingAmount;
    }

    public void setSettlingAmount(double settlingAmount) {
        this.settlingAmount = settlingAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAvaliBalance() {
        return avaliBalance;
    }

    public void setAvaliBalance(BigDecimal avaliBalance) {
        this.avaliBalance = avaliBalance;
    }

    public BigDecimal getPreFreezeAmount() {
        return preFreezeAmount;
    }

    public void setPreFreezeAmount(BigDecimal preFreezeAmount) {
        this.preFreezeAmount = preFreezeAmount;
    }

    public BigDecimal getControlAmount() {
        return controlAmount;
    }

    public void setControlAmount(BigDecimal controlAmount) {
        this.controlAmount = controlAmount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
