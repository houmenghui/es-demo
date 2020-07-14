package com.esdemo.modules.controller;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.LoginValid;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.swagger.ProtocolSwaggerNotes;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.PublicDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2020/03/18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/protocol")
@Api(description = "隐私协议模块")
@RestController
@SignValidate(needSign = false)
@LoginValid(needLogin = false)
public class ProtocolController {

    @Resource
    private PublicDataService publicDataService;

    @ApiOperation(value = "下发隐私协议的版本", notes = ProtocolSwaggerNotes.QUERY_PROTOCOL_VERSION)
    @PostMapping("/queryProtocolVersion")
    @OldSwaggerDeveloped
    public ResponseBean queryProtocolVersion(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                             HttpServletRequest request) {
        try {
            //获取appNo
            AppDeviceInfo appDeviceInfo = WebUtils.getAppDeviceInfo(request);
            log.info("获取获取设备公共参数：{}", JSONUtil.toJsonStr(appDeviceInfo));
            if (null == appDeviceInfo) {
                return ResponseBean.error("请求不合法");
            }
            String appNo = appDeviceInfo.getAppNo();
            if (StringUtils.isBlank(appNo)) {
                return ResponseBean.error("请求不合法");
            }
            String version = publicDataService.queryProtocolVersionByAppNo(appNo);
            Map<String, String> resMap = new HashMap<>();
            resMap.put("version", version);
            return ResponseBean.success(resMap);
        } catch (Exception e) {
            log.error("下发隐私协议的版本异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("下发隐私协议的版本失败，请稍候再试");
        }
    }
}
