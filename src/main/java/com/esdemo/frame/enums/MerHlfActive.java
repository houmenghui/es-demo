package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：商户欢乐返激活状态
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum MerHlfActive {

    ACTIVE("1"),
    UN_ACTIVE("0");

    private String status;
}