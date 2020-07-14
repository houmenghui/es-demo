package com.esdemo.modules.service;

import java.util.Map;

/**
 * Created by Administrator on 2018/1/12/012.
 *
 */
public interface OpenPlatformService {
    /**
     * 鉴权-支持二三四要素
     * @param name 开户名
     * @param idCard 身份证号码
     * @param bankCode 银行卡号
     * @param phoneNum 银行预留手机号
     * @return
     * @author ZengJA
     * @date 2017-11-16 16:34:55
     */
    Map<String, String> doAuthen(String bankCode, String name, String idCard, String phoneNum);
}
