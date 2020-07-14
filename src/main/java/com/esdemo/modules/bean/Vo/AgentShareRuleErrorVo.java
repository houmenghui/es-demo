package com.esdemo.modules.bean.Vo;

import lombok.Data;

@Data
public class AgentShareRuleErrorVo{

    private String id;//服务合并ID
    private String msg;//错误提示语
    private String code;//错误状态码

    public AgentShareRuleErrorVo(String id, String msg, String code) {
        this.id = id;
        this.msg = msg;
        this.code = code;
    }
}
