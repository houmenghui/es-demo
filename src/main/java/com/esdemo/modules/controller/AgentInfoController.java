package com.esdemo.modules.controller;

import cn.hutool.crypto.SecureUtil;
import com.esdemo.frame.annotation.*;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.interceptor.ClientSignValidateInterceptor;
import com.esdemo.frame.utils.ClientInterface;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.AgentShareRuleTask;
import com.esdemo.modules.bean.HappyBackData;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.AgentShareService;
import com.esdemo.modules.service.SysConfigService;
import com.esdemo.modules.service.SysDictService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.collect.Tuple;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 代理商管理
 * @date 2019/5/20
 */
@Api(description = "代理商管理模块")
@RequestMapping("/agentInfo")
@RestController
@Slf4j
public class AgentInfoController {
    @Resource
    private AgentInfoService agentInfoService;

    @Resource
    private AgentShareService agentShareService;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private SysDictService sysDictService;

    @OldSwaggerDeveloped
    @ApiOperation(value = "新增代理商",notes = SwaggerNotes.INSERT_AGENT)
    @PostMapping("/insertAgent")
    public ResponseBean insertAgent(@RequestBody String params,
                                    @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("新增代理商 请求参数 ===> {}",params);
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params, Object.class);
            return ResponseBean.success(agentInfoService.insertAgent(paramsMap, userInfoBean));
        } catch (AppException e) {
            log.error("新增代理商出错", e);
            return ResponseBean.error(e.getMessage());
        }catch (Exception e) {
            log.error("新增代理商出错", e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @LoginValid(needLogin = false)
    @SignValidate(needSign = false)
    @ApiOperation(value = "拓展代理",notes = SwaggerNotes.INSERT_AGENT_EXPAND)
    @GetMapping("/insertAgentExpand")
    public String insertAgentExpand(AgentInfo agentInfo) {
        try {
            log.info("拓展代理 请求参数 ==> {}",agentInfo);
            if (agentInfo == null || StringUtils.isBlank(agentInfo.getParentId()) || StringUtils.isBlank(agentInfo.getUserId()) ||
                    StringUtils.isBlank(agentInfo.getAgentName()) || StringUtils.isBlank(agentInfo.getLinkName()) ||
                    StringUtils.isBlank(agentInfo.getProvince()) || StringUtils.isBlank(agentInfo.getCity()) ||
                    StringUtils.isBlank(agentInfo.getArea()) || StringUtils.isBlank(agentInfo.getAddress()) ||
                    StringUtils.isBlank(agentInfo.getMobilephone()) || StringUtils.isBlank(agentInfo.getSmsCode())||
                    StringUtils.isBlank(agentInfo.getSafePassword()) || StringUtils.isBlank(agentInfo.getSign())){
                return "regcallback" + "(" + GsonUtils.toJson(ResponseBean.error("必传参数不能为空")) + ")";
            }
            //校验sign
            String sign = agentInfo.getSign();
            String parentId = agentInfo.getParentId();
            String signMsg = SecureUtil.md5(parentId + "key=" + ClientSignValidateInterceptor.DEFAULT_SIGN_KEY);
            if (!sign.equalsIgnoreCase(signMsg)) {
                return "regcallback" + "(" + GsonUtils.toJson(ResponseBean.error("验签失败")) + ")";
            }
            return "regcallback" + "(" + GsonUtils.toJson(agentInfoService.insertAgentExpand(agentInfo)) + ")";
        } catch (Exception e) {
            log.error("拓展代理商出错", e);
            return "regcallback" + "(" + GsonUtils.toJson(ResponseBean.error("操作异常")) + ")";
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "拓展代理,海报下发",notes = SwaggerNotes.SELECT_POSTER)
    @GetMapping("/selectPoster")
    public ResponseBean selectPoster(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            return ResponseBean.success(agentInfoService.selectPoster(userInfoBean));
        }catch (Exception e){
            log.error("海报查询出错",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "代理商条件查询",notes = SwaggerNotes.QUERY_AGENT_INFO_LIST)
    @PostMapping("/queryAgentInfoList")
    public ResponseBean queryAgentInfoList(@RequestBody String params,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("代理商条件查询 请求参数 ===> {}",params);
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            String pageNo = paramsMap.get("pageNo");
            String pageSize = paramsMap.get("pageSize");
            String hasChild = paramsMap.get("hasChild");
            String entityId = userInfoBean.getAgentNo();
            if (paramsMap == null || StringUtils.isBlank(pageNo) ||
                    StringUtils.isBlank(pageSize) || StringUtils.isBlank(hasChild)){
                 return ResponseBean.error("参数有误");
            }
            List<AgentInfo> list = agentInfoService.queryAgentInfoList(paramsMap,userInfoBean);
            AgentInfo entityInfo = agentInfoService.queryAgentInfo(entityId);
            Map<String, String> resultMap = null;
            Integer openFloor9Points = entityInfo.getOpenFloor9Points();
            log.info("当前登录代理商积分兑业务开通状态==== {},是否调用9楼token 接口 ==== {} ====", openFloor9Points,1 == openFloor9Points);
            if(1 == openFloor9Points){//当前登录代理商开通了才显示,才需要调9楼接口
                resultMap = tokenMap(entityId);
            }
            if (list != null && list.size() > 0){
                String convertLinkAppOpenUrl = sysConfigService.getSysConfigValueByKey("CONVERT_LINK_APP_OPEN_URL");//开通积分兑/修改地址跳转
                String convertLinkAppUpdateUrl = sysConfigService.getSysConfigValueByKey("CONVERT_LINK_APP_UPDATE_URL");//开通积分兑/修改地址跳转
                log.info("数据库配置开通积分兑地址  ==== {} =========", convertLinkAppOpenUrl);
                log.info("数据库配置修改积分兑地址  ==== {} =========", convertLinkAppUpdateUrl);
                for (AgentInfo agentInfo : list) {
                    agentInfo.setIsDirectChild(false);
                    String convertLinkUrl = agentInfo.getOpenFloor9Points() == 0 ? convertLinkAppOpenUrl : convertLinkAppUpdateUrl;
                    if(resultMap != null){
                        convertLinkUrl = convertLinkUrl + "?signStr=" + resultMap.get("data");
                        agentInfo.setConvertLink(convertLinkUrl);//设置跳转链接+token
                        log.info("拼接后的跳转地址 convertLinkUrl = {} ", convertLinkUrl);
                    }
                    if (entityId.equals(agentInfo.getParentId())){
                        agentInfo.setIsDirectChild(true);
                    }
                }
            }
            PageInfo<AgentInfo> pageInfo = new PageInfo(list);
            return ResponseBean.success(pageInfo, pageInfo.getTotal());
        }catch (AppException e){
            log.error("代理商条件查询出错",e);
            return ResponseBean.error(e.getMessage());
        }catch (Exception e){
            log.error("代理商条件查询出错",e);
            return ResponseBean.error("查询异常");
        }
    }

    /**
     * 调用9楼接口获取token
     * @param entityId
     * @return
     */
    private Map<String, String> tokenMap(String entityId) {
        String key = sysConfigService.getSysConfigValueByKey("CORE_JF_POS_DESPLUS_KEY");
        String url = sysConfigService.getSysConfigValueByKey("JF_POS_ACCESS_WEB_URL");//token接口IP端口,代理商
        String floor9Token = ClientInterface.getFloor9Token(entityId, key, url);
        log.info("调用9楼token接口返回 {} ===",floor9Token);
        return GsonUtils.fromJson2Map(floor9Token, String.class);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询代理商基本信息",notes = SwaggerNotes.QUERY_AGENT_INFO_BYNO)
    @GetMapping("/queryAgentInfoByNo/{agent_no}")
    public ResponseBean queryAgentInfoByNo(@PathVariable String agent_no,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询代理商基本信息 请求参数 agent_no ===> {}",agent_no);
            if (signAgentNo(agent_no, userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(agentInfoService.queryAgentInfoByNo(agent_no));
        }catch (Exception e){
            log.error("代理商详情基本信息出错",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询代理商代理的产品",notes = SwaggerNotes.GET_AGENT_PRODUCT_LIST)
    @GetMapping("/getAgentProductList/{agent_no}")
    public ResponseBean getAgentProductList(@PathVariable String agent_no,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询代理商代理的产品 请求参数 agent_no===> {}",agent_no);
            if (signAgentNo(agent_no, userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(agentInfoService.getAgentProductList(agent_no));
        }catch (Exception e){
            log.error("查询代理商代理的产品出错",e);
            return ResponseBean.error("查询异常");
        }
    }

    /**
     * 可以查自己及下级
     * @param agent_no
     * @param userInfoBean
     * @return
     */
    private boolean signAgentNo(@PathVariable String agent_no, @CurrentUser @ApiIgnore UserInfoBean userInfoBean) {
        String entityNode = userInfoBean.getAgentNode();
        AgentInfo agentInfo = agentInfoService.queryAgentInfo(agent_no);
        if (!(agentInfo.getAgentNode()).contains(entityNode)) {
            log.info("所传代理商 {} 不是当前登录代理商 {} 下级", agent_no, entityNode);
            return true;
        }
        return false;
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询代理商分润列表",notes = SwaggerNotes.GET_AGENT_SHARE_API_LIST)
    @GetMapping("/getAgentShareApiList/{agent_no}")
    public ResponseBean getAgentShareApiList(@PathVariable String agent_no,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询代理商分润列表 请求参数 agent_no===> {}",agent_no);
            if (signAgentNo(agent_no, userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(agentInfoService.getAgentShareList(agent_no));
        }catch (Exception e){
            log.error("查询代理商分润列表异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "修改分润",notes = SwaggerNotes.UPDATE_AGENT_SHARE)
    @PostMapping("/updateAgentShare")
    public ResponseBean updateAgentShare(@RequestBody AgentShareRuleTask agentShareRuleTask,
                                         @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("修改分润 请求参数 ===> {}",agentShareRuleTask);
            String entityId = userInfoBean.getAgentNo();
            if (agentShareRuleTask == null || !entityId.equals(agentShareRuleTask.getEntityId()) ||
                    agentShareRuleTask.getShareId() == null || agentShareRuleTask.getEfficientDate() == null ||
                    agentShareRuleTask.getShareProfitPercent() == null || agentShareRuleTask.getCost() == null ||
                    agentShareRuleTask.getProfitType() == null || agentShareRuleTask.getAgentNo() == null){
                return ResponseBean.error("参数有误");
            }
            return agentShareService.updateAgentShare(agentShareRuleTask,userInfoBean);
        } catch (AppException e) {
            log.error("修改分润异常", e);
            return ResponseBean.error(StringUtils.isBlank(e.getMessage()) ? "操作异常" : e.getMessage());
        }catch (Exception e){
            log.error("修改分润异常",e);
            return ResponseBean.error("操作异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询修改分润记录",notes = SwaggerNotes.SELECT_AGENT_SHARE)
    @GetMapping("/selectAgentShare/{shareId}/{pageNo}/{pageSize}")
    public ResponseBean selectAgentShare(@PathVariable Long shareId,
                                         @PathVariable Integer pageNo,
                                         @PathVariable Integer pageSize,
                                         @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询修改分润记录 请求参数 shareId===> {},pageNo===> {},pageSize===> {}",shareId,pageNo,pageSize);
            if (shareId == null){
                return ResponseBean.error("参数有误");
            }
            PageHelper.startPage(pageNo, pageSize,false);
            return ResponseBean.success(new PageInfo<>(agentShareService.selectAgentShare(shareId,userInfoBean)));
        }catch (Exception e){
            log.error("查询修改分润记录",e);
            return ResponseBean.error(StringUtils.isBlank(e.getMessage()) ? "操作异常" : e.getMessage());
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询代理商的费率列表",notes = SwaggerNotes.GET_AGENT_RATE_LIST)
    @GetMapping("/getAgentRateList/{agent_no}")
    public ResponseBean getAgentRateList(@PathVariable String agent_no,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询代理商费率列表 请求参数 agent_no===> {}",agent_no);
            if (signAgentNo(agent_no, userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(agentInfoService.getAgentRateList(agent_no));
        }catch (Exception e){
            log.error("查询代理商的费率列表异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询代理商的限额列表",notes = SwaggerNotes.GET_AGENT_QUOTA_LIST)
    @GetMapping("/getAgentQuotaList/{agent_no}")
    public ResponseBean getAgentQuotaList(@PathVariable String agent_no,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("查询代理商限额列表 请求参数 agent_no===> {}",agent_no);
            if (signAgentNo(agent_no, userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(agentInfoService.getAgentQuotaList(agent_no));
        }catch (Exception e){
            log.error("查询代理商的限额异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "获取分润服务信息",notes = SwaggerNotes.GET_AGENT_SERVICES)
    @PostMapping("/getAgentServices")
    public ResponseBean getAgentServices(@RequestBody String params,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("获取分润服务信息 请求参数==> {}",params);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params, Object.class);
            if (paramsMap == null || paramsMap.get("agentNo") == null || paramsMap.get("bpIds") == null ||
                     signAgentNo(paramsMap.get("agentNo").toString(), userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            String agentNo = paramsMap.get("agentNo").toString();
            List<String> bpIds = GsonUtils.fromJson2List(paramsMap.get("bpIds").toString(), String.class);
            return ResponseBean.success(agentInfoService.getAgentServices(bpIds,agentNo));
        }catch (Exception e){
            log.error("获取分润服务信息",e);
            return ResponseBean.error("查询异常");
        }
    }
    @OldSwaggerDeveloped
    @ApiOperation(value = "新增代理商,查询欢乐返活动",notes = SwaggerNotes.SELECT_HAPPY_BACK)
    @GetMapping("/selectHappyBack/{agentNo}")
    public ResponseBean selectHappyBack(@PathVariable String agentNo,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("新增代理商,查询欢乐返活动 请求参数==> {}",agentNo);
        try {
            if (StringUtils.isBlank(agentNo) || signAgentNo(agentNo, userInfoBean)){
                return ResponseBean.error("参数有误");
            }
            List<HappyBackData> list = agentInfoService.selectHappyBack(agentNo);
            if (list != null && list.size() > 0) {
                Map<String, Object> supportRankMap = agentInfoService.getSupportRank(agentInfoService.queryAgentInfo(userInfoBean.getAgentNo()));
                Boolean fullPrizeLevelFlag = (Boolean)supportRankMap.get("fullPrizeLevelFlag");
                Boolean notFullDeductLevelFlag = (Boolean)supportRankMap.get("notFullDeductLevelFlag");
                for (HappyBackData happyBackData : list) {
                    happyBackData.setActivityCode(agentInfoService.getFunctionManagerByNum(happyBackData.getActivityCode()));
                    happyBackData.setFullPrizeLevelFlag(fullPrizeLevelFlag);
                    happyBackData.setNotFullDeductLevelFlag(notFullDeductLevelFlag);
                }
            }
            return ResponseBean.success(list);
        }catch (Exception e){
            log.error("新增代理商,查询欢乐返活动",e);
            return ResponseBean.error("查询异常");
        }
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "查询登陆代理商直接下级", notes = SwaggerNotes.LIST_AGENT_INFO)
    @PostMapping("/listAgentInfo/{pageNo}/{pageSize}")
    public ResponseBean listAgentInfo(
            @PathVariable int pageNo,
            @PathVariable int pageSize,
            @RequestBody AgentInfo agentInfo,
            @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("查询登录代理商直接下级 请求参数 ===> {}",agentInfo);
//        boolean isDirect = StringUtils.equalsIgnoreCase("1", agentInfo.getAgentType());
        pageNo = pageNo - 1 <= 0 ? 0 : pageNo - 1;
        pageSize = pageSize <= 1 ? 1 : pageSize;
        Tuple<List<AgentInfo>, Long> result = agentInfoService.listAgentInfoByKeyword(userInfoBean, agentInfo.getAgentType(), agentInfo.getKeyword(), PageRequest.of(pageNo, pageSize));
        return ResponseBean.success(result.v1(), result.v2());
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "调用9楼接口获取token下发拼接后的链接给客户端,首页入口", notes = SwaggerNotes.GET_FLOOR9_TOKEN)
    @PostMapping("/getFloor9Token")
    public ResponseBean getFloor9Token(@RequestBody String params,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        try {
            log.info("调用9楼接口获取token 请求参数 ===> {}",params);
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            String agentNo = paramsMap.get("agentNo");
            String entityId = userInfoBean.getAgentNo();
            if(!agentNo.equals(entityId)){
                log.info("==== 所传代理商编号 {} 不是当前登录代理商编号 {} =====", agentNo, entityId);
               return ResponseBean.error("参数有误!");
            }
            Map<String, String> resultMap = tokenMap(agentNo);//获取token
            String jfPosAccessUrl = sysConfigService.getSysConfigValueByKey("HOME_PAGE_POS_URL");//首页跳转入口
            String url = jfPosAccessUrl + "?signStr=" + resultMap.get("data");
            log.info("首页入口:拼接后下发给客户端的9楼的链接地址  ===> {}", url);
            return ResponseBean.success(url,resultMap.get("msg"));
        }catch (Exception e){
            log.error("调用9楼接口获取token出现异常",e);
            return ResponseBean.error("接口异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "超级推推广优化参数下发", notes = SwaggerNotes.CJT_PARAMS)
    @GetMapping("/cjtParams/{clientFlag}")
    public ResponseBean cjtParams(@PathVariable String clientFlag,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("参数clientFlag: {}",clientFlag);
        Map<String, Object> params = new HashMap<>();
        String userId = userInfoBean.getUserId();
        //https://core的地址/cjt/cjtShare?recommendedUserId=代理商的用户id&appNo=200010&source=V2-agent;
        String coreUrl = sysDictService.getDictSysValue("CORE_SERVICE_URL");
        StringBuffer shareUrl = new StringBuffer(coreUrl)
                .append("cjt/cjtShare?recommendedUserId=")
                .append(userId)
                .append("&appNo=200010&source=V2-agent");
        params.put("shareUrl", shareUrl);
        params.put("saveUrl", shareUrl.toString() + "&operType=saveImage");
        if(StringUtils.isBlank(clientFlag)){
            return ResponseBean.error("参数有误");
        }
        if("android".equals(clientFlag)){
            String result = ClientInterface.cjtShareForAliYun(userId);
            if(StringUtils.isNotBlank(result)){
                Map<String, Map> msg = GsonUtils.fromJson2Map(result, Map.class);
                Map<String, Object> map = msg.get("body");
                params.put("imgUrl", map.get("imgUrl"));
            }
        }
        return ResponseBean.success(params);
    }
}
