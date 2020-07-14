package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.AgentShareRule;
import com.esdemo.modules.bean.AgentShareRuleTask;
import com.esdemo.modules.bean.ProfitUpdateRecord;
import com.esdemo.modules.bean.UserInfoBean;

import java.util.List;

/**
 * @author tgh
 * @description 修改分润
 * @date 2019/6/13
 */
public interface AgentShareService {

    /**
     * 修改分润
     * @param share
     * @return
     */
    ResponseBean updateAgentShare(AgentShareRuleTask share, UserInfoBean userInfoBean);

    /**
     * 分润修改记录查询
     * @param shareId
     * @return
     */
    List<ProfitUpdateRecord> selectAgentShare(Long shareId,UserInfoBean userInfoBean);

    /**
     * 根据代理商编号查询分润列表
     * @param param
     * @return
     */
    List<AgentShareRule> getAgentShareList(String param);
}
