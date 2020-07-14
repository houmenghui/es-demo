package com.esdemo.modules.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 三方数据明细查询对象
 *
 * @author Qiu Jian
 *
 */
@Setter
@Getter
@ToString
public class ThreeDataTetailQo {

	private String currentAgentNo;
	private String agentNo;
	private String teamId;
	private String yearMonth;

	private List<String> agentNoList;
}
