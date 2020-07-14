package com.esdemo.modules.dao;

import com.esdemo.modules.bean.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-16 14:17
 */
@Mapper
public interface MerchantDao {
    /**
     * 根据teamId获取team的名字
     */
    String getTeamNameByTeamId(@Param("teamId") String teamId);

    /**
     * 根据主组织获取所有子组织
     */
    List<Map<String, Object>> getEntryTeamByTeamId(@Param("teamId") String teamId);

    /**
     * 根据业务产品id获取业务产品名字
     *
     * @param bpIds 业务产品id
     * @return
     */
    List<Map<String, Object>> listBpNameByBpIds(@Param("bpIds") List<String> bpIds);

    /**
     * 获取代理商开通的业务产品
     *
     * @param agentNo 代理商编号
     */
    List<Map<String, Object>> listBusinessProductByAgentNo(@Param("agentNo") String agentNo);

    /**
     * 根据商户号获取硬件产品
     *
     * @param merchantNo 商户编号
     * @param agentNode  代理商节点
     */
    List<KeyValueBean> listHardwareByMerchantNo(@Param("merchantNo") String merchantNo,
                                                @Param("agentNode") String agentNode);

    /**
     * 根据商户号获取商户基本信息
     *
     * @param merchantNo 商户编号
     */
    MerchantInfo queryMerchantInfoByNo(@Param("merchantNo") String merchantNo);

    /**
     * 查找可以被替换的业务产品信息(可更换业务产品的进件)
     *
     * @param merchantNo
     * @return
     */
    List<MerchantBpBean> listCanReplaceBp(@Param("merchantNo") String merchantNo);

    /**
     * 获取同组的其他业务信息
     *
     * @param bpId 业务产品id
     */
    List<MerchantBpBean> listOtherBpInTheSameGroup(@Param("bpId") String bpId, @Param("agentNo") String agentNo);

    /**
     * 获取同组的业务信息
     *
     * @param bpId 业务产品id
     */
    List<MerchantBpBean> listOtherAndUnUseBpInTheSameGroup(@Param("bpId") String bpId, @Param("agentNo") String agentNo, @Param("merchantNo") String merchantNo);

    /**
     * 统计商户业务产品进件数量
     *
     * @param merchantNo 商户编号
     * @param bpId       业务产品id
     */
    int countMerchantBpInfo(@Param("merchantNo") String merchantNo,
                            @Param("bpId") String bpId);

    int countTerminalBpInfo(@Param("merchantNo") String merchantNo,
                            @Param("bpId") String oldBpId);

    int updateMerchantTerminal(
            @Param("merchantNo") String merchantNo,
            @Param("oldBpId") String oldBpId,
            @Param("newBpId") String newBpId);

    int updateMerchantBusinessProduct(@Param("merchantNo") String merchantNo,
                                      @Param("oldBpId") String oldBpId,
                                      @Param("newBpId") String newBpId);

    int insertMerBusProHis(@Param("mbpHis") MerchantBusinessProductHistory mbpHis);

    void delectMerBusItem(@Param("merchantNo") String merchantNo);

    List<ServiceInfoBean> listServiceInfoByBpId(@Param("bpId") String oldBpId);

    void deleteMerRate(@Param("bpId") String bpId, @Param("merchantNo") String merchantNo);

    void deleteMerQuota(@Param("bpId") String oldBpId, @Param("merchantNo") String merchantNo);

    int updateMerchantService(@Param("merchantNo") String merchantNo,
                              @Param("oldBpId") String oldBpId,
                              @Param("newBpId") String newBpId,
                              @Param("oldServiceId") String oldServiceId,
                              @Param("newServiceId") String newServiceId);

    List<ServiceRate> getServiceRateByServiceId(@Param("one_agent_no") String oneAgentNo, @Param("serviceId") String newServiceId);

    void bacthInsertServiceRate(@Param("rateList") List<ServiceRate> newServiceRateList, @Param("merchantNo") String merchantNo);

    List<ServiceQuota> getServiceQuotaByServiceId(@Param("one_agent_no") String one_agent_no, @Param("serviceId") String serviceId);

    void bacthInsertServiceQuota(@Param("quotaList") List<ServiceQuota> newServiceQuotaList, @Param("merchantNo") String merchantNo);

    @Select("SELECT COUNT(1) FROM zq_merchant_info zmi\n" +
            "JOIN merchant_business_product mbp ON mbp.id = zmi.mbp_id\n" +
            "WHERE zmi.sync_status = 1 \n" +
            "AND zmi.channel_code = 'ZF_ZQ'\n" +
            "AND mbp.merchant_no = #{merchantNo}\n" +
            "AND mbp.bp_id = #{bpId}")
    int countZF_ZQAndSyncSuccess(@Param("merchantNo") String merchantNo, @Param("bpId") String bpId);

    /**
     * 根据代理商编号获取代理商代理商的业务组织信息
     *
     * @param agentNo 业务产品id
     * @return
     */
    List<Map<String, Object>> listTeamNameByAgentNo(@Param("agentNo") String agentNo);

//    /**
//     * 根据代理商编号获取代理商代理商的业务产品信息
//     * @param agentNo 业务产品id
//     * @return
//     */
//    List<Map<String, Object>> listBpByAgentNo(@Param("agentNo") String agentNo);

    /**
     * 查询商户预警服务
     *
     * @param agentNo 代理商编号
     * @return
     */
    List<MerchantWarningBean> listMerchantWarning(@Param("agentNo") String agentNo);

    /**
     * 获取商户预警服务
     *
     * @param warningId 主键id
     * @param agentNo   代理商编号
     */
    MerchantWarningBean getMerchantWarning(@Param("warningId") String warningId,
                                           @Param("agentNo") String agentNo);

    /**
     * 根据商户编号集合查询商户信息
     *
     * @param merchantNos
     * @return
     */
    List<MerchantEsResultBean> listMerchantByNos(@Param("merchantNos") List<String> merchantNos);

    @Select("select * from function_manage where function_number=#{functionNumber}")
    Map<String, Object> findFunctionManage(@Param("functionNumber") String functionNumber);

    @Select("select * from agent_function_manage where agent_no= #{oneAgentNO}  and function_number= #{functionNumber} and team_id = #{teamId}")
    public Map<String, Object> findActivityIsSwitch(@Param("oneAgentNO") String oneAgentNO,
                                                    @Param("functionNumber") String functionNumber,
                                                    @Param("teamId") Long teamId);

    BigDecimal selectBpRateByServiceType(@Param("oneAgentNo") String oneAgentNo, @Param("bpId") String bpId, @Param("serviceType") String serviceType);

    List<Map<String, Object>> selectBpsRateByServiceType(@Param("oneAgentNo") String oneAgentNo, @Param("bpIds") List<String> bpList, @Param("serviceType") String serviceType);
}
