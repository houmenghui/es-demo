package com.esdemo.modules.service.impl;

import com.esdemo.modules.bean.ShiroRole;
import com.esdemo.modules.dao.UserRoleDao;
import com.esdemo.modules.service.ShiroRoleService;
import com.esdemo.modules.service.UserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Resource
    public UserRoleDao userRoleDao;
    @Resource
    public ShiroRoleService shiroRoleService;

    @Override
    public int insertUserRole(Integer userId, Integer roleId) {
        return userRoleDao.insertUserRole(userId, roleId);
    }

    @Override
    public int deleteUserRole(Integer userId, Integer role_id) {
        return userRoleDao.deleteUserRole(userId, role_id);
    }

    @Override
    public int saveUserRole(Integer userId, String[] roleIds) {
        List<ShiroRole> selectCheckBoxs = new ArrayList<>();
        List<ShiroRole> shiroRoles = shiroRoleService.findAllShiroRole();
        int i = 0;
        this.deleteUserRoleByUserId(userId);//新增角色之前，先删除用户对应的角色

        for (ShiroRole shiroRole : shiroRoles) {
            if (roleIds != null) {
                for (int j = 0; j < roleIds.length; j++) {
                    if (roleIds[j] != null && roleIds[j].trim().length() > 0) {
                        Integer _roleId = Integer.valueOf(roleIds[j]);
                        if (shiroRole.getId().equals(_roleId)) {
                            selectCheckBoxs.add(shiroRole);
                            break;
                        }
                    }
                }
            }
        }
        for (ShiroRole sr : selectCheckBoxs) {
            i = this.insertUserRole(userId, sr.getId());
        }

        return i;
    }

    @Override
    public int deleteUserRoleByUserId(Integer userId) {
        return userRoleDao.deleteUserRoleByUserId(userId);
    }


}
