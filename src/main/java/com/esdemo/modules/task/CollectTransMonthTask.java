package com.esdemo.modules.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.config.CallableHelper;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.TransMonthBatch;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.CollectService;
import com.esdemo.modules.service.TaskRecordService;
import com.esdemo.modules.service.TransBatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 月交易收益汇总定时任务
 *
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/7/16 11:48
 * @Version：1.0
 */
@Slf4j
public class CollectTransMonthTask {

    private static CollectService collectService = SpringHolder.getBean(CollectService.class);
    private static TransBatchService transBatchService = SpringHolder.getBean(TransBatchService.class);
    private static AgentInfoService agentService = SpringHolder.getBean(AgentInfoService.class);
    private static TaskRecordService taskRecordService = SpringHolder.getBean(TaskRecordService.class);
    //必须保证同一时刻只能允许一个线程执行
    private static final Object lock = new Object();

    /**
     * 支持汇总具体哪一个月和汇总多少个月数据（不能都不为空）
     */
    private String collectMonthTime;
    private Integer collectMonthNum;


    public CollectTransMonthTask(Object inCollectMonthTime, Object inCollectMonthNum) {
        if (inCollectMonthTime != null && inCollectMonthNum != null) {
            return;
        }
        this.collectMonthTime = inCollectMonthTime == null ? null : StringUtils.filterNull(inCollectMonthTime);
        String strInCollectDayNum = StringUtils.filterNull(inCollectMonthNum);
        this.collectMonthNum = StringUtils.isBlank(strInCollectDayNum) ? null : Integer.parseInt(strInCollectDayNum);
    }

    /**
     * 月交易收益汇总
     */
    public void implCollectTransMonth() {
        //获取代理商最大级别，从1开始
        int maxLevel = agentService.getMaxAgentLevel();
        if (maxLevel < 1) {
            return;
        }
        long beginTimeMillis = System.currentTimeMillis();

        Date now = new Date();
        Date monthDate = null;
        Date beginOfMonth = null;
        String monthTime = null;
        TransMonthBatch dbBatch = null;
        try {
            synchronized (lock) {
                //如果是指定跑多少月
                if (null != collectMonthNum && collectMonthNum > 0) {
                    int copyCollectMonthNum = collectMonthNum.intValue();
                    for (; copyCollectMonthNum > 0; copyCollectMonthNum--) {
                        monthDate = DateUtils.addMonths(new Date(), copyCollectMonthNum * -1);
                        beginOfMonth = DateUtil.beginOfMonth(monthDate);
                        monthTime = DateUtil.format(beginOfMonth, "yyyy-MM-dd");
                        dbBatch = transBatchService.getTransMonthBatchByCollecTime(monthTime);
                        if (null != dbBatch) {
                            continue;
                        }
                        for (int level = maxLevel; level >= 1; level--) {
                            if (level > 20) {
                                continue;
                            }
                            List<Map<String, Object>> agentList = agentService.getAgentInfoByLevel(level);
                            if (!CollectionUtil.isEmpty(agentList)) {
                                try {
                                    InsideCollectTransMonth insideClass = new InsideCollectTransMonth();
                                    CallableHelper.executeCallable(insideClass, agentList, new Class[]{List.class, Date.class}, new Object[]{beginOfMonth}, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                //如果指定跑哪一月
                else if (StringUtils.isNotBlank(collectMonthTime)) {
                    monthDate = DateUtil.parse(collectMonthTime, "yyyy-MM-dd");
                    beginOfMonth = DateUtil.beginOfMonth(monthDate);
                    monthTime = DateUtil.format(beginOfMonth, "yyyy-MM-dd");
                    dbBatch = transBatchService.getTransMonthBatchByCollecTime(monthTime);
                    if (null == dbBatch) {
                        for (int level = maxLevel; level >= 1; level--) {
                            if (level > 20) {
                                continue;
                            }
                            List<Map<String, Object>> agentList = agentService.getAgentInfoByLevel(level);
                            if (!CollectionUtil.isEmpty(agentList)) {
                                try {
                                    InsideCollectTransMonth insideClass = new InsideCollectTransMonth();
                                    CallableHelper.executeCallable(insideClass, agentList, new Class[]{List.class, Date.class}, new Object[]{beginOfMonth}, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                //默认只跑前一月
                else {
                    monthDate = DateUtils.addMonths(new Date(), -1);
                    beginOfMonth = DateUtil.beginOfMonth(monthDate);
                    monthTime = DateUtil.format(beginOfMonth, "yyyy-MM-dd");
                    dbBatch = transBatchService.getTransMonthBatchByCollecTime(monthTime);
                    if (null == dbBatch) {
                        for (int level = maxLevel; level >= 1; level--) {
                            if (level > 20) {
                                continue;
                            }
                            List<Map<String, Object>> agentList = agentService.getAgentInfoByLevel(level);
                            if (!CollectionUtil.isEmpty(agentList)) {
                                try {
                                    InsideCollectTransMonth insideClass = new InsideCollectTransMonth();
                                    CallableHelper.executeCallable(insideClass, agentList, new Class[]{List.class, Date.class}, new Object[]{beginOfMonth}, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("代理商月交易收益汇总任务异常：{}", e);
        }

        long endTimeMillis = System.currentTimeMillis();
        log.info("代理商月交易收益汇总任务完成耗时:{}ms", endTimeMillis - beginTimeMillis);

        try {
            //任务回调
            String classWholeName = this.getClass().getName();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            taskRecordService.taskRunningCallback(classWholeName + "." + methodName);
        } catch (Exception e) {
            log.error("代理商月交易收益汇总------>回调定时任务系统异常");
            e.printStackTrace();
        }
    }

    public static class InsideCollectTransMonth implements Callable<List<String>> {

        private List<Map<String, Object>> subList;
        private Date date;

        public InsideCollectTransMonth(List<Map<String, Object>> subList, Date date) {

            this.subList = subList;
            this.date = date;
        }

        public InsideCollectTransMonth() {

        }

        @Override
        public List<String> call() throws Exception {

            for (Map<String, Object> agentMap : subList) {
                collectTransMonthDate(agentMap, date);
            }
            //此线程处理不需要返回值
            return new ArrayList<>();
        }
    }

    /**
     * 根据日期汇总代理商月交易数据
     *
     * @param agentMap
     * @param monthDate
     */
    private static void collectTransMonthDate(Map<String, Object> agentMap, Date monthDate) {
        Date beginOfMonth = DateUtil.beginOfMonth(monthDate);
        Date endOfMonth = DateUtil.endOfMonth(monthDate);
        String monthTime = DateUtil.format(beginOfMonth, "yyyy-MM-dd");
        String agentNo = StringUtils.filterNull(agentMap.get("agent_no"));
        String agentNode = StringUtils.filterNull(agentMap.get("agent_node"));
        String parentId = StringUtils.filterNull(agentMap.get("parent_id"));
        String agentLevel = StringUtils.filterNull(agentMap.get("agent_level"));
        if (StringUtils.isBlank(agentNo)) {
            return;
        }
        try {
            //根据已汇总的日数据汇总
            Map<String, Object> collectMap = transBatchService.hasCollectDay(agentNo, DateUtil.format(beginOfMonth, "yyyy-MM-dd"), DateUtil.format(endOfMonth, "yyyy-MM-dd"));
            log.info("代理商编号" + agentNo + "月交易分润汇总月期" + monthTime + "已汇总的日数据：" + collectMap.toString());
            int collectDay = Integer.parseInt(collectMap.get("collectDay").toString());
            int collectCount = Integer.parseInt(collectMap.get("collectCount").toString());
            BigDecimal collectSum = new BigDecimal(collectMap.get("collectSum").toString());
            BigDecimal collectTrans = new BigDecimal(collectMap.get("collectTrans").toString());

            if (collectCount > 0) {
                //汇总入库
                TransMonthBatch dbBatch = TransMonthBatch.builder().agentNo(agentNo).parentId(parentId).agentNode(agentNode)
                        .totalCount(collectCount).totalMoney(collectSum).totalTransAmount(collectTrans).collecTime(beginOfMonth)
                        .build();

                //扣减下级收入
                BigDecimal subSumToatlAmout = transBatchService.sumTransMonthTotalMoneyByParentId(agentNo, monthTime);
                dbBatch.setAccMoney(collectSum.subtract(subSumToatlAmout));
                transBatchService.insertTransMonthBatch(dbBatch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("代理商编号" + agentNo + "月交易收益汇总日期" + monthTime + "汇总异常");
        }
    }

}