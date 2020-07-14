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
public enum PublicCode {

    ALL("0"),
    YES("1"),
    NOT("2");

    private String isAddPublic;
}