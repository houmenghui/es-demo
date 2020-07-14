package com.esdemo.modules.controller;

import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.LoginValid;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.SmsTemplate;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.*;
import com.esdemo.frame.utils.captcha.Captcha;
import com.esdemo.frame.utils.captcha.GifCaptcha;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.frame.utils.redis.RedisUtils;
import com.esdemo.frame.utils.swagger.SettingSwaggerNotes;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.SmsService;
import com.esdemo.modules.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/setting")
@Api(description = "设置模块")
@RestController
public class SettingController {

    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private UserService userService;
    @Resource
    private SmsService smsService;

    @ApiOperation(value = "登录-找回登录密码第一步-验证短信验证码", notes = SettingSwaggerNotes.SEEK_LOGIN_PWD_FIRST)
    @PostMapping("/seekLoginPwdFirst")
    @OldSwaggerDeveloped
    @LoginValid(needLogin = false)
    public ResponseBean seekLoginPwdFirst(@ApiIgnore @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String mobileNo = bodyParams.get("mobileNo");
            String verifyCode = bodyParams.get("verifyCode");
            if (StringUtils.isBlank(mobileNo, verifyCode)) {
                return ResponseBean.error("必要参数不能有空");
            }
            boolean correct = smsService.validateSmsCode(mobileNo, Constants.TEAM_ID_999, verifyCode);
            if (!correct) {
                return ResponseBean.error("短信验证码有误");
            }
            Map<String, String> res = new HashMap<>();
            res.put("mobileNo", mobileNo);
            return ResponseBean.success(res);

        } catch (Exception e) {
            log.error("找回登录密码第一步异常{}", e);
            return ResponseBean.error("找回登录密码失败，请稍候再试");
        }
    }

    @ApiOperation(value = "登录-找回登录密码第二步-设置登录密码", notes = SettingSwaggerNotes.SEEK_LOGIN_PWD_SECOND)
    @PostMapping("/seekLoginPwdSecond")
    @OldSwaggerDeveloped
    @LoginValid(needLogin = false)
    public ResponseBean seekLoginPwdSecond(@ApiIgnore @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String mobileNo = bodyParams.get("mobileNo");
            String encryNewLoginPwd = bodyParams.get("newLoginPwd");
            String encryNewConfirmLoginPwd = bodyParams.get("newConfirmLoginPwd");
            if (StringUtils.isBlank(mobileNo, encryNewLoginPwd, encryNewConfirmLoginPwd)) {
                return ResponseBean.error("必要参数不能有空");
            }
            Map<String, Object> dbUserInfo = userService.getAgentMobilephone(mobileNo, Constants.TEAM_ID_999);
            if (dbUserInfo == null) {
                return ResponseBean.error("此手机号未注册");
            }
            String userId = StringUtils.filterNull(dbUserInfo.get("user_id"));
            if (StringUtils.isBlank(userId)) {
                return ResponseBean.error("此用户信息合法");
            }
            //密码明文
            String newLoginPwd = RSAUtils.decryptDataOnJava(encryNewLoginPwd, Constants.LOGIN_PRIVATE_KEY);
            String newConfirmLoginPwd = RSAUtils.decryptDataOnJava(encryNewConfirmLoginPwd, Constants.LOGIN_PRIVATE_KEY);
            if (StringUtils.isBlank(newLoginPwd, newConfirmLoginPwd)) {
                return ResponseBean.error("非法操作");
            }
            if (!newLoginPwd.equals(newConfirmLoginPwd)) {
                return ResponseBean.error("确认新密码输入不一致");
            }
            String oldMd5LoginPwd = Md5.md5Str(newLoginPwd + "{" + mobileNo + "}");
            userService.updateUserLoginPwd(userId, newLoginPwd);

            return ResponseBean.success();

        } catch (Exception e) {
            log.error("找回登录密码第二步异常{}", e);
            return ResponseBean.error("找回登录密码失败，请稍候再试");
        }
    }

    @ApiOperation(value = "设置-修改登录密码", notes = SettingSwaggerNotes.UPDATE_LOGIN_PWD)
    @PostMapping("/updateLoginPwd")
    @OldSwaggerDeveloped
    public ResponseBean updateLoginPwd(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                       @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String dbAgentNo = userInfoBean.getAgentNo();
            String dbAgentUserId = userInfoBean.getUserId();

            Map<String, Object> dbUserInfo = userService.getAgentByUserId(dbAgentUserId);
            if (CollectionUtils.isEmpty(dbUserInfo)) {
                return ResponseBean.error("当前用户信息不存在");
            }
            String dbAgentLoginPwd = StringUtils.filterNull(dbUserInfo.get("password"));
            String dbAgentMobilePhone = StringUtils.filterNull(dbUserInfo.get("mobilephone"));

            String encryOldLoginPwd = bodyParams.get("oldLoginPwd");
            String encryNewLoginPwd = bodyParams.get("newLoginPwd");
            String encryNewConfirmLoginPwd = bodyParams.get("newConfirmLoginPwd");
            if (StringUtils.isBlank(encryOldLoginPwd, encryNewLoginPwd, encryNewConfirmLoginPwd)) {
                return ResponseBean.error("必要参数不能有空");
            }
            //密码明文
            String oldLoginPwd = RSAUtils.decryptDataOnJava(encryOldLoginPwd, Constants.LOGIN_PRIVATE_KEY);
            String newLoginPwd = RSAUtils.decryptDataOnJava(encryNewLoginPwd, Constants.LOGIN_PRIVATE_KEY);
            String newConfirmLoginPwd = RSAUtils.decryptDataOnJava(encryNewConfirmLoginPwd, Constants.LOGIN_PRIVATE_KEY);
            if (StringUtils.isBlank(oldLoginPwd, newLoginPwd, newConfirmLoginPwd)) {
                return ResponseBean.error("非法操作");
            }
            if (oldLoginPwd.equals(newLoginPwd)) {
                return ResponseBean.error("新密码不能与旧密码一致");
            }
            if (!newLoginPwd.equals(newConfirmLoginPwd)) {
                return ResponseBean.error("确认新密码输入不一致");
            }
            String oldMd5LoginPwd = Md5.md5Str(oldLoginPwd + "{" + dbAgentMobilePhone + "}");
            if (StringUtils.isBlank(oldMd5LoginPwd) || !oldMd5LoginPwd.equals(dbAgentLoginPwd)) {
                return ResponseBean.error("旧密码输入不正确");
            }
            int count = userService.updateUserLoginPwd(dbAgentUserId, newLoginPwd);
            if (count < 1) {
                return ResponseBean.error("修改登录密码失败");
            }
            return ResponseBean.success();

        } catch (Exception e) {
            log.error("当前登录代理商{}设置-修改登录密码异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("修改登录密码失败，请稍候再试");
        }
    }

    @ApiOperation(value = "获取图片验证码", notes = SettingSwaggerNotes.LOAD_CAPTCHA)
    @GetMapping("/loadCaptcha")
    @OldSwaggerDeveloped
    @LoginValid(needLogin = false)
    @SignValidate(needSign = false)
    public void loadCaptcha(HttpServletResponse response, @RequestParam Map<String,Object> params) {
        try {
            String uuid = StringUtils.filterNull(params.get("uuid"));
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");
            /**
             * gif格式动画验证码
             * 宽，高，位数。
             */
            Captcha captcha = null;
            if (StringUtils.isBlank(uuid)) {
                captcha = new GifCaptcha(146, 33, 4);
            } else {
                captcha = new GifCaptcha(146, 33, 4, uuid);
            }
            //输出
            captcha.out(response.getOutputStream());
        } catch (Exception e) {
            log.error("获取图片验证码异常{}", e);
        }
    }

    @ApiOperation(value = "发送短信验证码", notes = SettingSwaggerNotes.SEND_SMS_VALIDATE_CODE)
    @PostMapping("/sendSmsValidateCode")
    @OldSwaggerDeveloped
    @LoginValid(needLogin = false)
    public ResponseBean sendSmsValidateCode(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                            HttpServletRequest request,
                                            @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String uuid = bodyParams.get("uuid");
            String templateCode = bodyParams.get("templateCode");
            String mobileNo = bodyParams.get("mobileNo");
            if (StringUtils.isBlank(mobileNo, templateCode)) {
                return ResponseBean.error("必要参数不能为空");
            }
            //获取短信模板
            SmsTemplate smsTemplate = SmsTemplate.getTemplateByCode(templateCode);
            if (null == smsTemplate) {
                return ResponseBean.error("非法操作");
            }
            //找回密码外的业务类型，必须要登录状态
            if (!SmsTemplate.SEEK_LOGIN_PWD.getCode().equals(templateCode)) {
                if (null == userInfoBean) {
                    return ResponseBean.error("没有登陆");
                }
            }
            String captcha = bodyParams.get("captcha");
            if (StringUtils.isBlank(captcha)) {
                return ResponseBean.error("请先输入图形验证码");
            }
            //如果有唯一标识，走新的确认接口，没有则取原来的即可
            if (!StringUtils.isBlank(uuid)) {
                String redisCaptchaValue = RedisUtils.get(Constants.CAPTCHA_REDIS_KEY_PREFIX + uuid);
                if (StringUtils.isBlank(redisCaptchaValue) || !redisCaptchaValue.equals(captcha)) {
                    return ResponseBean.error("图形验证码有误请重新输入");
                }
            } else{
                String redisCaptchaValue = RedisUtils.get(Constants.CAPTCHA_REDIS_KEY_PREFIX + captcha);
                if (StringUtils.isBlank(redisCaptchaValue)) {
                    return ResponseBean.error("图形验证码有误请重新输入");
                }
            }
            //根据短信模板添加参数
            List<String> params = new ArrayList<>();
            ResponseBean checkRes = checkMobileSmsCode(mobileNo);
            if (checkRes.getCode() != 200) {
                return checkRes;
            }
            return smsService.sendSmsValidateCode(mobileNo, Constants.TEAM_ID_999, smsTemplate, params.toArray());

        } catch (Exception e) {
            log.error("发送短信验证码异常{}", e);
            return ResponseBean.error("发送短信验证码失败，请稍候再试");
        }
    }

    @ApiOperation(value = "发送短信验证码H5", notes = "- 参数:\n  - mobileNo: 手机号,必传\n  - captcha: 图形验证码,必传 ")
    @GetMapping("/smsCodeH5/{mobileNo}/{captcha}")
    @OldSwaggerDeveloped
    @LoginValid(needLogin = false)
    @SignValidate(needSign = false)
    public String smsCodeH5(@PathVariable String mobileNo, @PathVariable String captcha) {
        try {
            if (StringUtils.isBlank(mobileNo)) {
                return "smscallback" + "(" + GsonUtils.toJson(ResponseBean.error("手机号不能为空")) + ")";
            }
            //获取短信模板
            SmsTemplate smsTemplate = SmsTemplate.getTemplateByCode("REGIST_SDB");
            if (null == smsTemplate) {
                return "smscallback" + "(" + GsonUtils.toJson(ResponseBean.error("非法操作")) + ")";
            }
            //校验图形验证码
            if (StringUtils.isBlank(captcha)) {
                return "smscallback" + "(" + GsonUtils.toJson(ResponseBean.error("请先输入图形验证码")) + ")";
            }
            String redisCaptchaValue = RedisUtils.get(Constants.CAPTCHA_REDIS_KEY_PREFIX + captcha);
            if (StringUtils.isBlank(redisCaptchaValue)) {
                return "smscallback" + "(" + GsonUtils.toJson(ResponseBean.error("图形验证码有误请重新输入")) + ")";
            }
            //根据短信模板添加参数
            List<String> params = new ArrayList<>();
            if (smsTemplate.isNeedParams()) {

            }
            ResponseBean checkRes = checkMobileSmsCode(mobileNo);
            if (checkRes.getCode() != 200) {
                return "smscallback" + "(" + GsonUtils.toJson(checkRes) + ")";
            }
            return "smscallback" + "(" + GsonUtils.toJson(
                    smsService.sendSmsValidateCode(mobileNo, Constants.TEAM_ID_999, smsTemplate, params.toArray())) + ")";

        } catch (Exception e) {
            log.error("发送短信验证码异常{}", e);
            return "smscallback" + "(" + GsonUtils.toJson(ResponseBean.error("发送短信验证码失败，请稍候再试")) + ")";
        }
    }

    @ApiOperation(value = "设置-安全手机操作（包含：设置安全手机、修改安全手机第一步、修改安全手机第二步）", notes = SettingSwaggerNotes.OPERATE_SAFE_PHONE)
    @PostMapping("/operateSafePhone")
    @OldSwaggerDeveloped
    public ResponseBean operateSafePhone(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                         @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String loginAgentNo = userInfoBean.getAgentNo();
            String loginUserId = userInfoBean.getUserId();

            AgentInfo dbAgentInfo = agentInfoService.queryAgentInfo(loginAgentNo);
            if (null == dbAgentInfo) {
                return ResponseBean.error("代理商信息不存在");
            }
            String dbSafePhone = dbAgentInfo.getSafephone();
            String dbRegistMobileNo = dbAgentInfo.getMobilephone();

            String mobileNo = bodyParams.get("mobileNo");
            String verifyCode = bodyParams.get("verifyCode");
            String operateType = bodyParams.get("operateType");
            if (StringUtils.isBlank(mobileNo, verifyCode, operateType)) {
                return ResponseBean.error("必要参数不能有空");
            }
            boolean correct = true;
            int count = 0;
            switch (operateType) {
                case "SET": {
                    //判断当前是否已有安全手机
                    if (StringUtils.isNotBlank(dbSafePhone)) {
                        return ResponseBean.error("当前账号已设置安全手机");
                    }
                    correct = smsService.validateSmsCode(mobileNo, Constants.TEAM_ID_999, verifyCode);
                    if (!correct) {
                        return ResponseBean.error("短信验证码有误");
                    }
                    count = agentInfoService.updateSafePhone(loginAgentNo, mobileNo);
                    if (count < 1) {
                        return ResponseBean.error("设置安全手机失败，请稍候再试");
                    }
                    //删除公共数据缓存
                    try {
                        RedisUtils.del(String.format("agentApi2:publicData:%s:%s", loginAgentNo, loginUserId));
                        log.info("......删除公共数据接口缓存成功");
                    } catch (Exception e) {
                        log.info("......删除公共数据接口缓存异常：{}", e);
                    }
                    //安全手机设置成功后向注册手机号码发送短信通知
                    smsService.sendSmsValidateCode(dbRegistMobileNo, Constants.TEAM_ID_999, SmsTemplate.COMPLETE_SAFE_PHONE, null);
                    break;
                }
                case "UPDATE_FIRST": {
                    if (StringUtils.isBlank(dbSafePhone)) {
                        return ResponseBean.error("当前账号安全手机为空，请先设置再修改");
                    }
                    //判断原手机号是否跟数据库安全手机一致
                    if (!mobileNo.equals(dbSafePhone)) {
                        return ResponseBean.error("原安全手机号码不正确");
                    }
                    correct = smsService.validateSmsCode(mobileNo, Constants.TEAM_ID_999, verifyCode);
                    if (!correct) {
                        return ResponseBean.error("短信验证码有误");
                    }
                    break;
                }
                case "UPDATE_SECOND": {
                    if (StringUtils.isBlank(dbSafePhone)) {
                        return ResponseBean.error("当前账号安全手机为空，请先设置再修改");
                    }
                    correct = smsService.validateSmsCode(mobileNo, Constants.TEAM_ID_999, verifyCode);
                    if (!correct) {
                        return ResponseBean.error("短信验证码有误");
                    }
                    count = agentInfoService.updateSafePhone(loginAgentNo, mobileNo);
                    if (count < 1) {
                        return ResponseBean.error("修改安全手机失败，请稍候再试");
                    }
                    //删除公共数据缓存
                    try {
                        RedisUtils.del(String.format("agentApi2:publicData:%s:%s", loginAgentNo, loginUserId));
                        log.info("......删除公共数据接口缓存成功");
                    } catch (Exception e) {
                        log.info("......删除公共数据接口缓存异常：{}", e);
                    }
                    //安全手机设置成功后向注册手机号码发送短信通知
                    smsService.sendSmsValidateCode(dbRegistMobileNo, Constants.TEAM_ID_999, SmsTemplate.COMPLETE_SAFE_PHONE, null);
                    break;
                }
                default: {
                    return ResponseBean.error("操作不合法");
                }
            }
            return ResponseBean.success();

        } catch (Exception e) {
            log.error("当前登录代理商{}设置-安全手机操作异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("安全手机操作失败，请稍候再试");
        }
    }

    @ApiOperation(value = "设置-资金密码（包含：设置和修改）", notes = SettingSwaggerNotes.OPERATE_SAFE_PASSWORD)
    @PostMapping("/operateSafePassword")
    @OldSwaggerDeveloped
    public ResponseBean operateSafePassword(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                            @RequestBody(required = false) Map<String, String> bodyParams) {

        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String loginAgentNo = userInfoBean.getAgentNo();
            String loginUserId = userInfoBean.getUserId();

            AgentInfo dbAgentInfo = agentInfoService.queryAgentInfo(loginAgentNo);
            if (null == dbAgentInfo) {
                return ResponseBean.error("代理商信息不存在");
            }
            String dbSafePassword = dbAgentInfo.getSafePassword();
            String dbSafePhone = dbAgentInfo.getSafephone();

            String operateType = bodyParams.get("operateType");
            String encryNewSafePassword = bodyParams.get("newSafePassword");
            if (StringUtils.isBlank(operateType, encryNewSafePassword)) {
                return ResponseBean.error("必要参数不能有空");
            }
            //密码解密
            String newSafePassword = RSAUtils.decryptDataOnJava(encryNewSafePassword, Constants.LOGIN_PRIVATE_KEY);
            if (StringUtils.isBlank(newSafePassword)) {
                throw new AppException("资金密码输入有误");
            }
            boolean correct = true;
            int count = 0;
            switch (operateType) {
                case "SET": {
                    //判断当前是否已有资金密码
                    if (StringUtils.isNotBlank(dbSafePassword)) {
                        return ResponseBean.error("当前账号已设置资金密码");
                    }
                    //如果是一连串的设置，设置好安全手机并校验短信验证码后就不需要再校验验证码了
                    String confirmed = bodyParams.get("confirmed");
                    if (!"confirmed".equalsIgnoreCase(confirmed)) {
                        String mobileNo = bodyParams.get("mobileNo");
                        String verifyCode = bodyParams.get("verifyCode");
                        if (StringUtils.isBlank(mobileNo, verifyCode)) {
                            return ResponseBean.error("必要参数不能有空");
                        }
                        //判断手机号是否正确
                        if (!mobileNo.equals(dbSafePhone)) {
                            return ResponseBean.error("安全手机号有误");
                        }
                        correct = smsService.validateSmsCode(mobileNo, Constants.TEAM_ID_999, verifyCode);
                        if (!correct) {
                            return ResponseBean.error("短信验证码有误");
                        }
                    }
                    count = agentInfoService.updateSafePassword(loginAgentNo, newSafePassword);
                    if (count < 1) {
                        return ResponseBean.error("设置资金密码失败，请稍候再试");
                    }
                    //删除公共数据缓存
                    try {
                        RedisUtils.del(String.format("agentApi2:publicData:%s:%s", loginAgentNo, loginUserId));
                        log.info("......删除公共数据接口缓存成功");
                    } catch (Exception e) {
                        log.info("......删除公共数据接口缓存异常：{}", e);
                    }
                    break;
                }
                case "UPDATE": {
                    if (StringUtils.isBlank(dbSafePassword)) {
                        return ResponseBean.error("当前账号资金密码为空，请先设置再修改");
                    }
                    String encryOldSafePassword = bodyParams.get("oldSafePassword");
                    String oldSafePassword = RSAUtils.decryptDataOnJava(encryOldSafePassword, Constants.LOGIN_PRIVATE_KEY);
                    String oldMd5SafePassword = Md5.md5Str(oldSafePassword + "{" + loginAgentNo + "}");
                    //判断原资金密码是否正确
                    if (StringUtils.isBlank(oldMd5SafePassword) || !oldMd5SafePassword.equals(dbSafePassword)) {
                        return ResponseBean.error("原资金密码有误");
                    }
                    if (newSafePassword.equals(oldSafePassword)) {
                        return ResponseBean.error("新资金密码不能跟原资金密码一致");
                    }
                    count = agentInfoService.updateSafePassword(loginAgentNo, newSafePassword);
                    if (count < 1) {
                        return ResponseBean.error("修改资金密码失败，请稍候再试");
                    }
                    break;
                }
                default: {
                    return ResponseBean.error("操作不合法");
                }
            }
            return ResponseBean.success();

        } catch (Exception e) {
            log.error("当前登录代理商{}设置-资金密码异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("资金密码操作失败，请稍候再试");
        }
    }

    /**
     * 手机号发送短信规则校验
     *
     * @param mobileNo
     * @return
     */
    public ResponseBean checkMobileSmsCode(String mobileNo) {
        //一天发送次数
        String dayStr = DateUtil.format(new Date(), "yyyy-MM-dd");
        String dayMaxSmsCodeCountStr = WebUtils.getSysConfigValueByKey("DAY_MAX_SMS_CODE_COUNT");
        int dayMaxSmsCodeCount = StringUtils.isBlank(dayMaxSmsCodeCountStr) ? 10 : Integer.parseInt(dayMaxSmsCodeCountStr);
        long dayCount = RedisUtils.incr(mobileNo + dayStr, 1);
        if (dayCount > dayMaxSmsCodeCount) {
            log.info("手机号{}{}一天内手机号短信验证码发送次数已超过{}次", mobileNo, dayStr, dayMaxSmsCodeCount);
            return ResponseBean.error("当前手机号已超过一天验证码短信发送次数");
        }
        RedisUtils.expire(mobileNo + dayStr, 60 * 60 * 24);
        //一分钟发送次数
        String minuteStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm");
        String minuteMaxSmsCodeCountStr = WebUtils.getSysConfigValueByKey("MINUTE_MAX_SMS_CODE_COUNT");
        int minuteMaxSmsCodeCount = StringUtils.isBlank(minuteMaxSmsCodeCountStr) ? 1 : Integer.parseInt(dayMaxSmsCodeCountStr);
        long minuteCount = RedisUtils.incr(mobileNo + minuteStr, 1);
        if (minuteCount > minuteMaxSmsCodeCount) {
            log.info("手机号{}{}一分钟内手机号短信验证码发送次数已超过{}次", mobileNo, minuteStr, minuteMaxSmsCodeCount);
            return ResponseBean.error("当前手机号已超过一分钟验证码短信发送次数");
        }
        RedisUtils.expire(mobileNo + minuteStr, 60);
        return ResponseBean.success();
    }

    @ApiOperation(value = "忘记资金密码", notes = SettingSwaggerNotes.FORGET_MONEY_PWD)
    @PostMapping("/forgetSafePassword")
    @OldSwaggerDeveloped
    public ResponseBean forgetSafePassword(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                           @RequestBody(required = false) Map<String, String> bodyParams) {
        bodyParams = Optional.ofNullable(bodyParams).orElse(new HashMap<>());
        try {
            String loginAgentNo = userInfoBean.getAgentNo();
            String loginUserId = userInfoBean.getUserId();

            AgentInfo dbAgentInfo = agentInfoService.queryAgentInfo(loginAgentNo);
            if (null == dbAgentInfo) {
                return ResponseBean.error("代理商信息不存在");
            }
            String dbSafePassword = dbAgentInfo.getSafePassword();
            String dbSafePhone = dbAgentInfo.getSafephone();

            String encryNewSafePassword = bodyParams.get("newSafePassword");
            if (StringUtils.isBlank(encryNewSafePassword)) {
                return ResponseBean.error("必要参数不能有空");
            }

            //密码解密
            String newSafePassword = RSAUtils.decryptDataOnJava(encryNewSafePassword, Constants.LOGIN_PRIVATE_KEY);
            if (StringUtils.isBlank(newSafePassword)) {
                throw new AppException("资金密码输入有误");
            }

            String mobileNo = bodyParams.get("mobileNo");
            String verifyCode = bodyParams.get("verifyCode");

            //判断手机号是否正确
            if (!mobileNo.equals(dbSafePhone)) {
                return ResponseBean.error("安全手机号有误");
            }
            boolean correct = smsService.validateSmsCode(mobileNo, Constants.TEAM_ID_999, verifyCode);
            if (!correct) {
                return ResponseBean.error("短信验证码有误");
            }
            int count = agentInfoService.updateSafePassword(loginAgentNo, newSafePassword);
            if (count < 1) {
                return ResponseBean.error("设置资金密码失败，请稍候再试");
            }
            //删除公共数据缓存
            RedisUtils.del(String.format("agentApi2:publicData:%s:%s", loginAgentNo, loginUserId));
            log.info("......删除公共数据接口缓存成功");
            return ResponseBean.success("修改资金密码成功");

        } catch (Exception e) {
            log.error("当前登录代理商{}设置-资金密码异常{}", userInfoBean.getAgentNode(), e);
            return ResponseBean.error("资金密码操作失败，请稍候再试");
        }
    }
}
