package com.esdemo.modules.service.impl;

import com.esdemo.modules.bean.SysDict;
import com.esdemo.modules.dao.SysDictDao;
import com.esdemo.modules.service.SysDictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class SysDictServiceImpl implements SysDictService {
    @Resource
    SysDictDao sysDictDao;

    @Override
    public Map<String, Object> getSysDictByKey(String paramKey) {
        return sysDictDao.getDictValue(paramKey);
    }

    @Override
    public String getDictSysValue(String key) {
        return sysDictDao.getDictSysValue(key);
    }

    @Override
    public String getSysDictValueByKey(String paramKey) {
        return sysDictDao.getDictSysValue(paramKey);
    }

    @Override
    public <T> T getSysDictValueByKey(String paramKey, T defaultValue, Function<String, T> function) {
        return null;
    }

    @Override
    public List<Map<String,Object>> sysDicts(String key) {
        return sysDictDao.getDictValues(key);
    }

    @Override
    public SysDict sysDictByKeyAndValue(String key, String value) {
        return sysDictDao.sysDictByKeyAndValue(key,value);
    }
}
