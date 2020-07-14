package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.TransDayBatch;
import com.esdemo.modules.bean.TransMonthBatch;
import com.esdemo.modules.dao.TransBatchDao;
import com.esdemo.modules.service.TransBatchService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
public class TransBatchServiceImpl implements TransBatchService {

    @Resource
    private TransBatchDao transBatchDao;

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public TransDayBatch getTransDayBatchByCollecTime(String collecTime) {
        return transBatchDao.getTransDayBatchByCollecTime(collecTime);
    }

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public BigDecimal sumTransDayTotalMoneyByParentId(String parentId, String collecTime) {
        Map<String, BigDecimal> sumMap = transBatchDao.sumTransDayTotalMoneyByParentId(parentId, collecTime);
        if (CollectionUtils.isEmpty(sumMap)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumResult = sumMap.get("subSumTotalAmount");
        return null == sumResult ? BigDecimal.ZERO : sumResult;

    }

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public TransMonthBatch getTransMonthBatchByCollecTime(String collecTime) {
        return transBatchDao.getTransMonthBatchByCollecTime(collecTime);
    }

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public BigDecimal sumTransMonthTotalMoneyByParentId(String parentId, String collecTime) {
        Map<String, BigDecimal> sumMap = transBatchDao.sumTransMonthTotalMoneyByParentId(parentId, collecTime);
        if (CollectionUtils.isEmpty(sumMap)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumResult = sumMap.get("subSumTotalAmount");
        return null == sumResult ? BigDecimal.ZERO : sumResult;
    }

    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertTransDayBatch(TransDayBatch transDayBatch) {
        return transBatchDao.insertTransDayBatch(transDayBatch);
    }

    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertTransMonthBatch(TransMonthBatch transMonthBatch) {
        return transBatchDao.insertTransMonthBatch(transMonthBatch);
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
        Map<String, Object> collectMap = transBatchDao.hasCollectDay(agentNo, beginOfMonth, endOfMonth);
        if (CollectionUtils.isEmpty(collectMap)) {
            collectMap = ImmutableMap.of("collectDay", "0", "collectCount", "0", "collectSum", "0", "collectTrans", "0");
        }
        return collectMap;
    }

    /**
     * 统计代理商分润收入数据
     *
     * @param timeUnit
     * @param agentNode
     * @param beginTimeStr
     * @param endTimeStr
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public List<Map<String, Object>> summaryProfitIncomeByTerms(TimeUnitEnum timeUnit, String agentNode, String beginTimeStr, String endTimeStr) {
        if (StringUtils.isBlank(agentNode)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> summaryList = transBatchDao.summaryProfitIncomeByTerms(timeUnit, agentNode, beginTimeStr, endTimeStr);
        return CollectionUtils.isEmpty(summaryList) ? new ArrayList<>() : summaryList;
    }
}