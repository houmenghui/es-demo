package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:25
 */
@Data
public class UserInfoBean {

    private String userName;
    private String password;
    private String agentOem;
    private String loginToken;
    private String userId;
    private String agentNo;
    private String agentNode;
    private String agentName;
    private Long agentLevel;
    private String parentId;
    private String oneLevelId;
    private String phone;
    private String manage;
    private Date lockTime;
    private int wrongPasswordCount;
    private String oneAgentNo;
    private String teamId;

}
