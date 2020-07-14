package com.esdemo.modules.service.impl;

import com.esdemo.frame.enums.QueryScope;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.MerchantBpBean;
import com.esdemo.modules.dao.AcqMerchantDao;
import com.esdemo.modules.dao.MerchantDao;
import com.esdemo.modules.dao.MerchantInfoDao;
import com.esdemo.modules.service.AcqMerchantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2020/4/6 11:36
 * @Version：1.0
 */
@Slf4j
@Service
public class AcqMerchantServiceImpl implements AcqMerchantService {

    @Resource
    private AcqMerchantDao acqMerchantDao;
    @Resource
    private MerchantDao merchantDao;
    @Resource
    private MerchantInfoDao merchantInfoDao;

    /**
     * 根据商户号，收单商户号（一户一码特约商户号）查询收单商户记录
     *
     * @param merchantNo    商户号
     * @param acqMerchantNo 收单商户号（一户一码特约商户号）
     * @return
     */
    @Override
    public Map<String, Object> queryAcqMerByGeneralMerNo(String merchantNo, String acqMerchantNo) {
        return acqMerchantDao.queryAcqMerByGeneralMerNo(merchantNo, acqMerchantNo);
    }

    /**
     * 根据普通商户号，查询最近的收单商户进件记录
     *
     * @param merchantNo 商户号
     * @return
     */
    @Override
    public Map<String, Object> queryLatestAcqMerchantInfo(String merchantNo) {
        return acqMerchantDao.queryLatestAcqMerchantInfo(merchantNo);
    }

    /**
     * 获取商户进件要求项值
     *
     * @param merchantNo
     * @param mriId
     * @return
     */
    @Override
    public String getMerItemByNoAndMriId(String merchantNo, String mriId) {
        return acqMerchantDao.getMerItemByNoAndMriId(merchantNo, mriId);
    }

    /**
     * 获取商户所有业务产品   及   业务产品组以下的业务产品
     *
     * @param merchantNo 商户号
     * @param ownAgentNo 所属代理商编号
     * @return
     */
    @Override
    public List<MerchantBpBean> listMerBpInfoWithGroup(String merchantNo, String ownAgentNo) {
        List<MerchantBpBean> merchantBpBeanList = merchantDao.listCanReplaceBp(merchantNo);
        if (CollectionUtils.isEmpty(merchantBpBeanList)) {
            return merchantBpBeanList;
        }
        merchantBpBeanList
                .forEach(item -> {
                    List<MerchantBpBean> otherBpInTheSameGroup = merchantDao.listOtherAndUnUseBpInTheSameGroup(item.getBpId(), ownAgentNo, merchantNo);
                    //加上自己的业务产品
                    MerchantBpBean selfBean = new MerchantBpBean();
                    selfBean.setBpId(item.getBpId());
                    selfBean.setBpName(item.getBpName());
                    otherBpInTheSameGroup.add(selfBean);
                    item.setCanReplaceBpList(otherBpInTheSameGroup);
                });
        return merchantBpBeanList;
    }

    /**
     * 商户结算卡
     *
     * @param accountNo
     * @return
     */
    @Override
    public Map<String, Object> querySettleAccountNo(String accountNo) {
        return merchantInfoDao.querySettleAccountNo(accountNo);
    }

    /**
     * 获取商户结算卡信息
     *
     * @param merchantNo
     * @param accountNo
     * @return
     */
    @Override
    public Map<String, Object> findMerAccountNo(String merchantNo, String accountNo) {
        return acqMerchantDao.findMerAccountNo(merchantNo, accountNo);
    }

    /**
     * 统计机具数量
     *
     * @param agentNode
     * @param queryScope
     * @param isActive
     * @return
     */
    @Override
    public long censusTerminalCount(String agentNode, QueryScope queryScope, boolean isActive) {
        if (StringUtils.isBlank(agentNode) || queryScope == null) {
            return 0;
        }
        return acqMerchantDao.censusTerminalCount(agentNode, queryScope.getScopeCode(), isActive);
    }
}
