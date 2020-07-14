package com.esdemo.modules.service.impl;

import com.esdemo.modules.dao.SysConfigDao;
import com.esdemo.modules.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import com.esdemo.frame.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.function.Function;

/**
 * @Title：agentApi2
 * @Description：系统参数业务层实现
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Service
@Slf4j
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigDao sysConfigDao;

    /**
     * 根据参数名获取系统参数对象
     *
     * @param paramKey
     * @return
     */
    @Override
    public Map<String, Object> getSysConfigByKey(String paramKey) {
        return sysConfigDao.getSysConfigByKey(paramKey);
    }

    /**
     * 根据参数名获取对应值
     *
     * @param paramKey
     * @return
     */
    @Override
    public String getSysConfigValueByKey(String paramKey) {
        Map<String, Object> configMap = getSysConfigByKey(paramKey);
        if (null == configMap || configMap.isEmpty() || null == configMap.get("PARAM_VALUE")) {
            return null;
        }
        return StringUtils.filterNull(configMap.get("PARAM_VALUE"));
    }

    @Override
    public <T> T getSysConfigValueByKey(String paramKey, T defaultValue, Function<String, T> function) {
        try {
            String sysConfigValueByKey = sysConfigDao.getSysConfigValueByKey(paramKey);
            if (StringUtils.isBlank(sysConfigValueByKey)) {
                return defaultValue;
            }
            return function.apply(sysConfigValueByKey);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
