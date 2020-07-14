package com.esdemo.modules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.IncomeDetail;
import com.esdemo.modules.bean.KeyValueBean;
import com.esdemo.modules.dao.TransBatchDao;
import com.esdemo.modules.service.HpbBatchService;
import com.esdemo.modules.service.IncomeService;
import com.esdemo.modules.service.TransBatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Title：agentApi2
 * @Description：收入Service实现
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Slf4j
@Service
public class IncomeServiceImpl implements IncomeService {

    @Resource
    private TransBatchService transBatchService;
    @Resource
    private HpbBatchService hpbBatchService;
    @Resource
    private TransBatchDao transBatchDao;

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
    @Override
    @CacheData
    public List<KeyValueBean> summaryIncomeByTerms(String agentNode, String agentNo, TimeUnitEnum timeUnit, int pageNo, int pageSize) {
        List<KeyValueBean> resList = new ArrayList<>();
        if (StringUtils.isBlank(agentNode, agentNo)) {
            return resList;
        }
        Date now = new Date();
        //默认从2019-11-01开始统计
        Date minDate = DateUtil.parse("2019-11-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        String beginTimeStr = "", endTimeStr = "";
        int beginIndex = (pageNo - 1) * pageSize;
        int loopIndex = 1;
        List<Map<String, Object>> profitRes = new ArrayList<>();
        List<Map<String, Object>> hpbRes = new ArrayList<>();

        //按日维度查询
        if (TimeUnitEnum.DAY == timeUnit) {
            String yyyyMMdd;
            //不包含当天
            Date endDate = DateUtils.addDays(now, (beginIndex + 1) * -1);
            Date beginDate = DateUtils.addDays(endDate, (pageSize - 1) * -1);

            if (DateUtil.beginOfDay(endDate).before(minDate)) {
                return resList;
            }
            if (DateUtil.beginOfDay(beginDate).before(minDate)) {
                beginDate = minDate;
            }

            beginTimeStr = DateUtil.format(beginDate, "yyyy-MM-dd") + " 00:00:00";
            endTimeStr = DateUtil.format(endDate, "yyyy-MM-dd") + " 23:59:59";

            profitRes = transBatchService.summaryProfitIncomeByTerms(TimeUnitEnum.DAY, agentNode, beginTimeStr, endTimeStr);
            hpbRes = hpbBatchService.summaryHpbIncomeByTerms(TimeUnitEnum.DAY, agentNode, beginTimeStr, endTimeStr);

            Date loopDate = endDate;
            while (!loopDate.before(beginDate)) {
                yyyyMMdd = DateUtil.format(loopDate, "yyyy-MM-dd");
                resList.add(new KeyValueBean(yyyyMMdd, implIncomeSummary(profitRes, hpbRes, yyyyMMdd)));
                loopDate = DateUtils.addDays(loopDate, -1);
            }
        }
        //按月维度查询
        if (TimeUnitEnum.MONTH == timeUnit) {
            String yyyyMM;
            //包含当月不包含当天，当月数据从日统计表查询，
            int currMonth = DateUtil.month(now) + 1;
            int isQueryDay = 0;
            if (pageNo == 1) {
                Date beforeDay = DateUtils.addDays(now, -1);
                int beforeDayMonth = DateUtil.month(beforeDay) + 1;
                if (currMonth == beforeDayMonth) {
                    yyyyMM = DateUtil.format(beforeDay, "yyyy-MM");

                    beginTimeStr = DateUtil.format(DateUtil.beginOfMonth(beforeDay), "yyyy-MM-dd") + " 00:00:00";
                    endTimeStr = DateUtil.format(beforeDay, "yyyy-MM-dd") + " 23:59:59";

                    profitRes = transBatchService.summaryProfitIncomeByTerms(TimeUnitEnum.DAY, agentNode, beginTimeStr, endTimeStr);
                    hpbRes = hpbBatchService.summaryHpbIncomeByTerms(TimeUnitEnum.DAY, agentNode, beginTimeStr, endTimeStr);

                    BigDecimal profitTotal = BigDecimal.ZERO;
                    BigDecimal profitAcc = BigDecimal.ZERO;
                    BigDecimal hpbTotal = BigDecimal.ZERO;
                    BigDecimal hpbAcc = BigDecimal.ZERO;
                    //逐日累加
                    if (!CollectionUtils.isEmpty(profitRes)) {
                        for (Map<String, Object> profitMap : profitRes) {
                            String profitTotalStr = StringUtils.filterNull(profitMap.get("total_money"));
                            String profitAccStr = StringUtils.filterNull(profitMap.get("acc_money"));
                            profitTotal = profitTotal.add(StringUtils.isBlank(profitTotalStr) ? BigDecimal.ZERO : new BigDecimal(profitTotalStr));
                            profitAcc = profitAcc.add(StringUtils.isBlank(profitAccStr) ? BigDecimal.ZERO : new BigDecimal(profitAccStr));
                        }
                    }
                    if (!CollectionUtils.isEmpty(hpbRes)) {
                        for (Map<String, Object> hpbMap : hpbRes) {
                            String hpbTotalStr = StringUtils.filterNull(hpbMap.get("total_money"));
                            String hpbAccStr = StringUtils.filterNull(hpbMap.get("acc_money"));
                            hpbTotal = hpbTotal.add(StringUtils.isBlank(hpbTotalStr) ? BigDecimal.ZERO : new BigDecimal(hpbTotalStr));
                            hpbAcc = hpbAcc.add(StringUtils.isBlank(hpbAccStr) ? BigDecimal.ZERO : new BigDecimal(hpbAccStr));
                        }
                    }
                    IncomeDetail incomeDetail = new IncomeDetail();
                    incomeDetail.setProfitIncome(profitTotal);
                    incomeDetail.setLeftProfitIncome(profitAcc);
                    incomeDetail.setHpbIncome(hpbTotal);
                    incomeDetail.setLeftHpbIncome(hpbAcc);

                    resList.add(new KeyValueBean(yyyyMM, incomeDetail));
                    isQueryDay = 1;
                }
            }
            Date endDate = DateUtils.addMonths(now, (beginIndex + 1) * -1);
            Date beginDate = DateUtils.addMonths(endDate, (pageSize - 1 - isQueryDay) * -1);
            if (DateUtil.beginOfDay(endDate).before(minDate)) {
                return resList;
            }
            if (DateUtil.beginOfDay(beginDate).before(minDate)) {
                beginDate = minDate;
            }
            endDate = DateUtil.endOfMonth(endDate);
            beginDate = DateUtil.beginOfMonth(beginDate);

            beginTimeStr = DateUtil.format(beginDate, "yyyy-MM-dd") + " 00:00:00";
            endTimeStr = DateUtil.format(endDate, "yyyy-MM-dd") + " 23:59:59";

            profitRes = transBatchService.summaryProfitIncomeByTerms(TimeUnitEnum.MONTH, agentNode, beginTimeStr, endTimeStr);
            hpbRes = hpbBatchService.summaryHpbIncomeByTerms(TimeUnitEnum.MONTH, agentNode, beginTimeStr, endTimeStr);

            Date loopDate = endDate;
            while (!loopDate.before(beginDate)) {
                yyyyMM = DateUtil.format(loopDate, "yyyy-MM");
                resList.add(new KeyValueBean(yyyyMM, implIncomeSummary(profitRes, hpbRes, yyyyMM)));
                loopDate = DateUtils.addMonths(loopDate, -1);
            }
        }
        return resList;
    }

    /**
     * 统计今日所有收入，未跑批的（分润收入+活动补贴收入）
     *
     * @param agentNo
     * @return
     */
    @Override
    @CacheData
    public BigDecimal sumTodayTotalIncome(String agentNo) {
        Map<String, BigDecimal> sumRes = transBatchDao.sumTodayTotalIncome(agentNo);
        if (CollectionUtils.isEmpty(sumRes)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumBig = sumRes.get("cnt_amount");
        return null == sumBig ? BigDecimal.ZERO : sumBig;
    }

    /**
     * 统计指定时间当月收入，不包含今日，从跑批表获取
     *
     * @param agentNode
     * @param month
     * @param queryScope
     * @return 分润收入和活动补贴收入
     */
    @Override
    @CacheData
    public Map<String, BigDecimal> sumMonthIncome(String agentNode, Date month, QueryScope queryScope) {
        Map<String, BigDecimal> incomeMap = new HashMap<>();
        BigDecimal profitIncome = BigDecimal.ZERO;
        BigDecimal hpbIncome = BigDecimal.ZERO;

        String beginTimeStr = "", endTimeStr = "";
        Date now = new Date();
        month = null == month ? now : month;
        Date beginOfMonth = DateUtil.beginOfMonth(month);
        beginTimeStr = DateUtil.format(beginOfMonth, "yyyy-MM-dd") + " 00:00:00";
        //当日前一天
        Date beforeDay = DateUtils.addDays(now, -1);
        endTimeStr = DateUtil.format(beforeDay, "yyyy-MM-dd") + " 23:59:59";

        List<Map<String, Object>> profitRes = transBatchService.summaryProfitIncomeByTerms(TimeUnitEnum.DAY, agentNode, beginTimeStr, endTimeStr);
        List<Map<String, Object>> hpbRes = hpbBatchService.summaryHpbIncomeByTerms(TimeUnitEnum.DAY, agentNode, beginTimeStr, endTimeStr);

        BigDecimal profitTotal = BigDecimal.ZERO;
        BigDecimal profitAcc = BigDecimal.ZERO;
        BigDecimal hpbTotal = BigDecimal.ZERO;
        BigDecimal hpbAcc = BigDecimal.ZERO;
        //逐日累加
        if (!CollectionUtils.isEmpty(profitRes)) {
            for (Map<String, Object> profitMap : profitRes) {
                String profitTotalStr = StringUtils.filterNull(profitMap.get("total_money"));
                String profitAccStr = StringUtils.filterNull(profitMap.get("acc_money"));
                profitTotal = profitTotal.add(StringUtils.isBlank(profitTotalStr) ? BigDecimal.ZERO : new BigDecimal(profitTotalStr));
                profitAcc = profitAcc.add(StringUtils.isBlank(profitAccStr) ? BigDecimal.ZERO : new BigDecimal(profitAccStr));
            }
        }
        if (!CollectionUtils.isEmpty(hpbRes)) {
            for (Map<String, Object> hpbMap : hpbRes) {
                String hpbTotalStr = StringUtils.filterNull(hpbMap.get("total_money"));
                String hpbAccStr = StringUtils.filterNull(hpbMap.get("acc_money"));
                hpbTotal = hpbTotal.add(StringUtils.isBlank(hpbTotalStr) ? BigDecimal.ZERO : new BigDecimal(hpbTotalStr));
                hpbAcc = hpbAcc.add(StringUtils.isBlank(hpbAccStr) ? BigDecimal.ZERO : new BigDecimal(hpbAccStr));
            }
        }
        //查询范围
        switch (queryScope) {
            case ALL: {
                //全部
                profitIncome = profitTotal;
                hpbIncome = hpbTotal;
                break;
            }
            case OFFICAL: {
                //直营
                profitIncome = profitAcc;
                hpbIncome = hpbAcc;
                break;
            }
            case CHILDREN: {
                //下级
                profitIncome = profitTotal.subtract(profitAcc);
                hpbIncome = hpbTotal.subtract(hpbAcc);
                break;
            }
            default: {
                //全部
                profitIncome = profitTotal;
                hpbIncome = hpbTotal;
                break;
            }
        }
        //可以为负数
        incomeMap.put("profitIncome", profitIncome);
        incomeMap.put("hpbIncome", hpbIncome);
        return incomeMap;
    }

    /**
     * 明细统计实现，供summaryIncomeByTerms调用
     *
     * @param profitRes
     * @param hpbRes
     * @param timeKey
     */
    private IncomeDetail implIncomeSummary(List<Map<String, Object>> profitRes, List<Map<String, Object>> hpbRes, String timeKey) {
        IncomeDetail incomeDetail = new IncomeDetail();
        String mapTime = "";
        String mapTotalMoney = "";
        String mapAccMoney = "";

        for (Map<String, Object> map : profitRes) {
            mapTime = StringUtils.filterNull(map.get("collect_time"));
            if (timeKey.equalsIgnoreCase(mapTime)) {
                mapTotalMoney = StringUtils.filterNull(map.get("total_money"));
                mapAccMoney = StringUtils.filterNull(map.get("acc_money"));
                incomeDetail.setProfitIncome(StringUtils.isBlank(mapTotalMoney) ? BigDecimal.ZERO : new BigDecimal(mapTotalMoney));
                incomeDetail.setLeftProfitIncome(StringUtils.isBlank(mapAccMoney) ? BigDecimal.ZERO : new BigDecimal(mapAccMoney));
            }
        }
        for (Map<String, Object> map : hpbRes) {
            mapTime = StringUtils.filterNull(map.get("collect_time"));
            if (timeKey.equalsIgnoreCase(mapTime)) {
                mapTotalMoney = StringUtils.filterNull(map.get("total_money"));
                mapAccMoney = StringUtils.filterNull(map.get("acc_money"));
                incomeDetail.setHpbIncome(StringUtils.isBlank(mapTotalMoney) ? BigDecimal.ZERO : new BigDecimal(mapTotalMoney));
                incomeDetail.setLeftHpbIncome(StringUtils.isBlank(mapAccMoney) ? BigDecimal.ZERO : new BigDecimal(mapAccMoney));
            }
        }
        return incomeDetail;
    }
}
