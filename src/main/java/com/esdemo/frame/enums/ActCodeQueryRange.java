package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：激活码查询范围
 * @Author：zhangly
 * @Date：2020/03/20
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum ActCodeQueryRange {

    ALL("0"), //全部
    MY("1"); //我的

    private String range;
}