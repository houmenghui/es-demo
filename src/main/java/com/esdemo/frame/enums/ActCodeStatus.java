package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：激活码状态
 * @Author：zhangly
 * @Date：2020/03/20
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum ActCodeStatus {

    STORAGE("0"),
    ALLOCATED("1"),
    ACTIVATED("2");

    private String status;
}