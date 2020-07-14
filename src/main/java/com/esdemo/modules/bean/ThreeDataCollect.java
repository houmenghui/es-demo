package com.esdemo.modules.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 三方数据汇总
 *
 * @author Qiu Jian
 *
 */
@Setter
@Getter
@ToString
public class ThreeDataCollect {

	private String transSum;
	private String merchantSum;
	private String terminalSum;
	private String activatedMerchantSum;
	private String lastUpdateTime;

	private List<ThreeDataCollect> details;

}
