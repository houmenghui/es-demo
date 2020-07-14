package com.esdemo.modules.bean;

import com.esdemo.frame.enums.QueryScope;
import lombok.Data;

/**
 * 商户查询条件
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-20 10:17
 */
@Data
public class MerchantSummarySearchBean {

    private QueryScope queryScope;          // 商户类型
    private String agentNo;
    private String agentNode;

}
