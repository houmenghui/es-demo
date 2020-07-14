package com.esdemo.modules.service;

import com.esdemo.frame.enums.QueryScope;
import com.esdemo.modules.bean.MerchantBpBean;

import java.util.List;
import java.util.Map;

/**
 * @author zhangly
 * @date 2020/4/6 11:40
 */
public interface AcqMerchantService {
    /**
     * 根据商户号，收单商户号（一户一码特约商户号）查询收单商户记录
     *
     * @param merchantNo    商户号
     * @param acqMerchantNo 收单商户号（一户一码特约商户号）
     * @return
     */
    Map<String, Object> queryAcqMerByGeneralMerNo(String merchantNo, String acqMerchantNo);

    /**
     * 根据普通商户号，查询最近的收单商户进件记录
     *
     * @param merchantNo 商户号
     * @return
     */
    Map<String, Object> queryLatestAcqMerchantInfo(String merchantNo);

    /**
     * 获取商户进件要求项值
     *
     * @param merchantNo
     * @param mriId
     * @return
     */
    String getMerItemByNoAndMriId(String merchantNo, String mriId);

    /**
     * 获取商户所有业务产品   及   业务产品组以下的业务产品
     *
     * @param merchantNo 商户号
     * @param ownAgentNo 所属代理商编号
     * @return
     */
    List<MerchantBpBean> listMerBpInfoWithGroup(String merchantNo, String ownAgentNo);

    /**
     * 商户结算卡
     *
     * @param accountNo
     * @return
     */
    Map<String, Object> querySettleAccountNo(String accountNo);

    /**
     * 获取商户结算卡信息
     *
     * @param merchantNo
     * @param accountNo
     * @return
     */
    Map<String, Object> findMerAccountNo(String merchantNo, String accountNo);

    /**
     * 统计机具数量
     * @param agentNode
     * @param queryScope
     * @param isActive
     * @return
     */
    long censusTerminalCount(String agentNode, QueryScope queryScope, boolean isActive);
}
