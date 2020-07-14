package com.esdemo.frame.bean;

import lombok.Data;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-09 09:08
 */
@Data
public class AppDeviceInfo {
    private String appName;          // appName 盛钱包, 点付
    private String name;             // Mi 5s plus
    private String systemName;       // android / ios
    private String systemVersion;    // 8.0.0
    private String deviceId;         // 设备id
    private String appNo;            // app类型
    private String appVersion;       // app版本
    private String appBuild;         // app构建版本
    private String appChannel;       // app渠道
    private String loginToken;       // 登陆token
    private String timestamp;        // 当前时间戳
    private String sign;             // 签名
    private String jpushDevice;      // 极光推送设备id

}
