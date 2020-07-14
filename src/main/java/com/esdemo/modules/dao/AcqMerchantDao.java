package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhangly
 * @date 2020-04-06 11:26
 */
@Mapper
public interface AcqMerchantDao {

    /**
     * 根据商户号，收单商户号（一户一码特约商户号）查询收单商户记录
     *
     * @param merchantNo    商户号
     * @param acqMerchantNo 收单商户号（一户一码特约商户号）
     * @return
     */
    Map<String, Object> queryAcqMerByGeneralMerNo(@Param("merchantNo") String merchantNo, @Param("acqMerchantNo") String acqMerchantNo);

    /**
     * 根据普通商户号，查询最近的收单商户进件记录
     *
     * @param merchantNo 商户号
     * @return
     */
    Map<String, Object> queryLatestAcqMerchantInfo(@Param("merchantNo") String merchantNo);

    /**
     * 获取商户进件要求项值
     *
     * @param merchantNo
     * @param mriId
     * @return
     */
    String getMerItemByNoAndMriId(@Param("merchantNo") String merchantNo, @Param("mriId") String mriId);

    /**
     * 获取商户所有业务产品
     *
     * @param merchantNo
     * @return
     */
    List<Map<String, Object>> getMbpList(@Param("merchantNo") String merchantNo);

    Map<String, Object> findMerAccountNo(@Param("merchantNo") String merchantNo, @Param("accountNo") String accountNo);

    void deleteAcqMerFile(@Param("fileType") String fileType, @Param("acqIntoNo") String acqIntoNo);

    long censusTerminalCount(@Param("agentNode") String agentNode, @Param("queryScopeCode") String queryScopeCode, @Param("isActive") boolean isActive);
}
