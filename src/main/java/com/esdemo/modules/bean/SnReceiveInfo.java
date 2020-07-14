package com.esdemo.modules.bean;

import lombok.Data;

/**
 * @author lmc
 * 机具回收操作实体
 * @date 2019/6/3 16:52
 */
@Data
public class SnReceiveInfo {
    //代理商编号
    private String agentNo;
    //成功数量
    private int successCount;
    //机具集合
    private String snStr;
}
