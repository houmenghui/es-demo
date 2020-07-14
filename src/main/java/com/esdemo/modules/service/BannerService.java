package com.esdemo.modules.service;

import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/29 11:25
 */
public interface BannerService {
    /*
    banner查询
     */
    List<Map<String, Object>> findBanner(String app_no);

}
