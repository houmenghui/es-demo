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
public class MerchantSearchBean {

    private String agentNo;                 // 查询代理商编号
    private String agentNode;               // 查询代理商编号
    private String theOpenBpId;             // 开通业务产品
    private String teamId;                  // 所属组织
    private String teamEntryId;             // 所属子组织
    private String mobilePhone;             // 手机号
    private String startTransTime;          // 交易开始时间
    private String endTransTime;            // 交易结束时间
    private String minTransMoney;           // 最小交易金额
    private String maxTransMoney;           // 最大交易金额
    private String minOrderNum;             // 最小交易笔数
    private String maxOrderNum;             // 最大交易笔数
    private String province;                // 省份
    private String city;                    // 城市
    private String district;                // 区域
    private String startCreateTime;         // 商户开始创建时间
    private String endCreateTime;           // 商户结束创建时间
    private String merchantStatus;          // 商户状态
    private String theUnOpenBpId;           // 未开通的业务产品
    private QueryScope queryScope;          // 商户类型
    private SearchType searchType;          // 查询类型
    private SortType sortType;              // 排序类型
    private String warningId;               // 预警id
    private String hlfActive;               // 激活状态
    private String merchantName;            // 商户名称
    private String merchantNo;              // 商户编号
    private String recommendedSource;       // 推广来源
    private String riskStatus;              // 冻结状态
    private String specialMerFlag;           //是否特约商户（一户一码的特约商户，对应acq_merchant表中是否存在相应记录，对应ES的merchant类型中是否存在acq_merchant_no字段）
    private String searchKeywork;         // 商户名(模糊), 商户号(精确), 手机号(精确)

    public enum SearchType {
        QUERY,          // 查询           根据条件查询商户
        ALL,            // 全部           全部商户
        QUALITY,        // 优质商户       本月交易金额>=x元
        ACTIVE,         // 活跃商户       近x天交易笔数>=x笔且交易金额>=x元
        UNCERTIFIED,    // 未认证商户     身份未认证的商户
        SLEEP           // 休眠商户       入网>=x天且连续>x天无交易
    }

    public enum SortType {
        DEFAULT_ORDER,          // 默认排序: 按照商户的创建时间倒序排
        CUR_MONTH_TRANS_ASC,    // 本月交易量从低到高
        CUR_MONTH_TRANS_DESC,   // 本月交易量从高到底
        LAST_MONTH_TRANS_ASC,   // 上个月交易量从低到高
        LAST_MONTH_TRANS_DESC,  // 上个月交易量从高到低
        ALL_TRANS_ASC,          // 累积交易量从低到高
        ALL_TRANS_DESC          // 累积交易量从高到低
    }
}
