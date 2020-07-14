package com.esdemo.modules.service;

import com.esdemo.modules.bean.AgentInfo;

import java.util.List;

/**
 * @Title：agentApi2
 * @Description：三方代理商业务接口
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface ThreeAgentService {

    List<AgentInfo> getDirectChildThreeAgent(String agentNo);
}
