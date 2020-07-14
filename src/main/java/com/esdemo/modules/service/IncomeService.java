package com.esdemo.modules.service;

import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.modules.bean.KeyValueBean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：收入Service
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public interface IncomeService {

    /**
     * 收入统计，不包含今日，从跑批表获取
     *
     * @param agentNode
     * @param agentNo
     * @param timeUnit
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<KeyValueBean> summaryIncomeByTerms(String agentNode, String agentNo, TimeUnitEnum timeUnit, int pageNo, int pageSize);

    /**
     * 统计今日所有收入，未跑批的（分润收入+活动补贴收入）
     *
     * @param agentNo
     * @return
     */
    BigDecimal sumTodayTotalIncome(String agentNo);

    /**
     * 统计指定时间当月收入，不包含今日，从跑批表获取
     *
     * @param agentNode
     * @param month
     * @param queryScope
     * @return 分润收入和活动补贴收入
     */
    Map<String, BigDecimal> sumMonthIncome(String agentNode, Date month, QueryScope queryScope);
}
