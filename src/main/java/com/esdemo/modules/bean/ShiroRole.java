package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ShiroRole {
    private Integer id;

    private String roleCode;

    private String roleName;

    private String roleRemake;

    private Integer roleState;

    private String createOperator;

    private Date createTime;

    private Date updateTime;
}