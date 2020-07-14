package com.esdemo.frame.enums;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @create 2020-03-03 11:01
 */
public enum RepayEnum {
    REPAY("repay", "超级还款", "服务商"),  // 超级还业务
    NFC("nfc", "碰一碰", "代理商");      // nfc业务
    private String type;
    private String businessName;
    private String agentName;

    RepayEnum(String type, String businessName, String agentName) {
        this.type = type;
        this.businessName = businessName;
        this.agentName = agentName;
    }

    public String getType() {
        return type;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getAgentName() {
        return agentName;
    }
}
