package com.esdemo.modules.service.impl;

import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.dao.AppInfoDao;
import com.esdemo.modules.service.AppInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/6/4 14:39
 * @Version：1.0
 */
@Service
@Slf4j
public class AppInfoServceImpl implements AppInfoService {

    @Resource
    private AppInfoDao appInfoDao;

    /**
     * 校验APP客户端版本信息
     *
     * @param appDeviceInfo
     * @return
     */
    @Override
    public Map<String, Object> checkAppVersion(AppDeviceInfo appDeviceInfo) {

        Map<String, Object> resMap = new HashMap<>();
        if (null == appDeviceInfo) {
            return resMap;
        }
        String appNo = appDeviceInfo.getAppNo();
        String systemName = appDeviceInfo.getSystemName();
        String appVersion = appDeviceInfo.getAppVersion();

        String platForm = "android".equalsIgnoreCase(systemName) ? "0" : "1";
        resMap = appInfoDao.getAppVersion(platForm, appNo);

        if (!CollectionUtils.isEmpty(resMap)) {
            String dbVersion = StringUtils.filterNull(resMap.get("VERSION"));
            String downFlag = StringUtils.filterNull(resMap.get("DOWN_FLAG"));
            if (appVersion.equals(dbVersion)) {
                resMap.put("DOWN_FLAG", "0");
            }
        }
        return resMap;
    }
}