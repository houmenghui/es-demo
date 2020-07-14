package com.esdemo.modules.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.config.CallableHelper;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.SettleDayBatch;
import com.esdemo.modules.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 日出款收益汇总定时任务
 *
 * @Title：vipScore
 * @Description：
 * @Author：zhangly
 * @Date：2019/7/16 11:48
 * @Version：1.0
 */
@Slf4j
public class CollectSettleDayTask {

    private static CollectService collectService = SpringHolder.getBean(CollectService.class);
    private static SettleBatchService settleBatchService = SpringHolder.getBean(SettleBatchService.class);
    private static AgentInfoService agentService = SpringHolder.getBean(AgentInfoService.class);
    private static TaskRecordService taskRecordService = SpringHolder.getBean(TaskRecordService.class);

    //必须保证同一时刻只能允许一个线程执行
    private static final Object lock = new Object();

    /**
     * 支持汇总具体哪一天和汇总多少天数据（不能都不为空）
     */
    private String collectDayTime;
    private Integer collectDayNum;

    public CollectSettleDayTask(Object inCollectDayTime, Object inCollectDayNum) {
        if (inCollectDayTime != null && inCollectDayNum != null) {
            return;
        }
        this.collectDayTime = inCollectDayTime == null ? null : StringUtils.filterNull(inCollectDayTime);
        String strInCollectDayNum = StringUtils.filterNull(inCollectDayNum);
        this.collectDayNum = StringUtils.isBlank(strInCollectDayNum) ? null : Integer.parseInt(strInCollectDayNum);
    }

    /**
     * 日出款收益汇总
     */
    public void implCollectSettleDay() {
        //获取代理商最大级别，从1开始
        int maxLevel = agentService.getMaxAgentLevel();
        if (maxLevel < 1) {
            return;
        }
        long beginTimeMillis = System.currentTimeMillis();

        Date now = new Date();
        Date dayDate = null;
        String dayTime = null;
        SettleDayBatch dbBatch = null;
        try {
            synchronized (lock) {
                //如果是指定跑多少天
                if (null != collectDayNum && collectDayNum > 0) {
                    int copyCollectDayNum = collectDayNum.intValue();
                    for (; copyCollectDayNum > 0; copyCollectDayNum--) {
                        dayDate = DateUtils.addDays(new Date(), copyCollectDayNum * -1);
                        dayTime = DateUtil.format(dayDate, "yyyy-MM-dd");
                        dbBatch = settleBatchService.getSettleDayBatchByCollecTime(dayTime);
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
                                    InsideCollectSettleDay insideClass = new InsideCollectSettleDay();
                                    CallableHelper.executeCallable(insideClass, agentList, new Class[]{List.class, Date.class}, new Object[]{dayDate}, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                //如果指定跑哪一天
                else if (StringUtils.isNotBlank(collectDayTime)) {
                    dayDate = DateUtil.parse(collectDayTime, "yyyy-MM-dd");
                    dayTime = DateUtil.format(dayDate, "yyyy-MM-dd");
                    dbBatch = settleBatchService.getSettleDayBatchByCollecTime(dayTime);
                    if (null == dbBatch) {
                        for (int level = maxLevel; level >= 1; level--) {
                            if (level > 20) {
                                continue;
                            }
                            List<Map<String, Object>> agentList = agentService.getAgentInfoByLevel(level);
                            if (!CollectionUtil.isEmpty(agentList)) {
                                try {
                                    InsideCollectSettleDay insideClass = new InsideCollectSettleDay();
                                    CallableHelper.executeCallable(insideClass, agentList, new Class[]{List.class, Date.class}, new Object[]{dayDate}, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                //默认只跑前一天
                else {
                    dayDate = DateUtils.addDays(new Date(), -1);
                    dayTime = DateUtil.format(dayDate, "yyyy-MM-dd");
                    dbBatch = settleBatchService.getSettleDayBatchByCollecTime(dayTime);
                    if (null == dbBatch) {
                        for (int level = maxLevel; level >= 1; level--) {
                            if (level > 20) {
                                continue;
                            }
                            List<Map<String, Object>> agentList = agentService.getAgentInfoByLevel(level);
                            if (!CollectionUtil.isEmpty(agentList)) {
                                try {
                                    InsideCollectSettleDay insideClass = new InsideCollectSettleDay();
                                    CallableHelper.executeCallable(insideClass, agentList, new Class[]{List.class, Date.class}, new Object[]{dayDate}, null);
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
            log.error("代理商日出款收益汇总任务异常：{}", e);
        }

        long endTimeMillis = System.currentTimeMillis();
        log.info("代理商日出款收益汇总任务完成耗时:{}ms", endTimeMillis - beginTimeMillis);

        try {
            //任务回调
            String classWholeName = this.getClass().getName();
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            taskRecordService.taskRunningCallback(classWholeName + "." + methodName);
        } catch (Exception e) {
            log.error("代理商日出款收益汇总------>回调定时任务系统异常");
            e.printStackTrace();
        }
    }

    public static class InsideCollectSettleDay implements Callable<List<String>> {

        private List<Map<String, Object>> subList;
        private Date date;

        public InsideCollectSettleDay(List<Map<String, Object>> subList, Date date) {

            this.subList = subList;
            this.date = date;
        }

        public InsideCollectSettleDay() {

        }

        @Override
        public List<String> call() throws Exception {

            for (Map<String, Object> agentMap : subList) {
                collectSettleDayDate(agentMap, date);
            }
            //此线程处理不需要返回值
            return new ArrayList<>();
        }
    }

    /**
     * 根据日期汇总代理商日出款数据
     *
     * @param agentMap
     * @param dayDate
     */
    private static void collectSettleDayDate(Map<String, Object> agentMap, Date dayDate) {
        String dayTime = DateUtil.format(dayDate, "yyyy-MM-dd");
        String agentNo = StringUtils.filterNull(agentMap.get("agent_no"));
        String agentNode = StringUtils.filterNull(agentMap.get("agent_node"));
        String parentId = StringUtils.filterNull(agentMap.get("parent_id"));
        String agentLevel = StringUtils.filterNull(agentMap.get("agent_level"));
        if (StringUtils.isBlank(agentNo)) {
            return;
        }
        try {
            String beginTime = DateUtil.format(DateUtil.beginOfDay(dayDate), "yyyy-MM-dd HH:mm:ss");
            String endTime = DateUtil.format(DateUtil.endOfDay(dayDate), "yyyy-MM-dd HH:mm:ss");
            Map<String, Object> collectMap = collectService.collectSettleByAgentAndTime(agentNode, agentLevel, beginTime, endTime);
            log.info("代理商编号" + agentNo + "日出款汇总日期" + dayTime + "汇总结果：" + collectMap.toString());
            int collectCount = Integer.parseInt(collectMap.get("collectCount").toString());
            BigDecimal collectSum = new BigDecimal(collectMap.get("collectSum").toString());
            if (collectCount > 0) {
                //汇总入库
                SettleDayBatch dbBatch = SettleDayBatch.builder().agentNo(agentNo).parentId(parentId).agentNode(agentNode)
                        .totalCount(collectCount).totalMoney(collectSum).collecTime(dayDate)
                        .build();
                //扣减下级收入
                BigDecimal subSumToatlAmout = settleBatchService.sumSettleDayTotalMoneyByParentId(agentNo, dayTime);
                dbBatch.setAccMoney(collectSum.subtract(subSumToatlAmout));
                settleBatchService.insertSettleDayBatch(dbBatch);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("代理商编号" + agentNo + "日出款汇总日期" + dayTime + "汇总异常");
        }
    }

}