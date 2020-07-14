package com.esdemo.modules.service.impl;

import com.esdemo.modules.dao.BannerDao;
import com.esdemo.modules.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lmc
 * @date 2019/5/29 11:25
 */
@Service
@Slf4j
public class BannerServiceImpl implements BannerService {
    @Resource
    BannerDao bannerDao;

    /*
    banner查询
     */
    public List<Map<String, Object>> findBanner(String app_no){
        return bannerDao.findBanner(app_no);
    }
}
