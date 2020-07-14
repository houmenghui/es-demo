package com.esdemo.modules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.dao.ThreeDataDao;
import com.esdemo.modules.service.OrderEsService;
import com.esdemo.modules.service.SysDictService;
import com.esdemo.modules.utils.ThreeAgentUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.esdemo.frame.enums.EsNpospJoinType.AGENT;

/**
 * 三方数据服务
 *
 * @author Qiu Jian
 */
@Service
public class ThreeDataServiceImpl {

    public static final String THREE_INCOME_CALC_OEM = "THREE_INCOME_CALC_OEM";

    @Autowired
    private ThreeDataDao threeDataDao;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private OrderEsService orderEsService;

    public Page<AgentSelectVo> getChildrenAgentByAgentNoAndKeyword(String agentNo, String keyword, int pageNo,
                                                                   int pageSize) {
        if (StringUtils.isBlank(agentNo)) {
            return null;
        }
        Page<AgentSelectVo> page = PageHelper.startPage(pageNo, pageSize, true);
        threeDataDao.selectChildrenAgentByAgentNoAndKeyword(agentNo, keyword);
        return page;
    }

    public Integer entrySwitch(String agentNo) {
        // 入口默认只有在三方关系链中存在且是非末级的代理商才显示
        int countAgentLinkByAgentNo = threeDataDao.countAgentLinkByAgentNo(agentNo);
        return countAgentLinkByAgentNo > 0 ? 1 : 0;
    }

    public ThreeDataCollect collectQuery(String currentAgentNo, String agentNo, String teamId) {
        List<String> agentNoList = new ArrayList<>();
        if (StringUtil.isEmpty(agentNo)) {// 查全部
            agentNoList.add(currentAgentNo);
            getAllLookAgentNo(agentNoList, currentAgentNo);
        } else {
            String selectAgentNo = threeDataDao.selectAgentLinkByCurrentAgentNoAndAgentNo(currentAgentNo, agentNo);
            agentNoList.add(selectAgentNo);
            getAllLookAgentNo(agentNoList, selectAgentNo);
        }

        ThreeDataCollect countThreeDataCollect = threeDataDao.countThreeDataCollect(agentNoList, teamId);
        if (countThreeDataCollect == null) {
            countThreeDataCollect = new ThreeDataCollect();
            countThreeDataCollect.setMerchantSum("0");
            countThreeDataCollect.setTransSum("0.00");
            countThreeDataCollect.setActivatedMerchantSum("0");
        }
        // 获取最新更新时间
        Date date = threeDataDao.selectLastUpdateTime();
        countThreeDataCollect.setLastUpdateTime(DateUtil.formatDateTime(date));

        // 获取最新库存
        String terminalSum = threeDataDao.countTerminalSumByAgentNoListAndTeamIdAndCreateTime(agentNoList, teamId,
                date);
        countThreeDataCollect.setTerminalSum(terminalSum == null ? "0" : terminalSum);
        return countThreeDataCollect;
    }

    public List<TeamSelect> getTeamSelectList() {
        String teamConfigStr = sysDictService.getSysDictValueByKey(THREE_INCOME_CALC_OEM);
        List<TeamSelect> teamSelectList = new ArrayList<>();
        if (StringUtils.isBlank(teamConfigStr)) {
            return teamSelectList;
        }
        String[] teamArray = teamConfigStr.split("-");
        for (int i = 0; i < teamArray.length; i++) {
            String teamId = teamArray[i];
            TeamSelect teamSelect = threeDataDao.selectTeamSelectByTeamId(teamId);
            teamSelectList.add(teamSelect);
        }
        return teamSelectList;
    }

    private void getAllLookAgentNo(List<String> agentNoList, String agentNo) {
        List<String> selectLookAgentNo = threeDataDao.selectLookAgentNo(agentNo);
        for (String str : selectLookAgentNo) {
            if (agentNoList.contains(str)) {
                continue;
            }
            agentNoList.add(str);
            getAllLookAgentNo(agentNoList, str);
        }
    }

    public ThreeDataCollect getDetailByQo(ThreeDataTetailQo threeDataTetailQo) {
        String agentNo = threeDataTetailQo.getAgentNo();
        String currentAgentNo = threeDataTetailQo.getCurrentAgentNo();

        List<String> agentNoList = new ArrayList<>();
        if (StringUtil.isEmpty(agentNo)) {// 查全部
            agentNoList.add(currentAgentNo);
            getAllLookAgentNo(agentNoList, currentAgentNo);
        } else {
            String selectAgentNo = threeDataDao.selectAgentLinkByCurrentAgentNoAndAgentNo(currentAgentNo, agentNo);
            agentNoList.add(selectAgentNo);
            getAllLookAgentNo(agentNoList, selectAgentNo);
        }
        threeDataTetailQo.setAgentNoList(agentNoList);
        ThreeDataCollect threeDataCollect = threeDataDao.selectThreeDataCollectByQo(threeDataTetailQo);

        if (threeDataCollect == null) {
            threeDataCollect = new ThreeDataCollect();
            threeDataCollect.setMerchantSum("0");
            threeDataCollect.setTransSum("0.00");
            threeDataCollect.setActivatedMerchantSum("0");
        }

        List<ThreeDataCollect> details = threeDataDao.selectDetailByQo(threeDataTetailQo);
        threeDataCollect.setDetails(details);

        // 获取最新更新时间 最新库存
        ThreeDataCollect threeDataCollectSection = threeDataDao.selectThreeDataCollectSectionByQo(threeDataTetailQo);
        if (threeDataCollectSection == null) {
            threeDataCollect.setTerminalSum("0");
            String yearMonth = threeDataTetailQo.getYearMonth();
            yearMonth = yearMonth + "日";
            String lastUpdateTime = yearMonth.substring(0, 4).concat("年")
                    .concat(yearMonth.substring(4, yearMonth.length()));
            threeDataCollect.setLastUpdateTime(lastUpdateTime);
        } else {
            threeDataCollect.setLastUpdateTime(threeDataCollectSection.getLastUpdateTime());
            threeDataCollect.setTerminalSum(threeDataCollectSection.getTerminalSum());
        }
        return threeDataCollect;
    }

    public ThreeDataHome threeDataHome(AgentInfo queryAgentInfo, String teamId) {
        String agentNo = queryAgentInfo.getAgentNo();
        ThreeDataHome threeDataHome = new ThreeDataHome();
        List<String> agentNoList = new ArrayList<>();
        agentNoList.add(agentNo);
        getAllLookAgentNo(agentNoList, agentNo);

        // 获取昨天交易量和全部商户数
        ThreeDataHome getYesterdayTradeSumAndTotalMerchantNumByAgentNoAndCreateTime = threeDataDao
                .getYesterdayTradeSumAndTotalMerchantNumByAgentNoList(agentNoList, teamId);

        if (getYesterdayTradeSumAndTotalMerchantNumByAgentNoAndCreateTime == null) {

            threeDataHome.setYesterdayTradeSum("0");
        } else {
            threeDataHome.setYesterdayTradeSum(
                    getYesterdayTradeSumAndTotalMerchantNumByAgentNoAndCreateTime.getYesterdayTradeSum());
        }
        // 昨日新增商户
        Long yesterdayTotalMerchantNum = threeDataDao.getYesterdayTotalMerchantNumByAgentNoList(agentNoList, teamId);
        if (yesterdayTotalMerchantNum == null) {
            yesterdayTotalMerchantNum = 0L;
        }
        
        Long totalMerchantNum = threeDataDao.getTotalMerchantNumByAgentNoList(agentNoList, teamId);
        if (totalMerchantNum == null) {
        	totalMerchantNum = 0L;
        }
        threeDataHome.setTotalMerchantNum(totalMerchantNum);
        threeDataHome.setYesterdayAddMerchantNum(yesterdayTotalMerchantNum);
        // 全部代理
        // 走数据库跑批表查询，由于三方明细中的数据查的是ES，会有误差，所以汇总数据改为ES查询
        // Long totalAgentNum = threeDataDao.countTotalAgent(agentNo, teamId);
        Long totalAgentNum = ThreeAgentUtils.countThreeAddAgent(queryAgentInfo, teamId, TimeUnitEnum.DAY);
        threeDataHome.setTotalAgentNum(totalAgentNum);

        // 获取本月交易量和交易笔数
        ThreeDataHome getCurrentMonthTradeSumAndCurrentMonthTradeCountByAgentNoList = threeDataDao
                .getCurrentMonthTradeSumAndCurrentMonthTradeCountByAgentNoList(agentNoList, teamId);
        if (getCurrentMonthTradeSumAndCurrentMonthTradeCountByAgentNoList == null) {
            threeDataHome.setCurrentMonthTradeSum("0");
            threeDataHome.setCurrentMonthTradeCount(0L);
        } else {

            threeDataHome.setCurrentMonthTradeSum(
                    getCurrentMonthTradeSumAndCurrentMonthTradeCountByAgentNoList.getCurrentMonthTradeSum());
            threeDataHome.setCurrentMonthTradeCount(
                    getCurrentMonthTradeSumAndCurrentMonthTradeCountByAgentNoList.getCurrentMonthTradeCount());
        }
        // 本月新增代理
        // 走数据库跑批表查询，由于三方明细中的数据查的是ES，会有误差，所以汇总数据改为ES查询
        /*
         * Long currentMonthAddAgentNum =
         * threeDataDao.getCurrentMonthAddAgentNumByAgentNo(agentNo, teamId); if
         * (currentMonthAddAgentNum == null) {
         * threeDataHome.setCurrentMonthAddAgentNum(0L); }else {
         * threeDataHome.setCurrentMonthAddAgentNum(currentMonthAddAgentNum < 0 ? 0 :
         * currentMonthAddAgentNum); }
         */
        threeDataHome.setCurrentMonthAddAgentNum(
                ThreeAgentUtils.countThreeAddAgent(queryAgentInfo, teamId, TimeUnitEnum.MONTH));

        // 本月新增商户数
        Long currentMonthAddMerchantNum = threeDataDao.getCurrentMonthAddMerchantNumByAgentNoList(agentNoList, teamId);
        if (currentMonthAddMerchantNum == null) {
            threeDataHome.setCurrentMonthAddMerchantNum(0L);
        } else {
            threeDataHome
                    .setCurrentMonthAddMerchantNum(currentMonthAddMerchantNum < 0 ? 0 : currentMonthAddMerchantNum);
        }

        return threeDataHome;
    }

    @CacheData(type = CacheData.CacheType.TTL)
    public ThreeDataTrend threeDataTrend(AgentInfo queryAgentInfo, String teamId) {

        //递归获取所有三方代理商信息，下面ES统计时需要使用
        List<AgentInfo> inAgentInfo = new ArrayList<>();
        ThreeAgentUtils.getAllThreeChildAgentNos(queryAgentInfo.getAgentNo(), inAgentInfo);
        inAgentInfo.add(queryAgentInfo);
        //单独获取代理商编号，邱健使用，不改threeDataDao.tradeTrend dao代码
        List<String> agentNoList = new ArrayList<>();
        for(AgentInfo agentInfo : inAgentInfo){
            agentNoList.add(agentInfo.getAgentNo());
        }

        ThreeDataTrend threeDataTrend = new ThreeDataTrend();
        // 交易
        TransOrderTrend transOrderTrend = new TransOrderTrend();
        List<TradeTrend> tradeTrend = threeDataDao.tradeTrend(agentNoList, teamId, "1");
        List<KeyValueBean> sevenDayTrend = new ArrayList<>();
        for (TradeTrend trend : tradeTrend) {
            KeyValueBean keyValueBean = new KeyValueBean();
            keyValueBean.setKey(trend.getDateStr());
            keyValueBean.setValue(trend.getTradeSum());
            sevenDayTrend.add(keyValueBean);
        }
        transOrderTrend.setSevenDayTrend(sevenDayTrend);
        List<TradeTrend> tradeTrend2 = threeDataDao.tradeTrend(agentNoList, teamId, "2");
        List<KeyValueBean> halfYearTrend = new ArrayList<>();
        for (TradeTrend trend : tradeTrend2) {
            KeyValueBean keyValueBean = new KeyValueBean();
            keyValueBean.setKey(trend.getDateStr());
            keyValueBean.setValue(trend.getTradeSum());
            halfYearTrend.add(keyValueBean);
        }
        transOrderTrend.setHalfYearTrend(halfYearTrend);
        threeDataTrend.setTransOrderTrend(transOrderTrend);

        // 商户
        TransOrderTrend newlyMerTrend = new TransOrderTrend();
        List<KeyValueBean> sevenDayTrend2 = new ArrayList<>();
        List<MerTrend> merTrend = threeDataDao.merTrend(agentNoList, teamId, "1");
        for (int i = 0; i < merTrend.size(); i++) {
            MerTrend trend = merTrend.get(i);
            KeyValueBean keyValueBean = new KeyValueBean();
            keyValueBean.setKey(trend.getDateStr());
            keyValueBean.setValue(trend.getMerSum());
            sevenDayTrend2.add(keyValueBean);
        }
        newlyMerTrend.setSevenDayTrend(sevenDayTrend2);
        List<KeyValueBean> halfYearTrend2 = new ArrayList<>();
        List<MerTrend> merTrend2 = threeDataDao.merTrend(agentNoList, teamId, "2");
        for (int i = 0; i < merTrend2.size()-1; i++) {
            MerTrend trend = merTrend2.get(i);
            KeyValueBean keyValueBean = new KeyValueBean();
            keyValueBean.setKey(trend.getDateStr());
            keyValueBean.setValue(trend.getMerSum());
            halfYearTrend2.add(keyValueBean);
        }
        newlyMerTrend.setHalfYearTrend(halfYearTrend2);
        threeDataTrend.setNewlyMerTrend(newlyMerTrend);

        // 新增代理
        //之前走的跑批数据库查询，不满足业务场景，现改为ES查询
        TransOrderTrend newlyAgentTrend = new TransOrderTrend();
        //数据库查询
        /*List<KeyValueBean> sevenDayTrend3 = new ArrayList<>();
        List<AgentTrend> agentTrend = threeDataDao.agentTrend(agentNo, teamId, "1");
        for (int i = 1; i < agentTrend.size(); i++) {
            AgentTrend trend = agentTrend.get(i);
            AgentTrend trend2 = agentTrend.get(i - 1);
            KeyValueBean keyValueBean = new KeyValueBean();
            keyValueBean.setKey(trend.getDateStr());
            keyValueBean.setValue(trend.getTotalAgentNum() - trend2.getTotalAgentNum());
            sevenDayTrend3.add(keyValueBean);
        }
        newlyAgentTrend.setSevenDayTrend(sevenDayTrend3);
        List<KeyValueBean> halfYearTrend3 = new ArrayList<>();
        List<AgentTrend> agentTrend2 = threeDataDao.agentTrend(agentNo, teamId, "2");
        for (int i = 1; i < agentTrend2.size(); i++) {
            AgentTrend trend = agentTrend2.get(i);
            AgentTrend trend2 = agentTrend2.get(i - 1);
            KeyValueBean keyValueBean = new KeyValueBean();
            keyValueBean.setKey(trend.getDateStr());
            keyValueBean.setValue(trend.getTotalAgentNum() - trend2.getTotalAgentNum());
            halfYearTrend3.add(keyValueBean);
        }
        newlyAgentTrend.setHalfYearTrend(halfYearTrend3);*/
		//ES查询
        EsSearchBean searchBean = EsSearchBean.builder().typeName(AGENT.getTypeName()).inAgentInfo(inAgentInfo).queryScope(QueryScope.ALL).build();
        Tuple<List<KeyValueBean>, List<KeyValueBean>> newlyAgentTrendEs = orderEsService.listSevenDayAndHalfYearDataTrend(searchBean);
		newlyAgentTrend.setSevenDayTrend(newlyAgentTrendEs.v1());
		newlyAgentTrend.setHalfYearTrend(newlyAgentTrendEs.v2());

		threeDataTrend.setNewlyAgentTrend(newlyAgentTrend);
        return threeDataTrend;
    }

}
