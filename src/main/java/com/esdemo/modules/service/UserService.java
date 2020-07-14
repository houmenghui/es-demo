package com.esdemo.modules.service;

import com.esdemo.modules.bean.UserEntityInfo;
import com.esdemo.modules.bean.UserInfo;
import com.esdemo.modules.bean.UserInfoBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:50
 */
public interface UserService {
    /**
     * 通过邮箱密码登陆
     *
     * @param email    邮箱
     * @param agentOem 代理商oem
     */
    UserInfoBean getUserInfoByEmail(String email, List<String> agentOem);

    /**
     * 通过手机号密码登陆
     *
     * @param mobile   邮箱
     * @param agentOem 代理商oem
     */
    UserInfoBean getUserInfoByMobile(String mobile, List<String> agentOem);

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
     * @param teamId
     * @return
     */
    Map<String, Object> getAgentMobilephone(String mobilephone, String teamId);

    /**
     * 根据邮箱组织号查询用户及实体信息
     *
     * @param email
     * @param teamId
     * @return
     */
    Map<String, Object> getAgentEmail(String email, String teamId);

    /**
     * 根据userId查询用户及实体信息
     *
     * @param userId
     * @return
     */
    Map<String, Object> getAgentByUserId(String userId);

    /**
     * 新增代理商管理人员信息
     *
     * @param userInfo
     * @param userEntityInfo
     * @return
     */
    int insertAgentUserInfo(UserInfo userInfo, UserEntityInfo userEntityInfo);

    /**
     * 更新人员信息
     *
     * @param userInfo
     * @return
     * @throws Exception
     */
    int updateUserInfoWithApp(UserInfo userInfo, UserEntityInfo userEntityInfo);

    /**
     * 获取代理商的管理员用户
     *
     * @param agentNo
     * @return
     */
    List<Map<String, Object>> queryManagePerson(String agentNo);

    /**
     * 清空登陆错误次数
     */
    void clearWrongPasswordCount(String userId);

    /**
     * 增加登陆错误次数
     */
    void increaseWrongPasswordCount(String userId);

    /**
     * 锁定用户登陆
     */
    void lockLoginUser(String userId);

    /**
     * 修改用户登录密码
     *
     * @param userId
     * @param newLoginPwd
     * @return
     */
    int updateUserLoginPwd(String userId, String newLoginPwd);

    /**
     * 获取极光推送信息
     * @param userType
     * @param agentNo
     * @return
     */
    Map<String, Object> selectPushInfo(String userType, String agentNo);

    /**
     * 保存极光推送信息
     */
    boolean savePushInfo(String userType, String agentNo,  String jpushDevice, String systemName, String appNo);

    /**
     * 更新极光推送信息
     */
    boolean updatePushInfo(String userType, String agentNo,  String jpushDevice, String systemName, String appNo);
}
