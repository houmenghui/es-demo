package com.esdemo.modules.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.ActCodeQueryType;
import com.esdemo.frame.enums.PosteraliSource;
import com.esdemo.frame.enums.PublicCode;
import com.esdemo.frame.enums.RepayEnum;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.ActCodeUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.ActCodeQueryBean;
import com.esdemo.modules.bean.ActivationCodeBean;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.ProviderBean;
import com.esdemo.modules.dao.ActivationCodeDao;
import com.esdemo.modules.dao.ProviderDao;
import com.esdemo.modules.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 激活码业务实现
 */
@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    @Resource
    private ActivationCodeDao activationCodeDao;
    @Resource
    private AgentInfoService agentInfoService;
    @Resource
    private SysDictService sysDictService;
    @Resource
    private ProviderDao providerDao;
    @Resource
    private ProviderService providerService;
    @Resource
    private AccessService accessService;

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
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String, Object> listNfcActivationCode(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo,
                                                     Integer pageNo, Integer pageSize) {
        Map<String, Object> resMap = new HashMap<>();

        String queryAgentNo = queryBean.getAgentNo();
        if (StringUtils.isNotBlank(queryAgentNo)) {
            AgentInfo searchAgent = agentInfoService.queryAgentInfo(queryAgentNo);
            if (null == searchAgent) {
                throw new AppException("查询代理商编号" + queryAgentNo + "不存在");
            }
            queryBean.setAgentNo(searchAgent.getAgentNo());
            queryBean.setAgentNode(searchAgent.getAgentNode());
        } else {
            queryBean.setAgentNo(loginAgentInfo.getAgentNo());
            queryBean.setAgentNode(loginAgentInfo.getAgentNode());
        }

        if (PublicCode.YES.getIsAddPublic().equals(queryBean.getIsAddPublic())) {
            ProviderBean providerBean = providerDao.queryServiceCost(loginAgentInfo.getAgentNo(), RepayEnum.NFC.getType());
            if (providerBean == null || StringUtils.isBlank(providerBean.getNfcOrigCode())) {
                // 没有开通nfc或者没有通用码,随便给一串查不出来的通用码
                queryBean.setNfcOrigCode("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            } else {
                queryBean.setNfcOrigCode(providerBean.getNfcOrigCode());
            }
        }
        //根据V2商户信息查询
        if (StringUtils.isNotBlank(queryBean.getMerchantNo())) {
            List<String> repayMerNos = ActCodeUtils.getRepayMerNoByV2MerKey(queryBean.getMerchantNo(), loginAgentInfo.getAgentNode(), false);
            if (CollectionUtil.isEmpty(repayMerNos)) {
                //不存在超级还商户信息，复制一个查不出来的商户号
                queryBean.setRepayMerNos(new String[]{"aaaaaaaaaaaaaaaaaaaa"});
            } else {
                queryBean.setRepayMerNos(repayMerNos.toArray(new String[repayMerNos.size()]));
            }
        }
        Page<ActivationCodeBean> page = PageHelper.startPage(pageNo, pageSize, true);
        activationCodeDao.listNfcActivationCode(queryBean, loginAgentInfo);
        resMap.put("page", page);
        //查询全部的时候，再下发一个可回收激活码数量
        if (ActCodeQueryType.ALL.getType().equals(queryBean.getQueryType())) {
            long canRecoveryNfcActivationCount = activationCodeDao.countRecoveryNfcActivation(queryBean, loginAgentInfo);
            resMap.put("canRecoveryNfcActivationCount", canRecoveryNfcActivationCount);
        }
        //查询未使用的时候，再已下发一个可剔除通用码的数量
        if (ActCodeQueryType.UN_USE.getType().equals(queryBean.getQueryType())) {
            ProviderBean providerBean = providerDao.queryServiceCost(loginAgentInfo.getAgentNo(), RepayEnum.NFC.getType());
            boolean canRecoveryParentCode = true;
            //先判断是否允许回收母码
            if (providerBean == null || StringUtils.isBlank(providerBean.getNfcOrigCode())) {
                canRecoveryParentCode = false;
            }
            if (canRecoveryParentCode) {
                queryBean.setNfcOrigCode(providerBean.getNfcOrigCode());
                long canRecoveryParentCodeCount = activationCodeDao.countRecoveryParentCode(queryBean, loginAgentInfo);
                resMap.put("canRecoveryParentCodeCount", canRecoveryParentCodeCount);
            }
            resMap.put("canRecoveryParentCode", canRecoveryParentCode);
        }
        return resMap;
    }

    /**
     * 划分NFC激活码
     *
     * @param operateAgentNo 接受代理商信息
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public long divideNfcActivationCode(String operateAgentNo, ActCodeQueryBean queryBean, AgentInfo loginAgentInfo) {
        if (StringUtils.isBlank(operateAgentNo)) {
            throw new AppException("未选择" + RepayEnum.NFC.getAgentName());
        }
        AgentInfo operateAgentInfo = agentInfoService.queryAgentInfoByNo(operateAgentNo);
        if (operateAgentInfo == null || !StringUtils.equals(operateAgentInfo.getParentId(), loginAgentInfo.getAgentNo())) {
            throw new AppException("只能分配给直属" + RepayEnum.NFC.getAgentName());
        }
        ProviderBean providerBean = providerDao.queryServiceCost(operateAgentNo, RepayEnum.NFC.getType());
        if (providerBean == null) {
            providerService.openOemServiceCost(Arrays.asList(operateAgentNo), loginAgentInfo, RepayEnum.NFC);
        }
        return activationCodeDao.allotNfcActivationCode2Agent(queryBean, operateAgentInfo, loginAgentInfo);
    }

    /**
     * 回收NFC激活码
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public long recoveryNfcActivation(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo) {
        String queryAgentNo = queryBean.getAgentNo();
        if (StringUtils.isNotBlank(queryAgentNo)) {
            AgentInfo searchAgent = agentInfoService.queryAgentInfo(queryAgentNo);
            if (null == searchAgent) {
                throw new AppException("查询代理商编号" + queryAgentNo + "不存在");
            }
            queryBean.setAgentNo(searchAgent.getAgentNo());
            queryBean.setAgentNode(searchAgent.getAgentNode());
        } else {
            queryBean.setAgentNo(loginAgentInfo.getAgentNo());
            queryBean.setAgentNode(loginAgentInfo.getAgentNode());
        }
        //根据V2商户信息查询
        if (StringUtils.isNotBlank(queryBean.getMerchantNo())) {
            List<String> repayMerNos = ActCodeUtils.getRepayMerNoByV2MerKey(queryBean.getMerchantNo(), loginAgentInfo.getAgentNode(), false);
            if (CollectionUtil.isEmpty(repayMerNos)) {
                //不存在超级还商户信息，复制一个查不出来的商户号
                queryBean.setRepayMerNos(new String[]{"aaaaaaaaaaaaaaaaaaaa"});
            } else {
                queryBean.setRepayMerNos(repayMerNos.toArray(new String[repayMerNos.size()]));
            }
        }
        return activationCodeDao.recoveryNfcActivation(queryBean, loginAgentInfo);
    }

    /**
     * 分配母码
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public long assignParentCode(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo) {
        ProviderBean providerBean = providerDao.queryServiceCost(loginAgentInfo.getAgentNo(), RepayEnum.NFC.getType());
        if (providerBean == null) {
            throw new AppException(String.format("您未开通%s功能,请联系上级为您开通.", RepayEnum.NFC.getBusinessName()));
        }
        if (StringUtils.isBlank(providerBean.getNfcOrigCode())) {
            throw new AppException("你还没有通用码,请联系管理员为您生成");
        }
        //设置母码信息
        queryBean.setNfcOrigCode(providerBean.getNfcOrigCode());
        return activationCodeDao.assignParentCode(queryBean, loginAgentInfo);
    }

    /**
     * 回收母码
     *
     * @param queryBean      查询信息
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public long recoveryParentCode(ActCodeQueryBean queryBean, AgentInfo loginAgentInfo) {
        ProviderBean providerBean = providerDao.queryServiceCost(loginAgentInfo.getAgentNo(), RepayEnum.NFC.getType());
        if (providerBean == null) {
            throw new AppException(String.format("您未开通%s功能,请联系上级为您开通.", RepayEnum.NFC.getBusinessName()));
        }
        if (StringUtils.isBlank(providerBean.getNfcOrigCode())) {
            throw new AppException("你还没有通用码,请联系管理员为您生成");
        }
        queryBean.setNfcOrigCode(providerBean.getNfcOrigCode());
        return activationCodeDao.recoveryParentCode(queryBean, loginAgentInfo);
    }

    /**
     * 汇总母码信息
     *
     * @param loginAgentInfo 登陆代理商
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String, Object> summaryParentCode(AgentInfo loginAgentInfo) {
        Map<String, Object> result = new HashMap<>();
        String loginAgentNo = loginAgentInfo.getAgentNo();

        ProviderBean providerBean = providerDao.queryServiceCost(loginAgentNo, RepayEnum.NFC.getType());
        String nfcOrigCode = "";
        String qrCodeUrl = "";
        result.put("total", 0);
        result.put("used", 0);
        result.put("noUsed", 0);
        if (providerBean != null && StringUtils.isNotBlank(providerBean.getNfcOrigCode())) {
            nfcOrigCode = providerBean.getNfcOrigCode();
            int total = providerDao.countParentCode(nfcOrigCode, loginAgentNo);
            int used = providerDao.countUsedParentCode(nfcOrigCode, loginAgentNo);
            result.put("total", total);
            result.put("used", used);
            result.put("noUsed", total - used);
        }
        qrCodeUrl = ActCodeUtils.getActQrCodeContent(nfcOrigCode, true);
        result.put("qrCodeUrl", qrCodeUrl);
        result.put("source", PosteraliSource.SUMMARY_PARENT_CODE.getSource());
        return result;
    }

    /**
     * 根据超级还商户号获取V2商户信息
     *
     * @param repayMerNo 超级还商户号
     * @return
     */
    @Override
    public Map<String, Object> getV2MerInfoByRepayMerNo(String repayMerNo) {
        return activationCodeDao.getV2MerInfoByRepayMerNo(repayMerNo);
    }

    /**
     * 查询激活码详情
     *
     * @param codeId 激活码id
     * @return
     */
    @Override
    public ActivationCodeBean getActivationCodeById(String codeId) {
        return activationCodeDao.getActivationCodeById(codeId);
    }

    /**
     * 根据代理商编号和母码获取激活码对象
     *
     * @param agentNo     代理商编号
     * @param nfcOrigCode 母码
     * @return
     */
    @Override
    public ActivationCodeBean getActivationCodeByAgentNoAndNfcOrigCode(String agentNo, String nfcOrigCode) {
        return activationCodeDao.getActivationCodeByAgentNoAndNfcOrigCode(agentNo, nfcOrigCode);
    }
}
