package com.esdemo.modules.service;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/21 11:40
 */
public interface NoticeService {
    /*
    获取OemType,one_level_id
     */
    Map<String, Object> getOemInfo(String agentNo);

    /*
   获取首页消息
    */
    List<Map<String, Object>> getHomeMsg(String one_level_id,String oem_type);

    /*
    获取消息详情
    */
    Map<String, Object> getMsgDetail(String nt_id);

    /*
     获取消息列表
    */
    List<Map<String, Object>> getMsgList(String one_level_id_flag,String oem_type);

    /*
    获取弹窗消息
    */
    List<Map<String, Object>> getPopupMsg(String one_level_id,String oem_type);


    /*
   获取昨天收入
   */
    String getYesterdayIncome(String agent_no, String start_time,String end_time);

    /**
     * 根据用户ID更新消息阅读时间
     */
    int updatelastTime(String user_id);

}
