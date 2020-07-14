package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.UserInfoBean;

import java.util.List;
import java.util.Map;

/**
 * @author lmc liuks
 * @date 2019/5/16 14:51
 */
public interface MachineManageService {
    /**
     * 获取该用户下所有机具信息
     */
    ResponseBean getAllByCondition(UserInfoBean userInfoBean, String params);

    /**
     *机具管理-机具下发/回收操作
     */
    ResponseBean manageTerminal(UserInfoBean userInfoBean,String params);


    /**
     *机具管理-机具下发/回收操作
     */
    ResponseBean terminalRelease(UserInfoBean userInfoBean,String params);


    /**
     * 查询代理商功能开关
     */
    Map<String, Object> getFunctionManage(String function_number);


    /**
     * 机具流动记录查询
     */
    List<Map<String, Object>> getSnSendAndRecInfo(Map<String, Object> params_map);

    /**
     * 机具流动详情查询
     */
    String getSnSendAndRecDetail(String id);

    /**
     * 获取代理商权限控制信息
     */
    String getAgentFunction(String agent_no, String function_number);

    /**
     * 是否黑名单 不包含下级
     */
    long countBlacklistNotContains(String agentNo);

    /**
     * 是否黑名单 包含下级
     */
    long countBlacklistContains(String agentNode);

    /*
    查询当前代理商的一级代理商勾选的欢乐返子类型
     */
    List<Map<String, Object>> getActivityTypes(String agent_no);
}
