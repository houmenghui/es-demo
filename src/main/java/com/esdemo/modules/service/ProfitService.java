package com.esdemo.modules.service;

import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.utils.DataBundle;
import com.esdemo.modules.bean.ServiceQuota;
import com.esdemo.modules.bean.ServiceRate;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/29 14:41
 */
public interface ProfitService {
    /*
    近6个月累计分润不包含当月
    */
    String getAccumulatedIncome(String agent_no, String time_start,String time_end);

    /*
    获取今日分润
    */
    @CacheData
    String getTodayIncome(DataBundle data);

    /*
    获取本月分润
    */
    String getMonthIncome(String agent_no, String time_str);

    /*
    获取分润账户
    */
    Map<String, Object> getShareAccount(String agent_no);

    /*
    获取活动补贴
    */
    Map<String, Object> getActivitySubsidy(String agent_no);

    /*
    查询我的分润趋势
    */
    String getProfitTendency(DataBundle data);

    List<Map<String, Object>> getProfitTendencyGroupByTime(DataBundle data);

    /*
    查询账户明细
    */
    List<Map<String, Object>> getProfitDetail(Map<String, Object> params_map);

    /*
   查询账户统计
   */
    Map<String, Object> getProfitCount(Map<String, Object> params_map);

    /**
     * 查询总开关的状态
     * @return
     */
    Map<String, Object> selectDefaultStatus();

    /**
     * 根据代理商编号查询到当前代理商设置的留存金额
     * @param agent_no
     * @return
     */
     Map<String, Object> selectRetainAmount(String agent_no);

    //查询安全密码
    public String getSafePassword(String agentNo);

    /*
    查询服务信息表
    */
    List<Map<String, Object>> selectByServiceType(String serviceType);

    /**
     * 海涛,国栋,水育确认上游金额小于设置金额,自动关闭通道开关,然后账务手动开启
     * @param id
     * @return
     */
    Integer updateWithdrawSwitch(Integer id);

    /**
     * 获取一级服务费率
     * @param serviceId
     * @return
     */
    ServiceRate getFristAgentServiceRateById(String serviceId);

    /*
   获取服务管控限额
    */
    ServiceQuota queryHlsServiceQuota();

    /*
   是否可以提现
   */
    boolean canWithdrawCash(Long serviceId);

    /*
    获取提现信息
    */
    Map<String, Object> findWithDrawCash(String entityId, String subType);

    /*
    记录提现记录
    */
    int insertWithDrawCash(Map<String, Object> map);

    /**
     * 活动补贴预冻结金额
     * @param agent_no
     * @return
     */
    Map<String, Object> getPreFreezeInActivitySubSidy(String agent_no);

    Map<String, Object> getPreFreezeInShareAccount(String agent_no);
}
