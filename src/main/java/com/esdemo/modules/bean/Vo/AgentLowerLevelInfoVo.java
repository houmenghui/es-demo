package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class AgentLowerLevelInfoVo {

    private Long id;
    private  String agentNo;//代理商编号
    private  String agentName;//代理商名称
    private  String agentNode;//代理商节点
    private  String agentLevel;//代理商级别
    private  String parentId;//上级代理商ID
    private  String oneLevelId;//一级代理商ID

    private  String mobilephone;//手机号
    private  String status;//状态：正常-1，关闭进件-0，冻结-2
    private  Date createDate;//创建时间
    private  String toBeSetStatus;//待设置状态

    private  String lowerStatus;//操作代理商是否是当前登入代理商的直属下级 1是 0否

}
