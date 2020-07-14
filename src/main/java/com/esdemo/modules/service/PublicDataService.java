package com.esdemo.modules.service;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/28 11:43
 */
public interface PublicDataService {
    /*
   获取新消息
    */
    List<Map<String, Object>> getMsgList(String one_level_id, String oem_type, String time_str);

    /*
   获取阅读时间戳
    */
    String getTimeStr(String user_id);

    List<Map<String,Object>> selectSurveyOrderInfoByOneAgent(String agentNo,String agentNode);

    List<Map<String,Object>> selectSurveyOrderInfo(String agentNo,String agentNode);

    /*
   获取下载应用信息
    */
    List<Map<String,Object>> getAppInfo(String app_no, String team_id);

    Integer selectBySuperPushBpId(String entityId, String key);

    String queryProtocolVersionByAppNo(String appNo);
}
