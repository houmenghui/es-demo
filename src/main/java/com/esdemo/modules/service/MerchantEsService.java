package com.esdemo.modules.service;

import com.esdemo.frame.enums.QueryScope;
import com.esdemo.modules.bean.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 16:00
 */
public interface MerchantEsService {
    /**
     * 统计所有的商户
     *
     * @param agentNode    代理商节点
     * @param isTodayNewly false 统计所有的商户数量, true 统计的是今天的商户数量
     * @return
     */
    long countMerchant(MerchantSummarySearchBean merchantSummarySearchBean, boolean isTodayNewly);

    /**
     * 统计商户数量
     *
     * @param searchBean
     * @return
     */
    long summaryMerCountByTerms(EsSearchBean searchBean);

    /**
     * 根据组织id分类汇总
     *
     * @param merchantSummarySearchBean 查询条件
     * @param isTodayNewly 是否为当日新增商户
     */
    List<MerchantSumBean> merchantSummaryByTeamId(MerchantSummarySearchBean merchantSummarySearchBean, boolean isTodayNewly);

    /**
     * 根据业务产品查询业务产品名称
     */
    Map<String, String> listBpNameByBpIds(List<String> bpIds);

    /**
     * 汇总直接下级的代理商的商户信息
     *
     * @param parentId    父级代理商编号
     * @param pageRequest 分页信息
     */
    Tuple<List<MerchantSumBean>, Long> statisMerchantByDirectAgent(MerchantSummarySearchBean merchantSummarySearchBean, PageRequest pageRequest, boolean isTodayNewly);

    /**
     * 获取代理商开通的业务产品
     *
     * @param agentNo 代理商编号
     */
    List<Map<String, Object>> listBusinessProductByAgentNo(MerchantSearchBean searchBean, String agentNo);

    /**
     * 查询商户信息
     *
     * @param searchBean  查询条件
     * @param pageRequest 分页信息
     * @param agentNode   代理商节点
     */
    Tuple<List<MerchantEsResultBean>, Long> listMerchantInfo(MerchantSearchBean searchBean, PageRequest pageRequest, String agentNode);

    /**
     * 查询商户需要的一些参数
     */
    Map<String, List<KeyValueBean>> queryMerchantParams();

    /**
     * 参看商户详情
     *
     * @param merchantNo 商户编号
     * @param agentNode  必须所属该代理商节点下的
     */
    MerchantDetailBean getMerchantDetails(String merchantNo, String agentNode);

    /**
     * 列举可被替换的业务产品信息
     *
     * @param merchantNo 商户编号
     */
    List<MerchantBpBean> listCanReplaceBpInfo(String merchantNo, String agentNo);

    /**
     * 替换业务产品
     *
     * @param merchantNo 商户编号
     * @param oldBpId    旧业务产品
     * @param newBpId    新业务产品
     */
    void replaceBusinessProduct(String merchantNo, String oldBpId, String newBpId, String operationAgentNo);

    /**
     * 查询商户预警信息
     *
     * @param queryScope   查询范围
     * @param agentNode    代理商节点
     * @param loginAgentNo 登陆代理商编号
     */
    List<MerchantWarningBean> queryMerchantEarlyWarning(QueryScope queryScope, String agentNode, String loginAgentNo);

    /**
     * 查询商户预警信息
     *
     * @param waringId
     * @param loginAgentNo
     * @return
     */
    MerchantWarningBean queryMerchantEarlyWaring(String waringId, String loginAgentNo);

    /**
     * 获取预警详情
     *
     * @param queryScope  查询范围
     * @param agentNode   代理商节点
     * @param warningBean 预警信息
     * @param pageRequest 分页信息
     */
    Tuple<List<MerchantEsResultBean>, Long> getMerchantEarlyWarningDetails(QueryScope queryScope, String agentNode, MerchantWarningBean warningBean, PageRequest pageRequest);

    /**
     * 根据商户编号集合查询商户信息
     *
     * @param merchantNos 商户编号
     * @return
     */
    List<MerchantEsResultBean> listMerchantByNos(List<String> merchantNos);

    /**
     * 判断该代理商的是否跟更换业务产品
     *
     * @param merchantNo 登陆代理商
     */
    boolean isOpenAgentUpdateBpSwitch(String merchantNo);
}
