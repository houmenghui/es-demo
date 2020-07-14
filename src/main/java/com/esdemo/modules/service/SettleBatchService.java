package com.esdemo.modules.service;


import com.esdemo.modules.bean.SettleDayBatch;
import com.esdemo.modules.bean.SettleMonthBatch;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/8/2 17:53
 * @Version：1.0
 */
public interface SettleBatchService {

    SettleDayBatch getSettleDayBatchByCollecTime(String collecTime);

    BigDecimal sumSettleDayTotalMoneyByParentId(String parentId, String collecTime);

    SettleMonthBatch getSettleMonthBatchByCollecTime(String collecTime);

    BigDecimal sumSettleMonthTotalMoneyByParentId(String parentId, String collecTime);

    int insertSettleDayBatch(SettleDayBatch settleDayBatch);

    int insertSettleMonthBatch(SettleMonthBatch settleMonthBatch);

    Map<String, Object> hasCollectDay(String agentNo, String beginOfMonth, String endOfMonth);
}