package com.esdemo.modules.service;


import com.esdemo.modules.bean.ShiroRole;

import java.util.List;

public interface ShiroRoleService {
    ShiroRole findShiroRoleByRoleCode(String roleCode);

    ShiroRole findShiroRoleById(Integer id);

    List<ShiroRole> findAllShiroRole();

    int updateShiroRole(ShiroRole shiroRole);

    int insertShiroRole(ShiroRole shiroRole);
}
