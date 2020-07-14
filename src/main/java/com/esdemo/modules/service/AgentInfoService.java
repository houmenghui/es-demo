package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.*;
import org.elasticsearch.common.collect.Tuple;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description
 * @date 2019/5/20
 */
public interface AgentInfoService {

    /**
     * 查询代理商详情
     * @param agentNo
     * @return
     */
    AgentInfo queryAgentInfoByNo(String agentNo);

    /**
     * 新增下级代理商
     *
     * @param map
     * @param userInfoBean
     * @return
     */
    Map<String, Object> insertAgent(Map<String, Object> map, UserInfoBean userInfoBean);

    /**
     * 拓展代理,无须登录
     * @param agentInfo
     * @return
     */
    ResponseBean insertAgentExpand(AgentInfo agentInfo);

    /**
     * 查询下发返现金额及税额百分比
     *
     * @param agentNo
     * @param activityTypeNo
     * @return
     */
    Map<String, Object> selectByActivityTypeNo(String agentNo, String activityTypeNo);

    /**
     * 查询总开关的状态
     *
     * @return
     */
    Map<String, Object> selectDefaultStatus();

    /**
     * 数据字典配置满奖不满扣级别查询
     * @param currentAgentInfo
     * @return
     */
    Map<String, Object> getSupportRank(AgentInfo currentAgentInfo);

    /**
     * 添加欢乐返数据
     *
     * @param happyBackList
     * @return
     */
    int insertAgentActivity(List happyBackList);

    /**
     * 查询直接下级的代理商信息
     *
     * @param parentId 父级代理商
     * @param page     分页信息
     */
    Tuple<List<AgentInfo>, Long> listDirectChildren(String parentId, PageRequest page);

    AgentInfo queryAgentInfo(String agentNo);

    /**
     * 根据关键词查询代理商列表
     *
     * @param loginUserInfo 登陆代理商
     * @param agentType     - '1': 直接下级代理商(不含本级),
     *                      - '2': 查询所有链条下的代理商(含本级)
     *                      - 其他: 查询所有链条下的代理商(含本级)
     *                      - '3': 直属下级代理商(含本级),
     *                      - '4': 链条下所有代理商(不含本级),
     * @param keyword       代理商名字(模糊)/代理商编号(精确)/手机号(精确)
     * @param page          分页信息
     */
    Tuple<List<AgentInfo>, Long> listAgentInfoByKeyword(UserInfoBean loginUserInfo, String agentType, String keyword, PageRequest page);

    /**
     * 修改安全手机
     *
     * @param agentNo
     * @param safePhone
     * @return
     */
    int updateSafePhone(String agentNo, String safePhone);

    /**
     * 修改安全密码
     *
     * @param agentNo
     * @param safePassword
     * @return
     */
    int updateSafePassword(String agentNo, String safePassword);

    AgentInfo selectBelongAgent(String agentNo,String entityId);

    /**
     * 查询代理商基本信息
     * @param agentInfo
     * @return
     */
    List<AgentInfo> queryAgentInfoList(Map<String,String> agentInfo,UserInfoBean userInfoBean);

    /**
     * 通过代理商ID获取到代理的业务产品
     * @param agentNo
     * @return
     */
    List<BusinessProductDefine> getAgentProductList(String agentNo);

    /**
     * 根据代理商编号查询分润列表
     * @param param
     * @return
     */
    List<AgentShareRule> getAgentShareList(String param);

    /**
     * 根据代理商编号查询费率列表
     * @param agentNo
     * @return
     */
    List<ServiceRate> getAgentRateList(String agentNo);

    /**
     * 根据代理商编号查询限额
     * @param agentNo
     * @return
     */
    List<ServiceQuota> getAgentQuotaList(String agentNo);

    /**
     * 海报查询,返回阿里云地址
     * @return
     */
    List<Map<String, String>> selectPoster(UserInfoBean userInfoBean);

    /**
     * 获取分润服务信息
     * @param bpIds
     * @param agentNo
     * @return
     */
    List<ServiceRate> getAgentServices(List<String> bpIds, String agentNo);

    /**
     * 查询欢乐返活动,子类型
     * @param agentNo
     * @return
     */
    List<HappyBackData> selectHappyBack(String agentNo);

    /**
     * 转换显示
     * @param funcNum
     * @return
     */
    String getFunctionManagerByNum(String funcNum);

    /**
     * 获取所有直接下级代理商
     * @param agentNo
     * @return
     */
    List<AgentInfo> getAllDirectChildren(String agentNo);

    Map<String,Object> selectActivityBySn(String sn);

    List<Map<String,Object>> getAgentInfoByLevel(int level);

    int getMaxAgentLevel();

    Map<String, Object> getTodoStatus(String currAgentNo);
}
