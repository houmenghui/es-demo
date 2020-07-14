package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

@Data
public class UserEntityInfo {
	private Integer Id;
	private String userId;
	private String userType;
	private String entityId;
	private String apply;
	private String manage;
	private String status;
	private Date lastNoticeTime;
	private String loginkey;
	private String loginTime;
	private String accessTeamId;
	
}
