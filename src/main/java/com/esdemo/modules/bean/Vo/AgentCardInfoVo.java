package com.esdemo.modules.bean.Vo;

import lombok.Data;

@Data
public class AgentCardInfoVo {

    private Long id;
    private  String agentNo;//代理商编号
    private  String agentName;//代理商名称
    private  String agentNode;//代理商节点
    private  String agentLevel;//代理商级别
    private  String parentId;//上级代理商ID
    private  String oneLevelId;//一级代理商ID
    private  String accountName;//开户名
    private  String idCardNo;//身份证,不打码


}
