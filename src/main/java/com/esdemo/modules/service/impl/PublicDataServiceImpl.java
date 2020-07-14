package com.esdemo.modules.service.impl;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.dao.PublicDataDao;
import com.esdemo.modules.service.PublicDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/28 11:44
 */
@Slf4j
@Service
public class PublicDataServiceImpl implements PublicDataService {
    @Resource
    private PublicDataDao publicDataDao;

    /*
   获取新消息
    */
    public List<Map<String, Object>> getMsgList(String one_level_id, String oem_type, String time_str) {
        return publicDataDao.getMsgList(one_level_id, oem_type, time_str);
    }

    /*
    获取阅读时间戳
    */
    public String getTimeStr(String user_id) {
        return publicDataDao.getTimeStr(user_id);
    }

    public List<Map<String, Object>> selectSurveyOrderInfoByOneAgent(String agentNo, String agentNode) {
        return publicDataDao.selectSurveyOrderInfoByOneAgent(agentNo, agentNode);
    }

    public List<Map<String, Object>> selectSurveyOrderInfo(String agentNo, String agentNode) {
        return publicDataDao.selectSurveyOrderInfo(agentNo, agentNode);
    }

    /*
   获取下载应用信息
    */
    public List<Map<String, Object>> getAppInfo(String app_no, String team_id) {
        return publicDataDao.getAppInfo(app_no, team_id);
    }

    @Override
    public Integer selectBySuperPushBpId(String entityId, String key) {
        return publicDataDao.selectBySuperPushBpId(entityId, key);
    }

    /**
     * 下发隐私权协议的版本
     * @param appNo
     * @return
     */
    @Override
    public String queryProtocolVersionByAppNo(String appNo) {
        String version = publicDataDao.queryProtocolVersionByAppNo(appNo);
        return StringUtils.isBlank(version) ? "0" : version;
    }
}
