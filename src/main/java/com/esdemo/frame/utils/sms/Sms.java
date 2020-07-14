package com.esdemo.frame.utils.sms;

import com.esdemo.frame.utils.HttpUtils;
import com.esdemo.frame.utils.md5.Md5;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送短信接口
 */
@Slf4j
public class Sms {

    private final static String UTF_8 = "UTF-8";
    private final static String key = "ruewoo1398543p";
    private final static String url = "http://msg.yfbpay.cn/msgplatform/sms/sendSms";

    /**
     * 短信
     *
     * @param mobileNo
     * @param context
     * @return
     */
    public static void sendMsg(String mobileNo, String context) {
        try {
            log.info("发送短信:手机号:{},内容:{}", mobileNo, context);
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("mobile", mobileNo);
            paramsMap.put("context", context);
            paramsMap.put("platform", "core2");
            paramsMap.put("mac", Md5.md5Str(("mobile" + "&&" + mobileNo + "platform" + "&&" + "core2") + key));
            //设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", "application/x-www-form-urlencoded; charset=" + UTF_8);
            HttpUtils.doPost(url, headers, paramsMap, false);

        } catch (Exception e) {
            log.info("发送短信:手机号:{},内容:{}，异常{}", mobileNo, context, e);
            log.error("异常{}", e);
        }
    }


    public static void main(String[] args) {
        Sms.sendMsg("18603049008", "感谢您注册超级刷，本次注册验证码为:9999 。【支付随心】");
    }
}
