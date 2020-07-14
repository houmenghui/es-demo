package com.esdemo.modules.dao;

import com.esdemo.modules.bean.ShiroRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShiroRoleDao {


    @Select("select id,role_code,role_name,role_remake,role_state,create_operator,create_time,update_time from agent_shiro_rigth where role_code = #{rigthCode}")
    ShiroRole findShiroRoleByRoleCode(@Param("roleCode") String roleCode);

    @Select("select id,role_code,role_name,role_remake,role_state,create_operator,create_time,update_time from agent_shiro_role where id = #{id}")
    ShiroRole findShiroRoleById(@Param("id") Integer id);

    @Select("select id,role_code,role_name,role_remake,role_state,create_operator,create_time,update_time from agent_shiro_role ")
    List<ShiroRole> findAllShiroRole();

    @Update("update agent_shiro_role set role_code = #{shiroRole.roleCode},role_name= #{shiroRole.roleName},role_remake= #{shiroRole.roleRemake},update_time= #{shiroRole.updateTime} where id = #{shiroRole.id}")
    int updateShiroRole(@Param("shiroRole") ShiroRole shiroRole);

    @Insert("insert into agent_shiro_role(role_code,role_name,role_remake,role_state,create_operator,create_time) "
            + "values(#{shiroRole.roleCode},#{shiroRole.roleName},#{shiroRole.roleRemake},#{shiroRole.roleState},#{shiroRole.createOperator},#{shiroRole.createTime})")
    int insertShiroRole(@Param("shiroRole") ShiroRole shiroRole);
}
