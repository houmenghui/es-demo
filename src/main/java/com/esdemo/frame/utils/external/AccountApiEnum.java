package com.esdemo.frame.utils.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountApiEnum {
    CREATE_DEFAULT_EXT_ACCOUNT("/extAccountController/createDefaultExtAccount.do"),
    FIND_EXT_ACCOUNT_BALANCE("/extAccountController/findExtAccountBalance.do"),
    FIND_EXT_ACCOUNT_TRANS_INFO_LIST("/extAccountController/findExtAccountTransInfoList.do"),
    FIND_AGENT_PROFIT_DAY_SETTLE_LIST("/agentProfitController/findAgentProfitDaySettleList.do"),
    PROFIT_BUSINESS_PRODUCT("/agentProfitController/findAgentProfitByBusinessProduct.do"),
    FIND_AGENT_PROFIT_SETTLE_COLLEC_SEVEN_DAY("/agentProfitController/findAgentProfitSettleCollecSevenDay.do");

    private String path;
}