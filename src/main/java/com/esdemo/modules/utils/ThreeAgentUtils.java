package com.esdemo.modules.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.EsSearchBean;
import com.esdemo.modules.service.AgentEsService;
import com.esdemo.modules.service.ThreeAgentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.esdemo.frame.enums.EsNpospJoinType.AGENT;

/**
 * @description: 三方数据处理辅助
 * @author: zhangly
 * @create: 2020/05/09
 */
@Slf4j
public class ThreeAgentUtils {

    private static ThreeAgentService threeAgentService = SpringHolder.getBean(ThreeAgentService.class);
    private static AgentEsService agentEsService = SpringHolder.getBean(AgentEsService.class);

    /**
     * 获取所有下发三方代理商编号
     *
     * @param agentNo
     * @return
     */
    public static void getAllThreeChildAgentNos(String agentNo, List<AgentInfo> threeChildAgentNos) {
        if (null == threeChildAgentNos) {
            return;
        }
        List<AgentInfo> directChildrenAgentNos = threeAgentService.getDirectChildThreeAgent(agentNo);
        if (!CollectionUtils.isEmpty(directChildrenAgentNos)) {
            for (AgentInfo directChildAgentNo : directChildrenAgentNos) {
                threeChildAgentNos.add(directChildAgentNo);
                getAllThreeChildAgentNos(directChildAgentNo.getAgentNo(), threeChildAgentNos);
            }
        }
    }

    /**
     * 获取系统配置的三方组织编号
     *
     * @return
     */
    public static List<String> getThreeTeamIds() {
        List<String> thressTeamIdList = new ArrayList<>();
        String THREE_INCOME_CALC_OEM = WebUtils.getDictValue("THREE_INCOME_CALC_OEM");
        if (StringUtils.isNotBlank(THREE_INCOME_CALC_OEM)) {
            String[] thressTeamIdArray = THREE_INCOME_CALC_OEM.split("-");
            if (!ArrayUtils.isEmpty(thressTeamIdArray)) {
                thressTeamIdList = Arrays.asList(thressTeamIdArray);
            }
        }
        log.info("当前系统配置的三方组织编号为：{}", JSONUtil.toJsonStr(thressTeamIdList));
        return thressTeamIdList;
    }

    /**
     * 统计三方新增代理商数据
     *
     * @param queryAgentInfo 查询代理商，默认为当前登录，调用方赋值
     * @param teamId         查询三方组织，如果为空，取系统配置的所有三方组织（暂时不用组织过滤）
     * @param timeUnit       查询范围，day：昨日（后面产品又改为取全部了），month：本月但不包含当日（如果为某月一号，返回0）
     * @return
     */
    public static long countThreeAddAgent(AgentInfo queryAgentInfo, String teamId, TimeUnitEnum timeUnit) {
        Date now = new Date();
        Date yesterDay = DateUtils.addDays(now, -1);
        if (TimeUnitEnum.MONTH == timeUnit && DateUtil.month(now) != DateUtil.month(yesterDay)) {
            log.info("当月第一天，直接返回0");
            return 0L;
        }
        String beginTimeStr = "", endTimeStr = "";
        if (TimeUnitEnum.MONTH == timeUnit) {
            beginTimeStr = DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss");
            endTimeStr = DateUtil.format(DateUtil.endOfDay(yesterDay), "yyyy-MM-dd HH:mm:ss");
        }
        // 按组织查询，为空表示查询全部
        /*List<String> threeTeamIds = new ArrayList<>();
        if (StringUtils.isBlank(teamId)) {
            threeTeamIds = ThreeAgentUtils.getThreeTeamIds();
        } else {
            threeTeamIds.add(teamId);
        }
        Map<String, List<String>> inFields = new HashMap<>();
        inFields.put(TEAM_ID.getFieldName(), threeTeamIds);*/
        //代理商链条
        List<AgentInfo> inAgentInfo = new ArrayList<>();
        ThreeAgentUtils.getAllThreeChildAgentNos(queryAgentInfo.getAgentNo(), inAgentInfo);
        //要包含自身
        inAgentInfo.add(queryAgentInfo);
        //新增代理商数量
        EsSearchBean searchBean = EsSearchBean.builder().typeName(AGENT.getTypeName()).inAgentInfo(inAgentInfo).queryScope(QueryScope.ALL)
                /*.inFields(inFields)*/.build();
        if (StringUtils.isNotBlank(beginTimeStr)) {
            searchBean.setStartCreateTime(beginTimeStr);
        }
        if (StringUtils.isNotBlank(endTimeStr)) {
            searchBean.setEndCreateTime(endTimeStr);
        }
        return agentEsService.summaryAgentCountByTerms(searchBean);
    }
}
