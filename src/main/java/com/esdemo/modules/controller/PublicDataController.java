package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.ALiYunOssUtil;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.redis.RedisUtils;
import com.esdemo.frame.utils.swagger.SwaggerNoteLmc;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.PublicDataVo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.SysDictDao;
import com.esdemo.modules.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lmc
 * @date 2019/5/28 11:36
 */
@Api(description = "公共数据下发")
@RestController
@RequestMapping("/publicData")
@Slf4j
public class PublicDataController {
    @Resource
    private PublicDataService publicDataService;
    @Resource
    private MachineManageService machineManageService;

    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private AgentLowerLevelService agentLowerLevelService;
    @Resource
    private SysDictDao sysDictDao;
    @Resource
    private FunctionManageService functionManageService;

    @KqSwaggerDeveloped
    @ApiOperation(value = "获取公共数据", notes = SwaggerNoteLmc.GET_PUBLIC_DATA)
    @PostMapping("/getPublicData")
    public ResponseBean getPublicData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {

        String agentNo = userInfoBean.getAgentNo();
        String userId = userInfoBean.getUserId();

        String cacheKey = String.format("agentApi2:publicData:%s:%s:%s", agentNo, userId,userInfoBean.getLoginToken());
        try {
            PublicDataVo publicDataVo = RedisUtils.get(cacheKey);
            if (publicDataVo != null) {
                log.info("......从redis获取公共数据");
                return ResponseBean.success(publicDataVo);
            }
        } catch (Exception e) {
            log.error("从redis获取公共数据异常:", e);
        }
        log.info("......从数据库获取公共数据");

        AgentInfo oneAgentInfo = agentInfoService.queryAgentInfo(userInfoBean.getOneAgentNo());
        String customTel = "4000002999";
        if (oneAgentInfo != null
                && StringUtils.isNotBlank(oneAgentInfo.getCustomTel())
                && oneAgentInfo.getCustomTel().trim().matches("(\\d|-)+")) {
            customTel = oneAgentInfo.getCustomTel().trim();
        }

        PublicDataVo publicDataVo = new PublicDataVo();
        publicDataVo.setShowHotLinePhone(true);
        publicDataVo.setHotLinePhone(customTel);
        publicDataVo.setHotLinePhoneTip(String.format("是否拨打服务热线: %s", customTel));
        //首页待设置代理商条数
        publicDataVo.setToBeSetListSize(agentLowerLevelService.getAgentLowerLevelToBeSetList(1,10,userInfoBean).getCount());
        //代理商解绑机具开关
        publicDataVo.setFunCode030(functionManageService.getAgentTerminalRelease(userInfoBean));
        //代理商进件功能入口
        publicDataVo.setFunCode044(functionManageService.getFunctionSwitch(userInfoBean,"044"));

        //放入缓存
        try {
            RedisUtils.set(cacheKey, publicDataVo, getConfigTTL(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("公共数据放入缓存异常:", e);
        }
        return ResponseBean.success(publicDataVo);
    }

    public int getConfigTTL() {
        String sysTtl = sysDictDao.getDictSysValue("AGENT2_REDIS_TTL");
        if (StringUtils.isBlank(sysTtl)) {
            return 120;
        }
        try {
            return Integer.parseInt(sysTtl);
        }catch (Exception e){
            return 120;
        }
    }

//    @OldSwaggerDeveloped
//    @ApiOperation(value = "获取公共数据", notes = SwaggerNoteLmc.GET_PUBLIC_DATA)
//    @PostMapping("/getPublicData")
//    public ResponseBean getPublicData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
//
//        String agent_no = userInfoBean.getAgentNo();
//        String agent_node = userInfoBean.getAgentNode();
//        String user_id = userInfoBean.getUserId();
//
//        String cacheKey = String.format("agentApi2:publicData:%s:%s:%s", agent_no, user_id,userInfoBean.getLoginToken());
//        /*返回信息*/
//        Map<String, Object> return_map = new HashMap<>();
//
//        //从缓存获取，但是不能影响整体业务，异常时获取原始数据
//        boolean isCache = true;
//        try {
//            return_map = RedisUtils.get(cacheKey);
//            if (CollectionUtils.isEmpty(return_map)) {
//                isCache = false;
//            }
//        } catch (Exception e) {
//            log.error("从redis获取公共数据异常{}", e);
//            isCache = false;
//        }
//
//        if (isCache) {
//            log.info("......从redis获取公共数据");
//            return ResponseBean.success(return_map);
//        }
//        log.info("......从数据库获取公共数据");
//        return_map = new HashMap<>();
//
//        /*获取代理商信息*/
//        Map<String, Object> agent_info_map = machineManageService.getAgentInfoByAgentNo(agent_no);
//
//        Map<String, Object> oem_map = noticeService.getOemInfo(agent_no);
//        String oem_type = StringUtils.filterNull(oem_map.get("oem_type"));
//        //为空查询所有的
//        if (StringUtils.isBlank(oem_type)) {
//            oem_type = "0";
//        }
//        String one_level_id = StringUtils.filterNull(oem_map.get("one_level_id"));
//        //是否是一级代理商
//        String one_level_id_flag = "true";
//        if (one_level_id.equals(agent_no)) {
//            one_level_id_flag = "false";
//        }
//
//        String time_str = publicDataService.getTimeStr(user_id);
//        List list = publicDataService.getMsgList(one_level_id_flag, oem_type, time_str);
//        String notice_new_flag = "0";
//        if (null != list && list.size() > 0) {
//            notice_new_flag = "1";
//        }
//
//        //代理商一级解绑开关
//        return_map.put("machine_release_one_agent_switch", getFunctionSwitch(userInfoBean, "030"));
//        //盛代宝首页头条展示开关
//        return_map.put("sdb_home_msg_switch", getFunctionSwitch(userInfoBean, "043"));
//
//        String super_push_share_switch = "0";
//        //1.获取总开关
//        String main_switch_value = WebUtils.getDictValue("SUPER_PUSH_AGENT_SWITCH");
//        if ("1".equals(main_switch_value)) {
//            String entityId = userInfoBean.getAgentNo();
//            //2.获取代理商超级推开关字段
//            if ("1".equals(StringUtils.filterNull(agent_info_map.get("promotion_switch")))
//                //检查是否代理了超级推无卡支付业务产品,数据字典 SUPER_PUSH_BP_ID 查询bpId
//                && (publicDataService.selectBySuperPushBpId(entityId,"SUPER_PUSH_BP_ID") > 0)) {
//                super_push_share_switch = "1";
//            }
//        }
//
//        //当前登录代理商是否开通兑POS业务,1为已开通,0为未开通
//        return_map.put("openFloor9Points",agent_info_map.get("open_floor9_points") );
//
//        //超级推
//        return_map.put("super_push_share_switch", super_push_share_switch);
//        //收单商户进件菜单开关
//        return_map.put("acq_mer_rec_switch", getFunctionSwitch(userInfoBean, "038"));
//        //商户进件菜单开关
//        return_map.put("mer_rec_switch", getFunctionSwitch(userInfoBean, "051"));
//        //是否设置安全密码
//        return_map.put("is_safe_password", StringUtils.isBlank(StringUtils.filterNull(agent_info_map.get("safe_password"))));
//        //是否设置安全手机
//        return_map.put("agent_safe_phone", StringUtils.filterNull(agent_info_map.get("safephone")));
//        //是否有新的公告消息
//        return_map.put("notice_new_flag", notice_new_flag);
//
//        String survey_show_flag = "0";
//        //调单角标显示
//        if (1 == userInfoBean.getAgentLevel() && publicDataService.selectSurveyOrderInfoByOneAgent(agent_no, agent_node).size() > 0) {
//            survey_show_flag = "1";
//        }
//
//        //调单角标显示
//        if (1 != userInfoBean.getAgentLevel() && publicDataService.selectSurveyOrderInfo(agent_no, agent_node).size() > 0) {
//            survey_show_flag = "1";
//        }
//
//        return_map.put("survey_show_flag", survey_show_flag);
//
//        String entityId = userInfoBean.getAgentNo();
//        return_map.put("right_share_activity", agentShareService.getAgentShareList(entityId).size() > 0 ? true : false);
//
//        // 三方数据开关
//        Integer entrySwitch = threeDataService.entrySwitch(agent_no);
//        return_map.put("threeDataEntrySwitch", entrySwitch);
//
//        //闪付二期需求，判断当前登录代理商是否开通激活码功能
//        String openNfcActCodeFlag = "1";
//        ProviderBean providerBean = providerService.queryServiceCost(agent_no, RepayEnum.NFC.getType());
//        if (providerBean == null || StringUtils.isBlank(providerBean.getNfcOrigCode())) {
//            openNfcActCodeFlag = "0";
//        }
//        return_map.put("openNfcActCodeFlag", openNfcActCodeFlag);
//        //放入缓存
//        try {
//            RedisUtils.set(cacheKey, return_map, 2, TimeUnit.MINUTES);
//        } catch (Exception e) {
//            log.info("公共数据放入缓存异常{}", e);
//        }
//        return ResponseBean.success(return_map);
//    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "下载app", notes = SwaggerNoteLmc.GET_APP_INFO)
    @PostMapping("/getAppInfo")
    public ResponseBean getPublicData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, HttpServletRequest req) {
        List<Map<String, Object>> app_list = publicDataService.getAppInfo(WebUtils.getAppDeviceInfo(req).getAppNo(), userInfoBean.getTeamId());
        List result_list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : app_list) {
            if (!"".equals(StringUtils.filterNull(map.get("code_url")))) {
                map.put("code_url", ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, map.get("code_url").toString(), new Date(new Date().getTime() + 3600000)));
                result_list.add(map);
            }
        }
        if (result_list.size() > 1) {
            return ResponseBean.success(result_list);
        } else {
            return ResponseBean.error("无可用app下载");
        }
    }


    public String getFunctionSwitch(UserInfoBean userIfo, String function_number) {
        String function_switch = "1";
        Map<String, Object> functionMap = machineManageService.getFunctionManage(function_number);
        if (!CollectionUtils.isEmpty(functionMap)) {
            if ("1".equals(StringUtils.filterNull(functionMap.get("function_switch")))) {
                //开启代理商就只能对应的代理商才显示头条消息
                if ("1".equals(StringUtils.filterNull(functionMap.get("agent_control")))) {
                    if ("".equals(StringUtils.filterNull(machineManageService.getAgentFunction(userIfo.getOneAgentNo(), function_number)))) {
                        function_switch = "0";
                    }
                }
            } else {
                function_switch = "0";
            }
        }

        // 判断该代理商是否允许进件 黑名单优先级最高
        if (function_number.equals("051")) {
            // 是否黑名单 不包含下级
            long blacklistNotContains = machineManageService.countBlacklistNotContains(userIfo.getAgentNo());
            if (blacklistNotContains > 0) {
                function_switch = "0";
            }
            // 是否黑名单 包含下级
            long blacklistContains = machineManageService.countBlacklistContains(userIfo.getAgentNode());
            if (blacklistContains > 0) {
                function_switch = "0";
            }
        }
        return function_switch;
    }

}
