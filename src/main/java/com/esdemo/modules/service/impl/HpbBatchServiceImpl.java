package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.HpbDayBatch;
import com.esdemo.modules.bean.HpbMonthBatch;
import com.esdemo.modules.dao.HpbBatchDao;
import com.esdemo.modules.service.HpbBatchService;
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
public class HpbBatchServiceImpl implements HpbBatchService {

    @Resource
    private HpbBatchDao hpbBatchDao;

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public HpbDayBatch getHpbDayBatchByCollecTime(String collecTime) {
        return hpbBatchDao.getHpbDayBatchByCollecTime(collecTime);
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
    public BigDecimal sumHpbDayTotalMoneyByParentId(String parentId, String collecTime) {
        Map<String, BigDecimal> sumMap = hpbBatchDao.sumHpbDayTotalMoneyByParentId(parentId, collecTime);
        if (CollectionUtils.isEmpty(sumMap)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumResult = sumMap.get("subSumTotalAmount");
        return null == sumResult ? BigDecimal.ZERO : sumResult;
    }

    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public HpbMonthBatch getHpbMonthBatchByCollecTime(String collecTime) {
        return hpbBatchDao.getHpbMonthBatchByCollecTime(collecTime);
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
    public BigDecimal sumHpbMonthTotalMoneyByParentId(String parentId, String collecTime) {
        Map<String, BigDecimal> sumMap = hpbBatchDao.sumHpbMonthTotalMoneyByParentId(parentId, collecTime);
        if (CollectionUtils.isEmpty(sumMap)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumResult = sumMap.get("subSumTotalAmount");
        return null == sumResult ? BigDecimal.ZERO : sumResult;
    }

    /**
     * 新增日汇总记录
     *
     * @param hpbDayBatch
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertHpbDayBatch(HpbDayBatch hpbDayBatch) {
        return hpbBatchDao.insertHpbDayBatch(hpbDayBatch);
    }

    /**
     * 新增月汇总记录
     *
     * @param hpbMonthBatch
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertHpbMonthBatch(HpbMonthBatch hpbMonthBatch) {
        return hpbBatchDao.insertHpbMonthBatch(hpbMonthBatch);
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
        Map<String, Object> collectMap = hpbBatchDao.hasCollectDay(agentNo, beginOfMonth, endOfMonth);
        if (CollectionUtils.isEmpty(collectMap)) {
            collectMap = ImmutableMap.of("collectDay", "0", "collectCount", "0", "collectSum", "0");
        }
        return collectMap;
    }

    /**
     * 统计代理商活动补贴收入数据
     *
     * @param timeUnit
     * @param agentNode
     * @param beginTimeStr
     * @param endTimeStr
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.READ)
    public List<Map<String, Object>> summaryHpbIncomeByTerms(TimeUnitEnum timeUnit, String agentNode, String beginTimeStr, String endTimeStr) {
        if(StringUtils.isBlank(agentNode)){
            return new ArrayList<>();
        }
        List<Map<String, Object>> summaryList = hpbBatchDao.summaryHpbIncomeByTerms(timeUnit, agentNode, beginTimeStr, endTimeStr);
        return CollectionUtils.isEmpty(summaryList) ? new ArrayList<>() : summaryList;
    }
}