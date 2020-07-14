package com.esdemo.frame.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-10 11:36
 */
@AllArgsConstructor
@Getter
public enum ResponseType {

    SUCCESS(ResponseBean.success(null,"成功")),
    SERVICE_ERROR(ResponseBean.error(500, "请稍后重试")),
    NOT_FOUND(ResponseBean.error(404, "无效请求")),
    NOT_LOGIN(ResponseBean.error(401, "没有登陆")),
    SIGN_FAIL(ResponseBean.error(403, "签名失败")),
    ERROR(ResponseBean.error(400, "其他异常"));

    private ResponseBean responseBean;
}
