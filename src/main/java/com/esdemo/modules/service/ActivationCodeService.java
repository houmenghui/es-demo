package com.esdemo.modules.service;


import com.esdemo.modules.bean.ActCodeQueryBean;
import com.esdemo.modules.bean.ActivationCodeBean;
import com.esdemo.modules.bean.AgentInfo;

import java.util.Map;

/**
 * 激活码处理接口
 */
public interface ActivationCodeService {

    /**
     * 分页获取NFC激活码信息
     * 查询全部的时候，再下发一个可回收激活码数量
     * 查询未使用的时候，再已下发一个可剔除通用码的数量
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @param pageNo         分页信息
     * @param pageSize       分页信息
     */
    Map<String, Object> listNfcActivationCode(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo,
                                              Integer pageNo, Integer pageSize);

    /**
     * 划分NFC激活码
     *
     * @param operateAgentNo 接受代理商信息
     * @param queryBean        查询信息
     * @param loginAgentInfo   登陆代理商
     * @return
     */
    long divideNfcActivationCode(String operateAgentNo, ActCodeQueryBean queryBean, AgentInfo loginAgentInfo);

    /**
     * 回收激活码
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    long recoveryNfcActivation(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo);

    /**
     * 分配母码
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    long assignParentCode(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo);

    /**
     * 回收母码
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    long recoveryParentCode(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo);

    /**
     * 汇总母码信息
     *
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    Map<String, Object> summaryParentCode(AgentInfo loginAgentInfo);

    /**
     * 根据超级还商户号获取V2商户信息
     *
     * @param repayMerNo 超级还商户号
     * @return
     */
    Map<String, Object> getV2MerInfoByRepayMerNo(String repayMerNo);

    /**
     * 查询激活码详情
     *
     * @param uuidCode 激活码信息
     * @return
     */
    ActivationCodeBean getActivationCodeById(String uuidCode);

    /**
     * 根据代理商编号和母码获取激活码对象
     *
     * @param agentNo     代理商编号
     * @param nfcOrigCode 母码
     * @return
     */
    ActivationCodeBean getActivationCodeByAgentNoAndNfcOrigCode(String agentNo, String nfcOrigCode);
}
