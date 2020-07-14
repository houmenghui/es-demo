package com.esdemo.modules.service;

import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.UserInfoBean;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 机具申请
 * @date 2019/5/24
 */
public interface TerminalApplyRecordService {

    /**
     * 机具申请记录查询
     * @param paramMap
     * @return
     */
    List<Map<String,Object>> getTerminalApplyRecord(Map<String,String> paramMap);

    /**
     * 修改机具申请记录
     * @param params 参数
     * @return true为修改成功，反之修改失败
     * @author ZengJA
     * @date 2017-08-09 15:45:48
     */
    Map<String,Object> updateModifyTerminalApplyRecord(Map<String,String> params,UserInfoBean userInfoBean);

    AgentInfo getAgentInfoByNo(String param);

    /**
     * 查询机具信息
     * @param params
     * @return
     */
    Map<String, Object> getTerminalInfo(Map<String, String> params);

    /**
     * 查询商户信息-单表
     * @param params
     * @return
     * @author ZengJA
     * @date 2017-10-26 09:40:43
     */
    Map<String, Object> getMerInfo(Map<String, String> params);

    /**
     * 分配机具至指定代理商名下
     * @param params
     * @return true为修改成功，反之修改失败
     * @author ZengJA
     * @date 2017-10-20 15:27:42
     */
    boolean distributionTerminalInfo(Map<String,String> params);

    /**
     * 统计代理商名下机具申请记录数
     * @param agentNode 当前登录代理商节点
     * @return
     * @author ZengJA
     * @date 2017-08-09 15:45:52
     */
    int countTerminalApplyRecord(String agentNode);

    /**
     * 获取所有活动信息
     * @return
     */
    List<Map<String,Object>> getAllActivityInfo();
}
