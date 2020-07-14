package com.esdemo.modules.service;

import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 09:52
 */
public interface AccessService {

    /**
     * 登陆代理商是否能操作指定商户
     *
     * @param loginAgentNode 登陆代理商信息节点信息
     * @param merchantNo     商户编号
     * @param isOwn          true, 只能操作直营商户, false, 能操作所有链条下的商户
     */
    boolean canAccessTheMerchant(String loginAgentNode, String merchantNo, boolean isOwn);

    /**
     * 登陆代理商是否能操作指定商户
     *
     * @param loginAgentNode 登陆代理商信息节点信息
     * @param merchantKey    商户编号/商户名称
     * @param isOwn          true, 只能操作直营商户, false, 能操作所有链条下的商户
     */
    boolean canAccessTheMerchantWithKey(String loginAgentNode, String merchantKey, boolean isOwn);

    /**
     * 登陆代理商是否能操作指定代理商
     *
     * @param loginAgentNode 登陆代理商信息节点信息
     * @param agentNo        商户进件id
     */
    boolean canAccessTheAgent(String loginAgentNode, String agentNo);

    /**
     * 登陆代理商是否能操作指定代理商（三方数据）
     *
     * @param loginAgentNo 当前登录代理商
     * @param queryAgentNo 查询代理商
     */
    boolean canAccessTheThreeAgent(String loginAgentNo, String queryAgentNo);

    /**
     * 检查登陆代理商是否有操作指定代理商数据的权限,若有这返回对应的代理商节点
     *
     * @param loginAgentNode 登陆代理商节点
     * @param agentNo        需要操作的代理商,如果为空,返回登陆代理商节点,若不为空,则返回对应的代理商节点
     * @return
     */
    String checkAndGetAgentNode(String loginAgentNode, String agentNo);

    /**
     * 根据V2商户号/商户名模糊查询超级还商户号
     *
     * @param v2MerKey
     * @param currAgentNode
     * @param isOwn
     * @return
     */
    List<String> getRepayMerNoByV2MerKey(String v2MerKey, String currAgentNode, boolean isOwn);
}
