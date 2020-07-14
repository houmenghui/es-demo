package com.esdemo.modules.service;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/6/6 10:34
 */
public interface SuperPushService {
    /**
     * 获取超级推分润明细记录
     */
    List<Map<String, Object>> getSuperPushShareRecord(Map<String, Object> params_map);

    /**
     * 统计超级推收益
     */
    Map<String, Object> getSuperPushShareCount(Map<String, Object> params_map);

    /**
     * 获取邀请有奖商户信息记录
     */
    List<Map<String, Object>> getInvPriMerInfoRecord(Map<String, Object> params_map);

    /**
     * 统计邀请有奖商户
     */
    Map<String,Object> countInvPriMerInfoRecord(Map<String, Object> params_map);

}
