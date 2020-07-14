package com.esdemo.modules.service;

import com.esdemo.frame.bean.AppDeviceInfo;

import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：APP信息服务类
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface AppInfoService {

    /**
     * 校验APP客户端版本信息
     *
     * @param appDeviceInfo
     * @return
     */
    Map<String, Object> checkAppVersion(AppDeviceInfo appDeviceInfo);
}
