package com.esdemo.modules.service.impl;

import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.dao.AccessDao;
import com.esdemo.modules.dao.AgentInfoDao;
import com.esdemo.modules.dao.ThreeAgentDao;
import com.esdemo.modules.service.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-27 09:53
 */
@Slf4j
@Service
public class AccessServiceImpl implements AccessService {
    @Resource
    private AccessDao accessDao;
    @Resource
    private AgentInfoDao agentInfoDao;
    @Resource
    private ThreeAgentDao threeAgentDao;

    @Override
    public boolean canAccessTheMerchant(String loginAgentNode, String merchantNo, boolean isOwn) {
        return accessDao.canAccessTheMerchant(loginAgentNode, merchantNo, isOwn) > 0;
    }

    @Override
    public boolean canAccessTheMerchantWithKey(String loginAgentNode, String merchantKey, boolean isOwn) {
        return accessDao.canAccessTheMerchantWithKey(loginAgentNode, merchantKey, isOwn) > 0;
    }

    @Override
    public boolean canAccessTheAgent(String loginAgentNode, String agentNo) {
        return accessDao.canAccessTheAgent(loginAgentNode, agentNo) > 0;
    }

    @Override
    public boolean canAccessTheThreeAgent(String loginAgentNo, String queryAgentNo) {
        if(StringUtils.isNotBlank(loginAgentNo) && loginAgentNo.equalsIgnoreCase(queryAgentNo)){
            return true;
        }
        return threeAgentDao.canAccessTheThreeAgent(loginAgentNo, queryAgentNo) > 0;
    }

    @Override
    public String checkAndGetAgentNode(String loginAgentNo, String agentNo) {
        if (StringUtils.isBlank(agentNo)) {
            return loginAgentNo;
        }
        AgentInfo agentInfo = agentInfoDao.selectByAgentNo(agentNo);
        if (!canAccessTheAgent(loginAgentNo, agentNo) || agentInfo == null) {
            throw new AppException("无权操作");
        }
        return agentInfo.getAgentNode();
    }

    /**
     * 根据V2商户号/商户名模糊查询超级还商户号
     * @param v2MerKey
     * @param currAgentNode
     * @param isOwn
     * @return
     */
    @Override
    public List<String> getRepayMerNoByV2MerKey(String v2MerKey, String currAgentNode, boolean isOwn) {
        return accessDao.getRepayMerNoByV2MerKey(v2MerKey, currAgentNode, isOwn);
    }
}
