package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.AccessService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.MerchantEsService;
import com.esdemo.modules.service.MerchantInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import com.esdemo.modules.bean.Tuple;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-14 13:57
 */
@RestController
@Api(description = "商户模块")
@RequestMapping("/merchant")
public class MerchantController {

    @Resource
    private MerchantEsService merchantEsService;
    @Resource
    private MerchantInfoService merchantInfoService;
    @Resource
    private AccessService accessService;
    @Resource
    private AgentInfoService agentInfoService;

    @KqSwaggerDeveloped
    @ApiOperation(value = "1 首页->全部商户->商户汇总->产品汇总", notes = SwaggerNotes.MERCHANT_SUMMARY)
    @PostMapping("/merchantSummary")
    public ResponseBean merchantSummary(
            @RequestBody MerchantSummarySearchBean merchantSummarySearchBean,
            @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        merchantSummarySearchBean.setAgentNo(userInfoBean.getAgentNo());
        merchantSummarySearchBean.setAgentNode(userInfoBean.getAgentNode());
        List<MerchantSumBean> teamList = merchantEsService.merchantSummaryByTeamId(merchantSummarySearchBean, false);
        long merchantTotal = merchantEsService.countMerchant(merchantSummarySearchBean, false);
        Map<String, Object> result = new HashMap<>();
        result.put("teamList", teamList);
        result.put("merchantTotal", merchantTotal);
        return ResponseBean.success(result);
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "2 首页->全部商户->商户汇总->代理商汇总", notes = SwaggerNotes.COUNT_BY_DIRECT_AGENT)
    @PostMapping("/countByDirectAgent/{pageNo}/{pageSize}")
    public ResponseBean countByDirectAgent(@PathVariable int pageNo,
                                           @PathVariable int pageSize,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        pageNo = pageNo - 1 <= 0 ? 0 : pageNo - 1;
        pageSize = pageSize <= 1 ? 1 : pageSize;
        MerchantSummarySearchBean merchantSummarySearchBean = new MerchantSummarySearchBean();
        merchantSummarySearchBean.setAgentNo(userInfoBean.getAgentNo());
        merchantSummarySearchBean.setAgentNode(userInfoBean.getAgentNode());
        merchantSummarySearchBean.setQueryScope(QueryScope.OFFICAL);
        Tuple<List<MerchantSumBean>, Long> listLongTuple = merchantEsService.statisMerchantByDirectAgent(merchantSummarySearchBean,
                PageRequest.of(pageNo, pageSize), false);
        return ResponseBean.success(listLongTuple.v1(), listLongTuple.v2());
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "3 首页->今日新增商户->商户汇总->产品汇总", notes = SwaggerNotes.MERCHANT_SUMMARY)
    @PostMapping("/merchantSummaryToday")
    public ResponseBean merchantSummaryToday(@RequestBody MerchantSummarySearchBean merchantSummarySearchBean,
                                @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        merchantSummarySearchBean.setAgentNo(userInfoBean.getAgentNo());
        merchantSummarySearchBean.setAgentNode(userInfoBean.getAgentNode());
        List<MerchantSumBean> teamList = merchantEsService.merchantSummaryByTeamId(merchantSummarySearchBean, true);
        long merchantTotal = merchantEsService.countMerchant(merchantSummarySearchBean, true);
        Map<String, Object> result = new HashMap<>();
        result.put("teamList", teamList);
        result.put("merchantTotal", merchantTotal);
        return ResponseBean.success(result);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "4 首页->今日新增商户->商户汇总->代理商汇总", notes = SwaggerNotes.COUNT_BY_DIRECT_AGENT)
    @PostMapping("/countTodayByDirectAgent/{pageNo}/{pageSize}")
    public ResponseBean countTodayByDirectAgent(@RequestBody MerchantSummarySearchBean merchantSummarySearchBean,
                                                @PathVariable int pageNo,
                                                @PathVariable int pageSize,
                                                @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        pageNo = pageNo - 1 <= 0 ? 0 : pageNo - 1;
        pageSize = pageSize <= 1 ? 1 : pageSize;
        merchantSummarySearchBean.setAgentNo(userInfoBean.getAgentNo());
        merchantSummarySearchBean.setAgentNode(userInfoBean.getAgentNode());
        Tuple<List<MerchantSumBean>, Long> listLongTuple = merchantEsService.statisMerchantByDirectAgent(merchantSummarySearchBean,
                PageRequest.of(pageNo, pageSize), true);
        return ResponseBean.success(listLongTuple.v1(), listLongTuple.v2());
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "获取代理商开通的业务产品", notes = SwaggerNotes.LIST_BUSINESS_PRODUCT)
    @PostMapping("/listBusinessProduct")
    public ResponseBean listBusinessProduct(@RequestBody MerchantSearchBean searchBean, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        if (StringUtils.isNotBlank(searchBean.getAgentNo())) {
            boolean canAccess = accessService.canAccessTheAgent(userInfoBean.getAgentNode(), searchBean.getAgentNo());
            if (!canAccess) {
                return ResponseBean.error("无权操作");
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("bpId", "");
        map.put("bpName", "全部");
        result.add(map);
        result.addAll(Optional.ofNullable(merchantEsService.listBusinessProductByAgentNo(searchBean, userInfoBean.getAgentNo()))
                .orElse(new ArrayList<>()));
        return ResponseBean.success(result);
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "查询商户信息", notes = SwaggerNotes.LIST_MERCHANT_INFO)
    @PostMapping("/listMerchantInfo/{pageNo}/{pageSize}")
    public ResponseBean listMerchantInfo(
            @PathVariable int pageNo,
            @PathVariable int pageSize,
            @RequestBody MerchantSearchBean searchBean,
            @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agentNode = userInfoBean.getAgentNode();
        if (StringUtils.isNotBlank(searchBean.getAgentNo())) {
            boolean canAccess = accessService.canAccessTheAgent(userInfoBean.getAgentNode(), searchBean.getAgentNo());
            if (!canAccess) {
                return ResponseBean.error("无权操作");
            }
            AgentInfo agentInfo = agentInfoService.queryAgentInfo(searchBean.getAgentNo());
            agentNode = agentInfo.getAgentNode();
        }
        pageNo = pageNo - 1 <= 0 ? 0 : pageNo - 1;
        pageSize = pageSize <= 1 ? 1 : pageSize;
        Tuple<List<MerchantEsResultBean>, Long> listLongTuple = merchantEsService.listMerchantInfo(searchBean,
                PageRequest.of(pageNo, pageSize), agentNode);

        if (CollectionUtils.isNotEmpty(listLongTuple.v1())) {
            listLongTuple.v1().forEach(item -> {
                // 手机号打码显示
                item.setMobilePhone(StringUtils.mask4MobilePhone(item.getMobilePhone()));
                boolean isDirectMerchant = StringUtils.equalsIgnoreCase(item.getAgentNo(), userInfoBean.getAgentNo());
                item.setDirectMerchant(isDirectMerchant);
                // 非直营商户,不显示商户号
                if (!isDirectMerchant) {
                    item.setMerchantName("");
                }
            });
        }
        return ResponseBean.success(listLongTuple.v1(), listLongTuple.v2());
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "查询商户需要的参数", notes = SwaggerNotes.QUERY_MERCHANT_PARAMS)
    @GetMapping("/queryMerchantParams")
    public ResponseBean queryMerchantParams() {
        return ResponseBean.success(merchantEsService.queryMerchantParams());
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "查看商户详情", notes = SwaggerNotes.GET_MERCHANT_DETAILS)
    @GetMapping("/getMerchantDetails/{merchantNo}")
    public ResponseBean getMerchantDetails(@PathVariable String merchantNo,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        boolean canAccess = accessService.canAccessTheMerchant(userInfoBean.getAgentNode(), merchantNo, false);
        if (!canAccess) {
            return ResponseBean.error("该商户不存在");
        }
        return ResponseBean.success(merchantEsService.getMerchantDetails(merchantNo, userInfoBean.getAgentNode()));
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "列出可被替换的业务产品信息", notes = SwaggerNotes.LIST_CAN_REPLACEBPINFO)
    @GetMapping("/listCanReplaceBpInfo/{merchantNo}")
    public ResponseBean listCanReplaceBpInfo(@PathVariable String merchantNo,
                                             @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        boolean canAccess = accessService.canAccessTheMerchant(userInfoBean.getAgentNode(), merchantNo, true);
        if (!canAccess) {
            return ResponseBean.error("非直营商户,无法获取相关信息");
        }
        return ResponseBean.success(merchantEsService.listCanReplaceBpInfo(merchantNo, userInfoBean.getAgentNo()));
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "替换业务产品", notes = SwaggerNotes.REPLACE_BUSINESS_PRODUCT)
    @PostMapping("/replaceBusinessProduct")
    public ResponseBean replaceBusinessProduct(@RequestBody MerchantBpBean merchantBpBean,
                                               @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        boolean canAccess = accessService.canAccessTheMerchant(userInfoBean.getAgentNode(), merchantBpBean.getMerchantNo(), true);
        if (!canAccess) {
            return ResponseBean.error("非直营商户,无权操作");
        }
        if (merchantEsService.isOpenAgentUpdateBpSwitch(merchantBpBean.getMerchantNo())) {
            merchantEsService.replaceBusinessProduct(merchantBpBean.getMerchantNo(),
                    merchantBpBean.getBpId(),
                    merchantBpBean.getNewBpId(),
                    userInfoBean.getAgentNo());
            return ResponseBean.success();
        } else {
            return ResponseBean.error("暂不支持此功能修改");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "根据关键字模糊匹配商户", notes = SwaggerNotes.WILDCARD_MER_LIST)
    @PostMapping("/wildcardMerList")
    public ResponseBean wildcardMerList(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                        @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        String merchantKey = bodyParams.get("merchantKey");
        List<Map<String, Object>> wildcardMerList = new ArrayList<>();
        if (StringUtils.isNotBlank(merchantKey)) {

            wildcardMerList = merchantInfoService.queryMerListBykey(merchantKey, userInfoBean.getAgentNode());
        }
        return ResponseBean.success(wildcardMerList == null ? new ArrayList<>() : wildcardMerList);
    }

}
