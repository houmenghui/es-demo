package com.esdemo.modules.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @create 2020-04-22 16:03
 */
@Data
public class PublicDataVo implements Serializable {
    private static final long serialVersionUID = 1612498188847305829L;
    private boolean showHotLinePhone;
    // 热线电话
    private String hotLinePhone;
    // 热线电话提示文本
    private String hotLinePhoneTip;

    //代办代理商设置总条数
    private Long toBeSetListSize;

    //代理商解绑权限开关
    private boolean funCode030;

    //代理商进件功能入口 开关
    private boolean funCode044;
}
