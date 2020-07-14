package com.esdemo.modules.bean;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThreeDataHome {
	private String yesterdayTradeSum;
	private Long totalMerchantNum;
	private Long yesterdayAddMerchantNum;
	private Long totalAgentNum;
	private String currentMonthTradeSum;
	private Long currentMonthTradeCount;
	private Long currentMonthAddMerchantNum;
	private Long currentMonthAddAgentNum;


}
