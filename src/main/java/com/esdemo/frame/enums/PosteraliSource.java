package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：获取海报图片请求页面来源
 * @Author：zhangly
 * @Date：2020/03/20
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum PosteraliSource {

    ACT_CODE_DETAIL("0"),//激活码详情页面
    SUMMARY_PARENT_CODE("1");//母码汇总页面

    private String source;
}