package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.*;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.enums.TimeUnitEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.frame.utils.swagger.ThreeDataSwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.AccessService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.OrderEsService;
import com.esdemo.modules.service.impl.ThreeDataServiceImpl;
import com.esdemo.modules.utils.ThreeAgentUtils;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 三方数据控制器
 *
 * @author Qiu Jian
 */
@Api(description = "三方数据模块")
@RestController
@Slf4j
/*@SignValidate(needSign = false)
@LoginValid(needLogin = false)*/
public class ThreeDataController {

    @Autowired
    private ThreeDataServiceImpl threeDataService;
    @Resource
    private AccessService accessService;
    @Resource
    private OrderEsService orderEsService;
    @Resource
    private AgentInfoService agentInfoService;

    @PostMapping("/threeData/getChildrenAgent/{pageNo}/{pageSize}")
    @KqSwaggerDeveloped
    @ApiOperation(value = "获取直属下级代理商名称", notes = SwaggerNotes.THREE_DATA_GET_CHILDREN_AGENT)
    public ResponseBean getChildrenAgent(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                         @PathVariable(required = false) int pageNo, @PathVariable(required = false) int pageSize,
                                         @RequestBody(required = false) Map<String, String> bodyParams) {
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;

        String keyword = bodyParams.get("keyword");
        keyword = StringUtils.isBlank(keyword) ? "" : keyword.trim();
        String currAgentNo = userInfoBean.getAgentNo();
        try {
            Page<AgentSelectVo> page = threeDataService.getChildrenAgentByAgentNoAndKeyword(currAgentNo, keyword,
                    pageNo, pageSize);
            List<AgentSelectVo> pageData = new ArrayList<>();
            long totalCount = 0l;
            if (null != page) {
                pageData = page.getResult();
                totalCount = page.getTotal();
            }
            return new ResponseBean<>(200, "获取成功", pageData, totalCount, true);
        } catch (Exception e) {
            log.error("查询代理商{}三方下级代理商异常{}", currAgentNo, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @PostMapping("/threeData/teamSelectList")
    @KqSwaggerDeveloped
    @ApiOperation(value = "获取组织下拉列表", notes = SwaggerNotes.THREE_DATA_TEAM_SELECT_LIST)
    public ResponseBean<List<TeamSelect>> teamSelectList(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            List<TeamSelect> list = threeDataService.getTeamSelectList();
            return new ResponseBean<>(200, "获取成功", list, 0, true);
        } catch (Exception e) {
            log.error("查询代理商{}获取组织下拉列表异常{}", userInfoBean.getAgentNo(), e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @GetMapping("/threeData/collectQuery")
    @OldSwaggerDeveloped
    @ApiOperation(value = "获取三方数据汇总数据", notes = SwaggerNotes.THREE_DATA_COLLECT_QUERY)
    public ResponseBean<ThreeDataCollect> collectQuery(String agentNo, String teamId,
                                                       @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            ThreeDataCollect threeDataCollect = threeDataService.collectQuery(userInfoBean.getAgentNo(), agentNo,
                    teamId);
            return new ResponseBean<>(200, "获取成功", threeDataCollect, 0, true);
        } catch (Exception e) {
            log.error("系统异常", e);
            return new ResponseBean<>(400, "获取失败", null, 0, false);
        }
    }

    @GetMapping("/threeData/detail")
    @OldSwaggerDeveloped
    @ApiOperation(value = "获取三方数据汇总数据明细", notes = SwaggerNotes.THREE_DATA_DETAIL)
    public ResponseBean<ThreeDataCollect> detail(ThreeDataTetailQo threeDataTetailQo, HttpServletRequest reuqest) {
        String loginAgentNo = WebUtils.getLoginAgentNo(reuqest);
        threeDataTetailQo.setCurrentAgentNo(loginAgentNo);
        try {
            ThreeDataCollect threeDataCollect = threeDataService.getDetailByQo(threeDataTetailQo);
            return new ResponseBean<>(200, "获取成功", threeDataCollect, 0, true);
        } catch (Exception e) {
            log.error("系统异常", e);
            return new ResponseBean<>(400, "获取失败", null, 0, false);
        }
    }

    @ApiOperation(value = "三方数据明细", notes = ThreeDataSwaggerNotes.THREE_DATA_DETAIL)
    @PostMapping("/threeDataDetail/{pageNo}/{pageSize}")
    @KqSwaggerDeveloped
    public ResponseBean threeDataDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                        @PathVariable(required = false) int pageNo, @PathVariable(required = false) int pageSize,
                                        @RequestBody(required = false) Map<String, String> bodyParams) {

        Map<String, Object> res = new HashMap<>();
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;

        String queryAgentNo = "", queryAgentNode = "";
        String currAgentNode = userInfoBean.getAgentNode();
        String currAgentNo = userInfoBean.getAgentNo();

        String agentNo = bodyParams.get("agentNo");
        String teamId = bodyParams.get("teamId");
        String timeUnitCode = bodyParams.get("timeUnit");
        try {
            if (StringUtils.isBlank(timeUnitCode) || !Arrays
                    .asList(TimeUnitEnum.DAY.getUnit(), TimeUnitEnum.MONTH.getUnit()).contains(timeUnitCode)) {
                return ResponseBean.error("查询参数不合法");
            }
            TimeUnitEnum timeUnit = TimeUnitEnum.getByTimeUnitCode(timeUnitCode);
            // 按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheThreeAgent(currAgentNo, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                AgentInfo queryAgentInfo = agentInfoService.queryAgentInfo(agentNo);
                if (null == queryAgentInfo) {
                    return ResponseBean.error("查询代理商信息不存在");
                }
                queryAgentNo = agentNo;
                queryAgentNode = queryAgentInfo.getAgentNode();
            } else {
                queryAgentNo = currAgentNo;
                queryAgentNode = currAgentNode;
            }

            // 按组织查询，为空表示查询全部
            List<String> threeTeamIds = new ArrayList<>();
            if (StringUtils.isBlank(teamId)) {
                threeTeamIds = ThreeAgentUtils.getThreeTeamIds();
            } else {
                threeTeamIds.add(teamId);
            }

            AgentInfo queryAgentInfo = new AgentInfo();
            queryAgentInfo.setAgentNode(queryAgentNode);
            queryAgentInfo.setAgentNo(queryAgentNo);

            return ResponseBean.success(orderEsService.summaryAchievementByTerms(queryAgentInfo, true, threeTeamIds,
                    QueryScope.ALL, timeUnit, pageNo, pageSize));

        } catch (Exception e) {
            log.error("查询代理商{}按{}维度查询三方数据明细异常{}", queryAgentNo, timeUnitCode, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @ApiOperation(value = "三方数据代理商汇总", notes = ThreeDataSwaggerNotes.THREE_DATA_AGENT_CENSUS)
    @PostMapping("/threeData/agentCensus")
    @KqSwaggerDeveloped
    public ResponseBean threeDataAgentCensus(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                             @RequestBody(required = false) Map<String, String> bodyParams) {

        Map<String, Object> res = new HashMap<>();
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());

        String queryAgentNo = "", queryAgentNode = "";
        String currAgentNode = userInfoBean.getAgentNode();
        String currAgentNo = userInfoBean.getAgentNo();

        String agentNo = bodyParams.get("agentNo");
        String teamId = bodyParams.get("teamId");
        String timeUnitCode = bodyParams.get("timeUnit");
        String timeStr = bodyParams.get("timeStr");
        String orderByType = bodyParams.get("orderByType");
        try {
            if (StringUtils.isBlank(timeUnitCode, timeStr) || !Arrays
                    .asList(TimeUnitEnum.DAY.getUnit(), TimeUnitEnum.MONTH.getUnit()).contains(timeUnitCode)) {
                return ResponseBean.error("查询参数不合法");
            }
            TimeUnitEnum timeUnit = TimeUnitEnum.getByTimeUnitCode(timeUnitCode);
            // 按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheThreeAgent(currAgentNo, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                AgentInfo queryAgentInfo = agentInfoService.queryAgentInfo(agentNo);
                if (null == queryAgentInfo) {
                    return ResponseBean.error("查询代理商信息不存在");
                }
                queryAgentNo = agentNo;
                queryAgentNode = queryAgentInfo.getAgentNode();
            } else {
                queryAgentNo = currAgentNo;
                queryAgentNode = currAgentNode;
            }

            // 按组织查询，为空表示查询全部
            List<String> threeTeamIds = new ArrayList<>();
            if (StringUtils.isBlank(teamId)) {
                threeTeamIds = ThreeAgentUtils.getThreeTeamIds();
            } else {
                threeTeamIds.add(teamId);
            }

            AgentInfo queryAgentInfo = new AgentInfo();
            queryAgentInfo.setAgentNode(queryAgentNode);
            queryAgentInfo.setAgentNo(queryAgentNo);

            return ResponseBean.success(
                    orderEsService.threeDataAgentCensus(queryAgentInfo, threeTeamIds, timeUnit, timeStr, orderByType));

        } catch (Exception e) {
            log.error("查询代理商{}按{}查询三方数据代理商汇总异常{}", queryAgentNo, timeStr, e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "首页三方数据汇总", notes = ThreeDataSwaggerNotes.THREE_DATA_HOME)
    @PostMapping("/threeData/threeDataHome")
    @KqSwaggerDeveloped
    public ResponseBean<ThreeDataHome> threeDataHome(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                                     @RequestBody(required = false) Map<String, String> bodyParams) {
        String agentNo = bodyParams.get("agentNo");
        String teamId = bodyParams.get("teamId");

        String queryAgentNo = "", queryAgentNode = "";
        String currAgentNode = userInfoBean.getAgentNode();
        String currAgentNo = userInfoBean.getAgentNo();
        try {
            // 按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheThreeAgent(currAgentNo, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                AgentInfo queryAgentInfo = agentInfoService.queryAgentInfo(agentNo);
                if (null == queryAgentInfo) {
                    return ResponseBean.error("查询代理商信息不存在");
                }
                queryAgentNo = agentNo;
                queryAgentNode = queryAgentInfo.getAgentNode();
            } else {
                queryAgentNo = currAgentNo;
                queryAgentNode = currAgentNode;
            }
            AgentInfo queryAgentInfo = new AgentInfo();
            queryAgentInfo.setAgentNode(queryAgentNode);
            queryAgentInfo.setAgentNo(queryAgentNo);

            ThreeDataHome threeDataHome = threeDataService.threeDataHome(queryAgentInfo, teamId);
            return ResponseBean.success(threeDataHome);
        } catch (Exception e) {
            log.error("查询三方数据汇总异常", e);
            return ResponseBean.error("获取数据失败，请稍候再试");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "首页三方数据趋势", notes = ThreeDataSwaggerNotes.THREE_DATA_TREND)
    @PostMapping("/threeData/trend")
    public ResponseBean<ThreeDataTrend> trend(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                              @RequestBody(required = false) Map<String, String> bodyParams) {
        String agentNo = bodyParams.get("agentNo");
        String teamId = bodyParams.get("teamId");
        String queryAgentNo = "", queryAgentNode = "";
        String currAgentNo = userInfoBean.getAgentNo();
        String currAgentNode = userInfoBean.getAgentNode();
        try {
            // 按代理商查询，如果没选择代理商，默认查当前登录的代理商
            if (StringUtils.isNotBlank(agentNo)) {
                if (!accessService.canAccessTheThreeAgent(currAgentNo, agentNo)) {
                    return ResponseBean.error("无权操作");
                }
                AgentInfo queryAgentInfo = agentInfoService.queryAgentInfo(agentNo);
                if (null == queryAgentInfo) {
                    return ResponseBean.error("查询代理商信息不存在");
                }
                queryAgentNo = agentNo;
                queryAgentNode = queryAgentInfo.getAgentNode();
            } else {
                queryAgentNo = currAgentNo;
                queryAgentNode = currAgentNode;
            }

            AgentInfo queryAgentInfo = new AgentInfo();
            queryAgentInfo.setAgentNode(queryAgentNode);
            queryAgentInfo.setAgentNo(queryAgentNo);

            ThreeDataTrend ThreeDataTrend = threeDataService.threeDataTrend(queryAgentInfo, teamId);
            return new ResponseBean<>(200, "获取成功", ThreeDataTrend, 0, true);
        } catch (Exception e) {
            log.error("查询三方数据交易趋势异常", e);
            return new ResponseBean<>(0, "系统异常", null, 0, false);
        }
    }

}
