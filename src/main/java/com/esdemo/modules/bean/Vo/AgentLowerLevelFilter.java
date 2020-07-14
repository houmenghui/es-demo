package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class AgentLowerLevelFilter {
    //搜索条件
    private  String agentNo;//筛选的代理商
    private  Integer LowerStatus;//是否包含下级
    private  Date createDateBegin;
    private  Date createDateEnd;
    private  String shareRuleInit;//1=上级未初始化结算价,2=上级已初始化结算价,3=忽略
    private  String registType;//代理商注册类型:拓展代理为1,其他为空
}
