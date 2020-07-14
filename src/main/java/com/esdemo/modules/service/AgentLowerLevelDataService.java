package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.AgentInfoData;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.bean.Vo.AgentActivityVo;

import java.util.Map;

public interface AgentLowerLevelDataService {

    ResponseBean getAgentDetailEdit(String agentNo, UserInfoBean userInfoBean);

    ResponseBean getAgentDetailAdd(UserInfoBean userInfoBean);

    //下发奖励考核周期
    void saveRewardLevel(AgentActivityVo item);
    //保存活动开关
    void saveSwitch(Map<String,String> oemSwitch, AgentActivityVo item, AgentInfoData agentInfo);
    //获取OEM链条开关
    Map<String,String> getAgentOemPrizeBuckleRank();

}
