package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-31 14:38
 */
@Data
public class MerchantWarningBean {
    private String id;      // 自增id
    private String warningType;    // 预警类型
    private String warningName;         // 服务名称
    private String teamId;              // 组织id
    private String isUsed;              // 是否使用 0 否 1 是
    private String warningImg;          // 显示图标
    private String warningTitle;        // 显示标题
    private String warningUrl;          // 跳转连接
    private long noTranDay;             // 无交易天数
    private BigDecimal tranSlideRate;   // 交易下滑百分比
    private String createTime;
    private String remark;
    private long waringCount;

}

