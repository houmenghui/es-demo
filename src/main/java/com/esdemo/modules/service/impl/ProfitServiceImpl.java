package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.utils.DataBundle;
import com.esdemo.modules.bean.ServiceQuota;
import com.esdemo.modules.bean.ServiceRate;
import com.esdemo.modules.dao.ProfitDao;
import com.esdemo.modules.service.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/29 14:42
 */
@Service
@Slf4j
public class ProfitServiceImpl implements ProfitService {
    @Resource
    ProfitDao profitDao;

    /*
     近6个月累计分润不包含当月
     */
    public String getAccumulatedIncome(String agent_no, String time_start,String time_end) {
        return profitDao.getAccumulatedIncome(agent_no, time_start, time_end);
    }

    /*
    获取今日分润
    */
    public String getTodayIncome(DataBundle data) {
        return profitDao.getTodayIncome(data.getString("agent_no"), data.getString("agent_level"), data.getString("agent_node"));
    }

    /*
    获取本月分润
    */
    public String getMonthIncome(String agent_no, String time_str) {
        return profitDao.getMonthIncome(agent_no, time_str);
    }

    /*
    获取分润账户
    */
    @DataSourceSwitch(value = DataSourceType.BILL)
    public Map<String, Object> getShareAccount(String agent_no) {
        return profitDao.getShareAccount(agent_no);
    }

    /*
    获取活动补贴
    */
    @DataSourceSwitch(value = DataSourceType.BILL)
    public Map<String, Object> getActivitySubsidy(String agent_no) {
        return profitDao.getActivitySubsidy(agent_no);
    }

    /*
   查询我的分润趋势
   */
    public String getProfitTendency(DataBundle data) {
        return profitDao.getProfitTendency(data.get("select_type"), data.get("agent_no"), data.get("start_time"), data.get("end_time"));
    }

    public List<Map<String, Object>> getProfitTendencyGroupByTime(DataBundle data) {
        return profitDao.getProfitTendencyGroupByTime(data.get("select_type"), data.get("agent_no"), data.get("start_time"), data.get("end_time"));
    }

    /*
   查询账户明细
   */
    @DataSourceSwitch(value = DataSourceType.BILL)
    public List<Map<String, Object>> getProfitDetail(Map<String, Object> params_map) {
        return profitDao.getProfitDetail(params_map);
    }

    /*
   查询账户统计
   */
    @DataSourceSwitch(value = DataSourceType.BILL)
    public Map<String, Object> getProfitCount(Map<String, Object> params_map) {
        return profitDao.getProfitCount(params_map);
    }

    /**
     * 查询总开关的状态
     * @return
     */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public Map<String, Object> selectDefaultStatus() {
        return profitDao.selectDefaultStatus();
    }

    /**
     * 根据代理商编号查询到当前代理商设置的留存金额
     * @param agent_no
     * @return
     */
    public Map<String, Object>  selectRetainAmount(String agent_no) {
        return profitDao.selectRetainAmount(agent_no);
    }

    //查询安全密码
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public String getSafePassword(String agentNo){
        return profitDao.getSafePassword(agentNo);
    }

    /*
   查询服务信息表
    */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public List<Map<String, Object>> selectByServiceType(String serviceType) {
        return profitDao.selectByServiceType(serviceType);
    }

    /**
     * 海涛,国栋,水育确认上游金额小于设置金额,自动关闭通道开关,然后账务手动开启
     *
     * @param id
     * @return
     */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public Integer updateWithdrawSwitch(Integer id) {
        return profitDao.updateWithdrawSwitch(id);
    }

    /**
     * 获取一级服务费率
     * @param serviceId
     * @return
     */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ServiceRate getFristAgentServiceRateById(String serviceId) {
        return profitDao.getFristAgentServiceRateById(serviceId);
    }

    /*
    获取服务管控限额
     */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public ServiceQuota queryHlsServiceQuota() {
        return profitDao.queryHlsServiceQuota();
    }

    /*
    是否可以提现
    */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public boolean canWithdrawCash(Long serviceId) {
        return profitDao.canWithdrawCash(serviceId);
    }

    /*
   获取提现信息
    */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public Map<String, Object>  findWithDrawCash(String entityId, String subType) {
        return profitDao.findWithDrawCash(entityId, subType);
    }

    /*
    记录提现记录
    */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int insertWithDrawCash(Map<String, Object> map) {
        return profitDao.insertWithDrawCash(map);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.BILL)
    public Map<String, Object> getPreFreezeInActivitySubSidy(String agent_no) {
        return profitDao.getPreFreezeInActivitySubSidy(agent_no);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.BILL)
    public Map<String, Object> getPreFreezeInShareAccount(String agent_no) {
        return profitDao.getPreFreezeInShareAccount(agent_no);
    }
}
