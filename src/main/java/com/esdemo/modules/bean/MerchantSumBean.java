package com.esdemo.modules.bean;

import lombok.Data;

import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-14 09:40
 */
@Data
public class MerchantSumBean {
    private String typeId;              // 类别id, 业务产品id 或者 代理商编号 或者 组织id
    private String agentNode;           // 代理商节点(目前只有代理商汇总才有)
    private String typeName;            // 类别名称 业务产品名称 or 代理商名称 或 组织名称
//    private boolean teamEntry;        // 是否为子组织
    private long total;                 // 总数
    private long activeNumber;          // 激活数量
    private long notActiveNumber;       // 未激活数量
    private String rate;
    private List<MerchantSumBean> children;
}
