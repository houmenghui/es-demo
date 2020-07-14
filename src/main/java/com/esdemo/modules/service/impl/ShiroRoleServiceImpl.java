package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.modules.bean.ShiroRole;
import com.esdemo.modules.dao.ShiroRoleDao;
import com.esdemo.modules.service.ShiroRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ShiroRoleServiceImpl implements ShiroRoleService {
    @Resource
    public ShiroRoleDao shiroRoleDao;

    @Override
    public ShiroRole findShiroRoleByRoleCode(String roleCode) {
        return shiroRoleDao.findShiroRoleByRoleCode(roleCode);
    }

    @Override
    public ShiroRole findShiroRoleById(Integer id) {
        return shiroRoleDao.findShiroRoleById(id);
    }

    @Override
    public List<ShiroRole> findAllShiroRole() {
        return shiroRoleDao.findAllShiroRole();
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updateShiroRole(ShiroRole shiroRole) {
        return shiroRoleDao.updateShiroRole(shiroRole);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int insertShiroRole(ShiroRole shiroRole) {
        return shiroRoleDao.insertShiroRole(shiroRole);
    }

}
