package com.esdemo.modules.service;

import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.modules.bean.HpbDayBatch;
import com.esdemo.modules.bean.HpbMonthBatch;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/8/2 17:53
 * @Version：1.0
 */
public interface HpbBatchService {

    HpbDayBatch getHpbDayBatchByCollecTime(String collecTime);

    BigDecimal sumHpbDayTotalMoneyByParentId(String parentId, String collecTime);

    HpbMonthBatch getHpbMonthBatchByCollecTime(String collecTime);

    BigDecimal sumHpbMonthTotalMoneyByParentId(String parentId, String collecTime);

    int insertHpbDayBatch(HpbDayBatch hpbDayBatch);

    int insertHpbMonthBatch(HpbMonthBatch hpbMonthBatch);

    Map<String, Object> hasCollectDay(String agentNo, String beginOfMonth, String endOfMonth);

    List<Map<String, Object>> summaryHpbIncomeByTerms(TimeUnitEnum timeUnit, String agentNode, String beginTimeStr, String endTimeStr);
}