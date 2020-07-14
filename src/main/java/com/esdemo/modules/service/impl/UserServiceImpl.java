package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.modules.bean.UserEntityInfo;
import com.esdemo.modules.bean.UserInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.MerchantInfoDao;
import com.esdemo.modules.dao.UserDao;
import com.esdemo.modules.service.UserRoleService;
import com.esdemo.modules.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 09:50
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private SeqService seqService;
    @Resource
    private MerchantInfoDao merchantInfoDao;
    @Resource
    private UserRoleService userRoleService;

    @Override
    public UserInfoBean getUserInfoByEmail(String email, List<String> agentOemList) {
        if (StringUtils.isBlank(email)  || CollectionUtils.isEmpty(agentOemList)) {
            return null;
        }
        return userDao.getUserInfoByEmail(email, agentOemList);
    }

    @Override
    public UserInfoBean getUserInfoByMobile(String mobile, List<String> agentOemList) {
        if (StringUtils.isBlank(mobile)  || CollectionUtils.isEmpty(agentOemList)) {
            return null;
        }
        return userDao.getUserInfoByMobile(mobile, agentOemList);
    }

    /**
     * 通过查询条件获取代理商人员信息
     *
     * @param params
     * @return
     */
    @Override
    public List<Map<String, Object>> getUserInfoByParams(@Param("params") Map<String, String> params) {
        return userDao.getUserInfoByParams(params);
    }

    /**
     * 根据手机号组织号查询用户及实体信息
     *
     * @param mobilephone
     * @param teamId
     * @return
     */
    @Override
    public Map<String, Object> getAgentMobilephone(String mobilephone, String teamId) {
        return userDao.getAgentMobilephone(mobilephone, teamId);
    }

    /**
     * 根据邮箱组织号查询用户及实体信息
     *
     * @param email
     * @param teamId
     * @return
     */
    @Override
    public Map<String, Object> getAgentEmail(String email, String teamId) {
        return userDao.getAgentEmail(email, teamId);
    }

    /**
     * 根据userId查询用户及实体信息
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> getAgentByUserId(String userId) {
        return userDao.getAgentByUserId(userId);
    }

    /**
     * 新增代理商管理人员信息
     *
     * @param userInfo
     * @param userEntityInfo
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int insertAgentUserInfo(UserInfo userInfo, UserEntityInfo userEntityInfo) {
        UserInfo checkUser = merchantInfoDao.getMobilephone(userInfo.getMobilephone(), userInfo.getTeamId());
        if (checkUser == null) {//没有用户信息但没有应用信息
            String userId = seqService.createKey(Constants.USER_NO_SEQ, new BigInteger(Constants.USER_VALUE));
            String possword = Md5.md5Str("123456{" + userInfo.getMobilephone() + "}");
            userInfo.setPassword(possword);
            userInfo.setUserId(userId);
            int row = merchantInfoDao.insertUserInfo(userInfo);
            if (row != 1) {
                String msg = "失败:用户信息插入失败";
                throw new RuntimeException(msg);
            }
            userEntityInfo.setUserId(userId);
        } else {
            userEntityInfo.setUserId(checkUser.getUserId());
            userInfo.setPassword(checkUser.getPassword());
        }

        int entityrow = merchantInfoDao.insertAgentUserEntity(userEntityInfo);
        String[] roleIds = new String[]{"6"};

        if ("1".equals(userEntityInfo.getManage())) {
            roleIds = new String[]{"5"};
        }
        int roleRow = userRoleService.saveUserRole(userEntityInfo.getId(), roleIds);
        if (roleRow < 1) {
            String msg = "失败:用户角色插入失败";
            throw new RuntimeException(msg);
        }
        return entityrow;
    }

    /**
     * 更新人员信息
     *
     * @param userInfo
     * @param userEntityInfo
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updateUserInfoWithApp(UserInfo userInfo, UserEntityInfo userEntityInfo) {

        String userId = userInfo.getUserId();
        String manage = userEntityInfo.getManage();
        UserEntityInfo userEntity = userDao.getEntityInfo(userId);
        UserInfo agentInfo = userDao.getUserInfo(userId);
        String userName = agentInfo.getUserName();
        //默认管理角色
        String[] roleIds = {"5"};
        //用户角色进行了更改
        if (!userEntity.getManage().equals(manage)) {
            if ("0".equals(manage)) {
                roleIds = new String[]{"6"};
            }
            if ("1".equals(manage)) {
                roleIds = new String[]{"5"};
            }
            int roleRow = userRoleService.saveUserRole(userEntity.getId(), roleIds);
            if (roleRow < 1) {
                String msg = "失败:用户角色更新失败";
                throw new RuntimeException(msg);
            }
        }
        //更新用户信息
        int countRow = userDao.updateUserInfo(userInfo);
        //更新实体信息
        countRow = userDao.updateUserEntity(userEntityInfo);
        return countRow;
    }

    /**
     * 获取代理商的管理员用户
     *
     * @param agentNo
     * @return
     */
    @Override
    public List<Map<String, Object>> queryManagePerson(String agentNo) {
        return userDao.queryManagePerson(agentNo);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public void clearWrongPasswordCount(String userId) {
        userDao.clearWrongPasswordCount(userId);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public void increaseWrongPasswordCount(String userId) {
        userDao.increaseWrongPasswordCount(userId);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public void lockLoginUser(String userId) {
        userDao.lockLoginUser(userId);
    }

    /**
     * 修改用户登录密码
     *
     * @param userId
     * @param newLoginPwd
     * @return
     */
    @Override
    @Transactional
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public int updateUserLoginPwd(String userId, String newLoginPwd) {
        if (StringUtils.isBlank(userId, newLoginPwd)) {
            return 0;
        }
        return userDao.updateUserLoginPwd(userId, newLoginPwd);
    }

    @Override
    public Map<String, Object> selectPushInfo(String userType, String agentNo) {
        return userDao.selectPushInfo(userType, agentNo);
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public boolean savePushInfo(String userType, String agentNo, String jpushDevice, String systemName, String appNo) {
        return userDao.savePushInfo(userType, agentNo, jpushDevice, systemName, appNo) > 0;
    }

    @Override
    @DataSourceSwitch(value = DataSourceType.WRITE)
    public boolean updatePushInfo(String userType, String agentNo, String jpushDevice, String systemName, String appNo) {
        return userDao.updatePushInfo(userType, agentNo, jpushDevice, systemName, appNo) > 0;
    }
}
