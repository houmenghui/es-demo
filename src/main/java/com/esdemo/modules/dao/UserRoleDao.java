package com.esdemo.modules.dao;

import org.apache.ibatis.annotations.*;


/**
 * by zouruijin
 * email rjzou@qq.com zrj@eeepay.cn
 * 2016年4月12日13:45:54
 */
@Mapper
public interface UserRoleDao {


    @Insert("insert into agent_user_role(user_id,role_id) values(#{userId},#{roleId})")
    int insertUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    @Delete("delete from agent_user_role where user_id = #{userId} and role_id = #{role_id}")
    int deleteUserRole(@Param("userId") Integer userId, @Param("role_id") Integer role_id);

    @Delete("delete from agent_user_role where user_id = #{userId}")
    int deleteUserRoleByUserId(@Param("userId") Integer userId);

}
