package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 14:06
 */
@Data
public class ServiceInfoBean {
    private String serviceId;
    private String serviceType;
    private String fixedRate;
    private String fixedQuota;
}
