package com.esdemo.modules.bean;

import lombok.Data;

/**
 * 激活码查询对象封装
 */
@Data
public class ActCodeQueryBean {
    private String queryType;//查询类型，对应ActCodeQueryType枚举，0：全部、1：未使用
    private String queryRange;//查询范围，在queryType为0时，此条件可选，对应ActCodeQueryRange枚举，0：全部、1：我的
    private String beginId;//起始激活码编号
    private String endId;//终止激活码编号
    private String actCodeStatus;//激活码状态，对应ActCodeStatus枚举
    private String isAddPublic;//是否添加通用码，对应PublicCode枚举
    private String agentNo;//代理商编号
    private String agentNode;//代理商节点
    private String nfcOrigCode;//通用码
    private String merchantNo;//商户编号

    private long[] idArray;//操作数组（下发. 回收, 分配通用码 撤回通用码）
    private String[] repayMerNos;//超级还查询商户信息
}
