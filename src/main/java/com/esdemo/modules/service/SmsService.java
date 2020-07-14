package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.SmsTemplate;

/**
 * @Title：agentApi2
 * @Description：短信服务接口
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface SmsService {


    /**
     * 发送短信验证码
     *
     * @param mobileNo
     * @param teamId
     * @return
     */
    ResponseBean sendSmsValidateCode(String mobileNo, String teamId, SmsTemplate template, Object... params);

    /**
     * 校验短信验证码
     *
     * @param mobileNo
     * @param teamId
     * @param verifyCode
     * @return
     */
    boolean validateSmsCode(String mobileNo, String teamId, String verifyCode);

}
