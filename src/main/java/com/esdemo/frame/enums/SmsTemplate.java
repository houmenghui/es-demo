package com.esdemo.frame.enums;

import com.esdemo.frame.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：短信验证码模板
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum SmsTemplate {

    SEEK_LOGIN_PWD("SEEK_LOGIN_PWD", "验证码：xxxxxx。您正在找回登录密码，验证码5分钟内有效", true, true, false),
    REGIST_SDB("REGIST_SDB", "您的验证码是：xxxxxx，有效期为3分钟。若非本人操作，请忽略此条短信。", true, true, false),
    SET_SAFE_PHONE("SET_SAFE_PHONE", "验证码：xxxxxx。您正在设置安全手机号码，验证码5分钟内有效，如非本人操作请及时登录后台查看", false, true, false),
    SET_SAFE_PASSWORD("SET_SAFE_PASSWORD", "验证码：xxxxxx。您正在设置资金密码，验证码5分钟内有效，如非本人操作请及时登录后台查看", false, true, false),
    COMPLETE_SAFE_PHONE("COMPLETE_SAFE_PHONE", "安全手机号码设置成功，该手机号码可用于修改提现密码等重要操作，如非本人操作请及时登录后台查看。", true, false, false);

    //模板编码
    private String code;
    //模板内容
    private String context;
    //是否需要校验手机号合法性
    private boolean isCheckMobileNo;
    //是否需要生成6为随机数字验证码
    private boolean needVerifyCode;
    //模板内容是否有占位符，需要传递参数
    private boolean needParams;

    public static SmsTemplate getTemplateByCode(String code) {
        if (StringUtils.isNotBlank(code)) {
            SmsTemplate[] values = SmsTemplate.values();
            for (SmsTemplate value : values) {
                if (code.equals(value.getCode())) {
                    return value;
                }
            }
        }
        return null;
    }
}