package com.esdemo.modules.service.impl;

import com.esdemo.modules.dao.SuperPushDao;
import com.esdemo.modules.service.SuperPushService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/6/6 10:34
 */
@Service
public class SuperPushServiceImpl implements SuperPushService {
    @Resource
    SuperPushDao superPushDao;

    /**
     * 获取超级推分润明细记录
     */
    public List<Map<String, Object>> getSuperPushShareRecord(Map<String, Object> params_map){
        return superPushDao.getSuperPushShareRecord(params_map);
    }

    /**
     * 统计超级推收益
     */
    public Map<String, Object> getSuperPushShareCount(Map<String, Object> params_map){
        return superPushDao.getSuperPushShareCount(params_map);
    }

    /**
     * 获取邀请有奖商户信息记录
     */
    public List<Map<String, Object>> getInvPriMerInfoRecord(Map<String, Object> params_map){
        return superPushDao.getInvPriMerInfoRecord(params_map);
    }

    /**
     * 统计邀请有奖商户
     */
    public Map<String,Object> countInvPriMerInfoRecord(Map<String, Object> params_map){
        return superPushDao.countInvPriMerInfoRecord(params_map);
    }

}
