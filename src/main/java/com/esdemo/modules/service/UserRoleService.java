package com.esdemo.modules.service;


public interface UserRoleService {

    int insertUserRole(Integer userId, Integer roleId);

    int deleteUserRole(Integer userId, Integer role_id);

    int deleteUserRoleByUserId(Integer userId);

    int saveUserRole(Integer userId, String[] roleId);

}
