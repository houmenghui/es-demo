package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.util.List;

/**
 * 代理商代理的bpID
 */
@Data
public class AgentBpIdInfoVo {

    private  Long bpId;//业务产品ID
    private String agentShowName;//业务产品展示名称
    private String teamId;//组织
    private String teamName;//组织名称
    private String allowIndividualApply;//是否队长
    private String effectiveStatus;
    private String agentNo;//代理商编号
    private String groupNo;//组编号

    List<AgentShareRuleInfoVo> agentShare;

    private Integer lockStatus;//当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选



}
