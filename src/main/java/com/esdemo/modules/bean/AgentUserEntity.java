package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

@Data
public class AgentUserEntity {
    private Long id;

    private String userId;

    private String userType;

    private String entityId;

    private String apply;

    private String manage;

    private String status;

    private Date lastNoticeTime;

    private String loginkey;

    private Date loginTime;

    private String isAgent;

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId == null ? null : entityId.trim();
    }

    public void setApply(String apply) {
        this.apply = apply == null ? null : apply.trim();
    }

    public void setManage(String manage) {
        this.manage = manage == null ? null : manage.trim();
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public void setLoginkey(String loginkey) {
        this.loginkey = loginkey == null ? null : loginkey.trim();
    }

}