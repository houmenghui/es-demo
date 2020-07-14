package com.esdemo.modules.bean;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TradeTrend {
	private BigDecimal tradeSum;
	private String dateStr;

}
