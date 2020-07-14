package com.esdemo.modules.bean.Vo;

import com.esdemo.frame.utils.GsonUtils;
import lombok.Data;

import java.util.List;
@Data
public class AgentLowerLevelVo {

    String agentInfo;
    String accountCard;
    String bpList;
    String happyBack;
    String newHappyGive;

    public AgentLowerLevelUp getAgentInfo() {
        return GsonUtils.fromJson2Bean(agentInfo, AgentLowerLevelUp.class);
    }

    public AccountCardUp getAccountCard() {
        return GsonUtils.fromJson2Bean(accountCard, AccountCardUp.class);
    }

    public List<AgentBpIdInfoVo> getBpList() {
        return GsonUtils.fromJson2List(bpList, AgentBpIdInfoVo.class);
    }

    public List<AgentActivityVo> getHappyBack() {
        return GsonUtils.fromJson2List(happyBack, AgentActivityVo.class);
    }

    public List<AgentActivityVo> getNewHappyGive() {
        return GsonUtils.fromJson2List(newHappyGive, AgentActivityVo.class);
    }
}
