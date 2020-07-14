package com.esdemo.modules.service.impl;

import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.dao.ThreeAgentDao;
import com.esdemo.modules.service.ThreeAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title：agentApi2
 * @Description：三方代理商业务接口
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Slf4j
@Service
public class ThreeAgentServiceImpl implements ThreeAgentService {

    @Resource
    private ThreeAgentDao threeAgentDao;

    /**
     * 获取三方直接下级代理商编号
     * @param agentNo
     * @return
     */
    @Override
    public List<AgentInfo> getDirectChildThreeAgent(String agentNo) {
        return threeAgentDao.getDirectChildThreeAgent(agentNo);
    }
}
