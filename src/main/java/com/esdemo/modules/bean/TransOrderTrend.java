package com.esdemo.modules.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TransOrderTrend {
	
	private List<KeyValueBean> sevenDayTrend;
	private List<KeyValueBean> halfYearTrend;

}
