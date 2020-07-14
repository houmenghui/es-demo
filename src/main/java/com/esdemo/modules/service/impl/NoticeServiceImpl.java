package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.modules.dao.NoticeDao;
import com.esdemo.modules.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/21 11:40
 */
@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {
    @Resource
    NoticeDao noticeDao;

    /*
   获取OemType,one_level_id
    */
    public Map<String, Object> getOemInfo(String agentNo){
        return noticeDao.getOemInfo(agentNo);
    }

    /*
    获取首页消息
    */
    public List<Map<String, Object>> getHomeMsg(String one_level_id_flag,String oem_type){
        return noticeDao.getHomeMsg(one_level_id_flag, oem_type);
    }

    /*
   获取消息详情
   */
    public Map<String, Object> getMsgDetail(String nt_id){
        return noticeDao.getMsgDetail(nt_id);
    }

    /*
    获取消息列表
   */
    public List<Map<String, Object>> getMsgList(String one_level_id_flag,String oem_type){
        return noticeDao.getMsgList(one_level_id_flag, oem_type);
    }

    /*
    获取弹窗消息
    */
    public List<Map<String, Object>> getPopupMsg(String one_level_id,String oem_type){
        return noticeDao.getPopupMsg(one_level_id, oem_type);
    }

    /*
   获取昨天收入
   */
    public String getYesterdayIncome(String agent_no, String start_time,String end_time){
        return noticeDao.getYesterdayIncome(agent_no,start_time, end_time);
    }

    /**
     * 根据用户ID更新消息阅读时间
     */
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updatelastTime(String user_id){
        return noticeDao.updatelastTime(user_id);
    }

}
