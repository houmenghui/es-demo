package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Map;

/**
 * 代理商代理的bpID
 */
@Data
public class AgentBpIdInfo {

    private  Long bpId;//业务产品ID
    private String agentShowName;//业务产品展示名称
    private String teamId;//组织
    private String teamName;//组织名称
    private String allowIndividualApply;//是否队长
    private String effectiveStatus;
    private String agentNo;//代理商编号
    private String groupNo;//组编号

    Map<String,AgentShareRuleInfo> agentShareMap;

}
