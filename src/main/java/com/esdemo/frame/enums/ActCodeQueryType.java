package com.esdemo.frame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title：agentApi2
 * @Description：激活码查询类型
 * @Author：zhangly
 * @Date：2020/03/20
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
public enum ActCodeQueryType {

    ALL("0"), //全部
    UN_USE("1"); //未使用

    private String type;
}