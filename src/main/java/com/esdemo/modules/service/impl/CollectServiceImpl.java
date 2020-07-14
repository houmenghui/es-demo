package com.esdemo.modules.service.impl;

import com.esdemo.modules.dao.CollectDao;
import com.esdemo.modules.service.CollectService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/8/2 17:58
 * @Version：1.0
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    @Resource
    private CollectDao collectDao;

    /**
     * 根据代理商、时间汇总返现记录
     *
     * @param agentNo
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Map<String, Object> collectCashBackByAgentAndTime(String agentNo, String beginTime, String endTime) {
        Map<String, Object> collectMap = collectDao.collectCashBackByAgentAndTime(agentNo, beginTime, endTime);
        if (CollectionUtils.isEmpty(collectMap)) {
            collectMap = ImmutableMap.of("collectCount", "0", "collectSum", "0.00");
        }
        return collectMap;
    }

    @Override
    public Map<String, Object> collectTransShareByAgentAndTime(String agentNode, String agentLevel, String beginTime, String endTime) {
        Map<String, Object> collectMap = collectDao.collectTransByAgentAndTime(agentNode + "%", agentLevel, beginTime, endTime);
        if (CollectionUtils.isEmpty(collectMap)) {
            collectMap = ImmutableMap.of("collectCount", "0", "collectSum", "0.00", "collectSumTrans", "0.00");
        }
        return collectMap;
    }

    @Override
    public Map<String, Object> collectSettleByAgentAndTime(String agentNode, String agentLevel, String beginTime, String endTime) {
        Map<String, Object> collectMap = collectDao.collectSettleByAgentAndTime(agentNode + "%", agentLevel, beginTime, endTime);
        if (CollectionUtils.isEmpty(collectMap)) {
            collectMap = ImmutableMap.of("collectCount", "0", "collectSum", "0.00");
        }
        return collectMap;
    }
}