package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.bean.Vo.*;

import java.util.List;
import java.util.Map;

/**
 * 代理商开设下级查询service
 * @author liuks
 */
public interface AgentLowerLevelService {

    ResponseBean getAgentLowerLevelToBeSetList(int pageNo,int pageSize,UserInfoBean userInfoBean);

    List<AgentLowerLevelInfoVo> getAgentLowerLevelAllList(UserInfoBean userInfoBean, AgentLowerLevelFilter queryInfo);

    int setAgentToBeSetIgnore(String agentNo);

    AgentLowerLevelInfoVo getAgentLowerLevelDetail(String agentNo);

    ResponseBean getBindingSettlementCardBeforeData(UserInfoBean userInfoBean);

    ResponseBean setBindingSettlementCard(AccountCardUp accountCard, UserInfoBean userInfoBean);

    ResponseBean checkAgentBase(AgentLowerLevelUp agentInfo, UserInfoBean userInfoBean);

    ResponseBean checkAgentCard(AccountCardUp accountCard, UserInfoBean userInfoBean);

    ResponseBean checkbpService(List<AgentBpIdInfoVo> bpList, String agentNo, UserInfoBean userInfoBean);

    ResponseBean addAgentLowerLevel(AgentLowerLevelVo params, UserInfoBean userInfoBean,Map<String, String> agentNoMap);

    void openAgentAccount(String agentNo);

    ResponseBean editAgentLowerLevel(String agentNo, AgentLowerLevelVo params, UserInfoBean userInfoBean);

    int updateAgentAccount(String agentNo,int status);
}
