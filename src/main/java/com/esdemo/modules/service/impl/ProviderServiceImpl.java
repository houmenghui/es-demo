package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.enums.RepayEnum;
import com.esdemo.frame.exception.AppException;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.ProviderBean;
import com.esdemo.modules.dao.ProviderDao;
import com.esdemo.modules.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 666666 on 2017/10/27.
 */
@Service
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    @Resource
    private ProviderDao providerDao;

    /**
     * 开通oem服务
     *
     * @param agentNoList
     * @param loginAgent
     * @return
     */
    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public boolean openOemServiceCost(List<String> agentNoList, AgentInfo loginAgent, RepayEnum type) {
        if (CollectionUtils.isEmpty(agentNoList)) {
            log.error("请选择需要开通的" + type.getAgentName());
            throw new AppException("请选择需要开通的" + type.getAgentName() + ".");
        }

        ProviderBean loginProviderBean = providerDao.queryServiceCost(loginAgent.getAgentNo(), type.getType());
        if (loginProviderBean == null) {
            log.error("登陆" + type.getAgentName() + "没有开通此功能");
            throw new AppException("登陆" + type.getAgentName() + "没有开通此功能.");
        }

        int count = providerDao.checkAgentNoIsDirectChildren(agentNoList, loginAgent, type.getType());
        if (count != agentNoList.size()) {
            log.error("选中" + type.getAgentName() + "已经开通此功能或不是登陆代理商的直属下级");
            throw new AppException("选中" + type.getAgentName() + "已经开通此功能或不是登陆代理商的直属下级.");
        }

        List<ProviderBean> wantAddAgent = new ArrayList<>();
        for (String agentNo : agentNoList) {
            ProviderBean providerBean = new ProviderBean(agentNo, loginProviderBean.getRate(), loginProviderBean.getSingleAmount(),
                    loginProviderBean.getFullRepayRate(), loginProviderBean.getFullRepaySingleAmount(),
                    loginProviderBean.getPerfectRepayRate(), loginProviderBean.getPerfectRepaySingleAmount());
            if (type == RepayEnum.NFC) {
                providerBean.setNfcOrigCode(UUID.randomUUID().toString());
            }
            wantAddAgent.add(providerBean);
        }
        providerDao.openOemServiceCost(wantAddAgent, type.getType());
        return true;
    }

    /**
     * 查询oemServiceCost信息
     *
     * @param agentNo
     * @param type
     * @return
     */
    @Override
    public ProviderBean queryOemServiceCost(String agentNo, String type) {
        return providerDao.queryOemServiceCost(agentNo, type);
    }

    /**
     * 根据代理商编号及服务类型查询
     *
     * @param agentNo
     * @param type
     * @return
     */
    @Override
    public ProviderBean queryServiceCost(String agentNo, String type) {
        return providerDao.queryServiceCost(agentNo, type);
    }
}
