package com.esdemo.modules.service;

import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.modules.bean.TransDayBatch;
import com.esdemo.modules.bean.TransMonthBatch;

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
public interface TransBatchService {

    TransDayBatch getTransDayBatchByCollecTime(String collecTime);

    BigDecimal sumTransDayTotalMoneyByParentId(String parentId, String collecTime);

    TransMonthBatch getTransMonthBatchByCollecTime(String collecTime);

    BigDecimal sumTransMonthTotalMoneyByParentId(String parentId, String collecTime);

    int insertTransDayBatch(TransDayBatch TransDayBatch);

    int insertTransMonthBatch(TransMonthBatch TransMonthBatch);

    Map<String, Object> hasCollectDay(String agentNo, String beginOfMonth, String endOfMonth);

    List<Map<String, Object>> summaryProfitIncomeByTerms(TimeUnitEnum timeUnit, String agentNode, String beginTimeStr, String endTimeStr);
}