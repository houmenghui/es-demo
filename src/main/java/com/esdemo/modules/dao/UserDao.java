package com.esdemo.modules.dao;

import com.esdemo.modules.bean.UserEntityInfo;
import com.esdemo.modules.bean.UserInfo;
import com.esdemo.modules.bean.UserInfoBean;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:50
 */
@Mapper
public interface UserDao {
    /**
     * 通过邮箱密码登陆
     *
     * @param email        邮箱
     * @param agentOemList 代理商oem
     */
    UserInfoBean getUserInfoByEmail(@Param("email") String email,
                                    @Param("agentOemList") List<String> agentOemList);

    /**
     * 通过手机号密码登陆
     *
     * @param mobile       邮箱
     * @param agentOemList 代理商oem
     */
    UserInfoBean getUserInfoByMobile(@Param("mobile") String mobile,
                                     @Param("agentOemList") List<String> agentOemList);

    /**
     * 通过手机号码和组织结构查询用户
     *
     * @param mobileNo 手机号码
     * @param teamId   组织机构
     */
    UserInfoBean getUserInfoByMobileNoAndTeam(@Param("mobileNo") String mobileNo,
                                              @Param("teamId") String teamId);

    /**
     * 通过查询条件获取代理商人员信息
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getUserInfoByParams(@Param("params") Map<String, String> params);

    /**
     * 根据手机号组织号查询用户及实体信息
     *
     * @param mobilephone
     * @param teamID
     * @return
     */
    @Select("SELECT u.user_id,u.user_name,u.mobilephone,u.email,u.password,u.team_id,u.update_pwd_time,ui.last_notice_time,ui.manage,ui.status,ui.user_type,ui.entity_id,ui.apply FROM user_info u ,user_entity_info ui  WHERE u.user_id = ui.user_id AND  u.mobilephone = #{mobilephone} AND u.team_id = #{teamID} AND ui.user_type = '1' and ui.apply='1' ")
    Map<String, Object> getAgentMobilephone(@Param("mobilephone") String mobilephone, @Param("teamID") String teamID);

    /**
     * 根据邮件组织号查询用户及实体信息
     *
     * @param email
     * @param teamID
     * @return
     */
    @Select("SELECT u.user_id,u.user_name,u.mobilephone,u.email,u.password,u.team_id,u.update_pwd_time,ui.last_notice_time,ui.manage,ui.status,ui.user_type,ui.entity_id,ui.apply FROM user_info u ,user_entity_info ui  WHERE u.user_id = ui.user_id AND  u.email = #{email} AND u.team_id = #{teamID} AND ui.user_type = '1' and ui.apply='1' ")
    Map<String, Object> getAgentEmail(@Param("email") String email, @Param("teamID") String teamID);

    /**
     * 根据userId查询用户及实体信息
     *
     * @param userId
     * @return
     */
    @Select("SELECT u.user_id,u.user_name,u.mobilephone,u.email,u.password,u.team_id,u.update_pwd_time,ui.last_notice_time,ui.manage,ui.status,ui.user_type,ui.entity_id,ui.apply FROM user_info u ,user_entity_info ui  WHERE u.user_id = ui.user_id AND u.user_id = #{userId} AND ui.user_type = '1' and ui.apply='1' ")
    Map<String, Object> getAgentByUserId(@Param("userId") String userId);


    @Select("select * from user_entity_info where entity_id = #{agentNo} and user_type='1' and apply='1' and manage='1' and status='1' ")
    List<Map<String, Object>> queryManagePerson(@Param("agentNo") String agentNo);

    /**
     * 根据用户ID取对应实体信息
     *
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user_entity_info WHERE user_id = #{userId} AND apply = '1' ")
    @ResultType(UserEntityInfo.class)
    UserEntityInfo getEntityInfo(@Param("userId") String userId);

    /**
     * 根据用ID获取用户信息
     *
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    @ResultType(UserInfo.class)
    UserInfo getUserInfo(@Param("userId") String userId);

    /**
     * 更新用户信息
     *
     * @param userInfo
     * @return
     */
    @Update("UPDATE user_info SET user_name = #{userInfo.userName}, mobilephone = #{userInfo.mobilephone}, email = #{userInfo.email} WHERE user_id = #{userInfo.userId}")
    int updateUserInfo(@Param("userInfo") UserInfo userInfo);

    /**
     * 更新实体信息
     *
     * @param userEntityInfo
     * @return
     */
    @Update("UPDATE user_entity_info SET manage = #{userEntityInfo.manage}, status= #{userEntityInfo.status} WHERE user_id = #{userEntityInfo.userId} AND apply = #{userEntityInfo.apply} ")
    int updateUserEntity(@Param("userEntityInfo") UserEntityInfo userEntityInfo);

    /**
     * 修改用户登录密码
     *
     * @param userId
     * @param newLoginPwd
     * @return
     */
    @Update("UPDATE user_info SET password = MD5(CONCAT(#{newLoginPwd}, '{', mobilephone, '}')) WHERE user_id = #{userId} ")
    int updateUserLoginPwd(@Param("userId") String userId, @Param("newLoginPwd") String newLoginPwd);
    /**
     * 清空用户登陆次数
     */
    void clearWrongPasswordCount(@Param("userId") String userId);

    /**
     * 用户登陆错误次数+1
     */
    void increaseWrongPasswordCount(@Param("userId") String userId);

    /**
     * 锁定用户登陆
     */
    void lockLoginUser(@Param("userId") String userId);

    /**
     * 获取极光推送信息
     */
    Map<String, Object> selectPushInfo(@Param("userType") String userType,
                                       @Param("agentNo") String agentNo);

    /**
     * 保存极光推送信息
     */
    int savePushInfo(@Param("userType") String userType,
                      @Param("agentNo") String agentNo,
                      @Param("jpushDevice") String jpushDevice,
                      @Param("systemName") String systemName,
                      @Param("appNo") String appNo);

    /**
     * 更新极光推送信息
     */
    int updatePushInfo(@Param("userType") String userType,
                        @Param("agentNo") String agentNo,
                        @Param("jpushDevice") String jpushDevice,
                        @Param("systemName") String systemName,
                        @Param("appNo") String appNo);
}
