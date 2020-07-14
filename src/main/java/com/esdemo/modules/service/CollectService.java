package com.esdemo.modules.service;

import java.util.Map;

/**
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/8/2 17:53
 * @Version：1.0
 */
public interface CollectService {


    Map<String, Object> collectCashBackByAgentAndTime(String agentNo, String beginTime, String endTime);

    Map<String, Object> collectTransShareByAgentAndTime(String agentNode, String agentLevel, String beginTime, String endTime);

    Map<String, Object> collectSettleByAgentAndTime(String agentNode, String agentLevel, String beginTime, String endTime);


}