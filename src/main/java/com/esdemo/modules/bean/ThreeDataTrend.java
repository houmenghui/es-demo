package com.esdemo.modules.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThreeDataTrend  {
	private TransOrderTrend transOrderTrend;
	private TransOrderTrend newlyMerTrend;
	private TransOrderTrend newlyAgentTrend;
}
