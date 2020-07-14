package com.esdemo.modules.service.impl;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.SmsTemplate;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.sms.Sms;
import com.esdemo.modules.dao.SmsDao;
import com.esdemo.modules.dao.UserDao;
import com.esdemo.modules.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;

/**
 * @Title：agentApi2
 * @Description：短信服务
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Resource
    private SmsDao smsDao;
    @Resource
    private UserDao userDao;


    /**
     * 发送短信验证码
     *
     * @param mobileNo
     * @param teamId
     * @param template
     * @return 是否发送成功
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public ResponseBean sendSmsValidateCode(String mobileNo, String teamId, SmsTemplate template, Object... params) {
        log.info("发送短信验证码参数，mobileNo：{}，teamId：{}，template：{}", mobileNo, teamId, template);
        try {
            if (StringUtils.isBlank(mobileNo, teamId) || null == template) {
                return ResponseBean.error("必要参数不能有空");
            }
            String smsContext = template.getContext();
            boolean checkMobileNo = template.isCheckMobileNo();
            boolean needVerifyCode = template.isNeedVerifyCode();
            boolean needParams = template.isNeedParams();
            //是否需要校验手机号合法性,跟侯总确认,去掉这个校验
            /*if (checkMobileNo) {
                UserInfoBean dbUserInfo = userDao.getUserInfoByMobileNoAndTeam(mobileNo, teamId);
                if (dbUserInfo == null) {
                    return ResponseBean.error("此手机号未注册");
                }
            }*/
            //是否需要参数
            if (needParams && null != params) {
                smsContext = String.format(smsContext, params);
            }
            //是否需要验证码
            if (needVerifyCode) {
                double pross = (1 + new Random().nextDouble()) * Math.pow(10, 6);
                String fixLenthString = String.valueOf(pross);
                String verifyCode = fixLenthString.substring(1, 6 + 1);
                smsContext = smsContext.replaceAll("xxxxxx", verifyCode);
                int count = smsDao.insertSmsCode(mobileNo, verifyCode, teamId);
                if (count < 1) {
                    log.info("插入短信验证码记录失败");
                    return ResponseBean.error("短信验证码发送失败");
                }
            }
            Sms.sendMsg(mobileNo, smsContext);
            return ResponseBean.success("短信验证码已发送");

        } catch (Exception e) {
            log.error("异常{}", e);
            log.info("发送短信验证码参数，mobileNo：{}，teamId：{}，template：{}，异常{}", mobileNo, teamId, template, e);
            return ResponseBean.error("短信验证码发送失败");
        }
    }

    /**
     * 校验短信验证码
     *
     * @param mobileNo
     * @param teamId
     * @param verifyCode
     * @return
     */
    @Override
    public boolean validateSmsCode(String mobileNo, String teamId, String verifyCode) {
        log.info("校验短信验证码是否正确参数，mobileNo：{}，teamId：{}， verifyCode：{}", mobileNo, teamId, verifyCode);
        if (StringUtils.isBlank(mobileNo, teamId, verifyCode)) {
            return false;
        }
        Map<String, Object> newlySmsMap = smsDao.getLatest5MinuteSmsCode(mobileNo, teamId);
        if (CollectionUtils.isEmpty(newlySmsMap)) {
            return false;
        }
        log.info("数据库短信验证码记录：{}", JSONUtil.toJsonStr(newlySmsMap));
        String dbVerifyCode = StringUtils.filterNull(newlySmsMap.get("valid_code"));

        return verifyCode.equals(dbVerifyCode);
    }
}
