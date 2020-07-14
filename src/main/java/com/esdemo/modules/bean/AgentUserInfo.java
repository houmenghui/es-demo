package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

@Data
public class AgentUserInfo {
    private Long id;

    private String userId;

    private String userName;

    private String mobilephone;

    private String status;

    private Date updatePwdTime;

    private String password;

    private String teamId;

    private Date createTime;
    
    private String email;

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone == null ? null : mobilephone.trim();
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId == null ? null : teamId.trim();
    }
}