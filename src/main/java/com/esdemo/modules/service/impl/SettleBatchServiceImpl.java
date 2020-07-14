package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.modules.bean.SettleDayBatch;
import com.esdemo.modules.bean.SettleMonthBatch;
import com.esdemo.modules.dao.SettleBatchDao;
import com.esdemo.modules.service.SettleBatchService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
public class SettleBatchServiceImpl implements SettleBatchService {

    @Resource
    private SettleBatchDao settleBatchDao;

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public SettleDayBatch getSettleDayBatchByCollecTime(String collecTime) {
        return settleBatchDao.getSettleDayBatchByCollecTime(collecTime);
    }

    /**
     * 父代理商编号，汇总日期查询所有子代理商日汇总金额
     *
     * @param parentId
     * @param collecTime
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public BigDecimal sumSettleDayTotalMoneyByParentId(String parentId, String collecTime) {
        Map<String, BigDecimal> sumMap = settleBatchDao.sumSettleDayTotalMoneyByParentId(parentId, collecTime);
        if (CollectionUtils.isEmpty(sumMap)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumResult = sumMap.get("subSumTotalAmount");
        return null == sumResult ? BigDecimal.ZERO : sumResult;
    }

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public SettleMonthBatch getSettleMonthBatchByCollecTime(String collecTime) {
        return settleBatchDao.getSettleMonthBatchByCollecTime(collecTime);
    }

    /**
     * 父代理商编号，汇总日期查询所有子代理商月汇总金额
     *
     * @param parentId
     * @param collecTime
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public BigDecimal sumSettleMonthTotalMoneyByParentId(String parentId, String collecTime) {
        Map<String, BigDecimal> sumMap = settleBatchDao.sumSettleMonthTotalMoneyByParentId(parentId, collecTime);
        if (CollectionUtils.isEmpty(sumMap)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumResult = sumMap.get("subSumTotalAmount");
        return null == sumResult ? BigDecimal.ZERO : sumResult;
    }

    /**
     * 新增日汇总记录
     *
     * @param settleDayBatch
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertSettleDayBatch(SettleDayBatch settleDayBatch) {
        return settleBatchDao.insertSettleDayBatch(settleDayBatch);
    }

    /**
     * 新增月汇总记录
     *
     * @param settleMonthBatch
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertSettleMonthBatch(SettleMonthBatch settleMonthBatch) {
        return settleBatchDao.insertSettleMonthBatch(settleMonthBatch);
    }

    /**
     * 查看当前月内已经汇总的日数据
     *
     * @param agentNo
     * @param beginOfMonth
     * @param endOfMonth
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public Map<String, Object> hasCollectDay(String agentNo, String beginOfMonth, String endOfMonth) {
        Map<String, Object> collectMap = settleBatchDao.hasCollectDay(agentNo, beginOfMonth, endOfMonth);
        if (CollectionUtils.isEmpty(collectMap)) {
            collectMap = ImmutableMap.of("collectDay", "0", "collectCount", "0", "collectSum", "0");
        }
        return collectMap;
    }
}