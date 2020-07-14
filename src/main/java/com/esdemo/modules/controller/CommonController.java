package com.esdemo.modules.controller;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.annotation.LoginValid;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.RSAUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.AppInfoService;
import com.esdemo.modules.service.PublicDataService;
import com.esdemo.modules.service.SysConfigService;
import com.esdemo.modules.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:30
 */
@Slf4j
@Api(description = "公共模块")
@LoginValid(needLogin = false)
@RestController
//@SignValidate(needSign = false)
public class CommonController {

    @Resource
    private UserService userService;
    @Resource
    private AppInfoService appInfoService;
    @Resource
    private SysConfigService sysConfigService;
    @Resource
    private PublicDataService publicDataService;

    @KqSwaggerDeveloped
    @ApiOperation(value = "登陆", notes = SwaggerNotes.COMMON_LOGIN)
    @PostMapping("/login")
    public ResponseBean login(@ApiParam @RequestBody UserInfoBean userInfoBean, HttpServletRequest request) {
        if (userInfoBean == null ||
                StringUtils.isBlank(userInfoBean.getUserName()) ||
                StringUtils.isBlank(userInfoBean.getPassword()) ||
                StringUtils.isBlank(userInfoBean.getAgentOem())) {
            throw new AppException("用户名或密码有误");
        }
        String password = RSAUtils.decryptDataOnJava(userInfoBean.getPassword(), Constants.LOGIN_PRIVATE_KEY);
        if (StringUtils.isBlank(password)) {
            throw new AppException("用户名或密码有误");
        }
        String userName = userInfoBean.getUserName();
        UserInfoBean loginUser;
        String agentOem = userInfoBean.getAgentOem();
        // 快钱目前就只有这两个组织 110010 安收宝 110030 安POS
        List<String> agentOemList = Arrays.asList(agentOem, "110010", "110030");
        if (userName.contains("@")) {
            loginUser = userService.getUserInfoByEmail(userName, agentOemList);
        } else {
            loginUser = userService.getUserInfoByMobile(userName, agentOemList);
        }
        if (loginUser == null) {
            throw new AppException("用户名或密码有误");
        }
        checkUserIsLock(loginUser, password);
        loginUser.setPassword("");
        WebUtils.saveLoginUserInfo2Redis(loginUser);
        AppDeviceInfo appDeviceInfo = WebUtils.getAppDeviceInfo(request);
        updateJiGuangPushInfo(loginUser.getAgentNo(), appDeviceInfo);
        return ResponseBean.success(loginUser);
    }

    private void updateJiGuangPushInfo(String agentNo, AppDeviceInfo appDeviceInfo) {
        if (StringUtils.isBlank(agentNo) || appDeviceInfo == null) {
            return;
        }
        if (StringUtils.isBlank(appDeviceInfo.getAppNo(), appDeviceInfo.getSystemName(), appDeviceInfo.getJpushDevice(), appDeviceInfo.getAppNo())) {
            return;
        }
        String deviceType = StringUtils.equalsIgnoreCase(appDeviceInfo.getSystemName(), "android") ? "1" : "2";
        String userType = "v2Agent";
        Map<String, Object> map = userService.selectPushInfo(userType, agentNo);
        if (map == null) {
            userService.savePushInfo(userType, agentNo, appDeviceInfo.getJpushDevice(), deviceType, appDeviceInfo.getAppNo());
        } else {
            userService.updatePushInfo(userType, agentNo, appDeviceInfo.getJpushDevice(), deviceType, appDeviceInfo.getAppNo());
        }
    }

    private void checkUserIsLock(UserInfoBean loginUser, String password) {
        if (loginUser == null) {
            return;
        }
        // 检查用户是否被锁定
        if (loginUser.getLockTime() != null) {
            int lockTime = sysConfigService.getSysConfigValueByKey("agent_web_login_lock_time", 30, Integer::valueOf);
            int diffTime = (int) ((new Date().getTime() - loginUser.getLockTime().getTime()) / (1000 * 60));
            if (diffTime >= 0 && diffTime < lockTime) {
                int remainLockTime = lockTime - diffTime;
                remainLockTime = remainLockTime <= 0 ? 1 : remainLockTime;
                String message = "您的账号被锁定,锁定时间" + remainLockTime + "分钟";
                log.warn(message);
                throw new AppException(message);
            }
        }

        // 判断密码是否正确
        String encodePassword = Md5.md5Str(String.format("%s{%s}", password, loginUser.getPhone()));
        if (StringUtils.equalsIgnoreCase(encodePassword, loginUser.getPassword())) {
            log.warn("清空登陆错误次数");
            userService.clearWrongPasswordCount(loginUser.getUserId());
        } else {
            int wrongPasswordMaxCount = sysConfigService.getSysConfigValueByKey("agent_web_login_wrong_password_max_count", 5, Integer::valueOf);
            if (loginUser.getWrongPasswordCount() + 1 < wrongPasswordMaxCount) {
                userService.increaseWrongPasswordCount(loginUser.getUserId());
            } else {
                userService.lockLoginUser(loginUser.getUserId());
            }
            log.warn("用户名或密码有误");
            throw new AppException("用户名或密码有误");
        }
    }


    @OldSwaggerDeveloped
    @ApiOperation(value = "注销登陆")
    @GetMapping("/logout")
    public ResponseBean logout(HttpServletRequest request) {
        WebUtils.deleteLoginUserInfoFromRedis(request);
        return ResponseBean.success();
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "客戶端版本校验，不需要签名，新加隐私协议版本")
    @PostMapping("/checkVersion")
    @SignValidate(needSign = false)
    public ResponseBean checkVersion(HttpServletRequest request) {

        AppDeviceInfo appDeviceInfo = WebUtils.getAppDeviceInfo(request);
        log.info("获取获取设备公共参数：{}", JSONUtil.toJsonStr(appDeviceInfo));
        if (null == appDeviceInfo) {
            return ResponseBean.error("请求不合法");
        }
        Map<String, Object> checkMap = appInfoService.checkAppVersion(appDeviceInfo);
        if (CollectionUtils.isEmpty(checkMap)) {
            return ResponseBean.error("获取客户端版本失败");
        }
        //获取appNo
        String appNo = appDeviceInfo.getAppNo();
        if (StringUtils.isBlank(appNo)) {
            return ResponseBean.error("请求不合法");
        }
        String version = publicDataService.queryProtocolVersionByAppNo(appNo);
        checkMap.put("protocolVersion", version);
        return ResponseBean.success(checkMap);
    }
}
