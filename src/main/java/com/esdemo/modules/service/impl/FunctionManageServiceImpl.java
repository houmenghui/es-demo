package com.esdemo.modules.service.impl;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.FunctionManageDao;
import com.esdemo.modules.service.FunctionManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 获取系统开关配置service
 */
@Slf4j
@Service
public class FunctionManageServiceImpl  implements FunctionManageService {

    @Resource
    private FunctionManageDao functionManageDao;

    @Resource
    private FunctionManageService functionManageService;

    /**
     * 获取开关通用逻辑
     * @param userInfo 当前登入代理商
     * @param functionNumber 功能开关编号
     * @return 返回功能开关值 false 关闭 true开启
     */
    @Override
    public boolean getFunctionSwitch(UserInfoBean userInfo,String functionNumber) {
        Map<String, Object> functionMap = functionManageDao.getFunctionManage(functionNumber);
        if (CollectionUtils.isEmpty(functionMap)) {
            return false;
        }
        //判断黑名单是否开启
        if ("1".equals(StringUtils.filterNull(functionMap.get("blacklist")))) {
            // 是否黑名单 不包含下级
            long blacklistNotContains = functionManageDao.countBlacklistNotContains(userInfo.getAgentNo(),functionNumber);
            if (blacklistNotContains > 0) {
                return false;
            }
            // 是否黑名单 包含下级
            long blacklistContains = functionManageDao.countBlacklistContains(userInfo.getAgentNode(),functionNumber);
            if (blacklistContains > 0) {
                return false;
            }
        }
        if ("1".equals(StringUtils.filterNull(functionMap.get("function_switch")))) {
            //开启代理商就只能对应的代理商才显示头条消息
            if ("1".equals(StringUtils.filterNull(functionMap.get("agent_control")))) {
                if ("".equals(StringUtils.filterNull(functionManageDao.getAgentFunction(userInfo.getOneAgentNo(),functionNumber)))) {
                    //查询值为空则返回false
                    return false;
                }
            }
        }else{
            return false;
        }
        return true;
    }

    /**
     * 获取代理商的解绑机具权限
     * @param userInfo 当前登入代理商
     * @return
     */
    @Override
    public boolean getAgentTerminalRelease(UserInfoBean userInfo) {
        //机具解绑功能开关编号
        String functionNumber="030";
        if(userInfo.getAgentLevel().intValue()==1||userInfo.getAgentLevel().intValue()==2){
            boolean result =functionManageService.getFunctionSwitch(userInfo,functionNumber);
            if(result){
                //如果开关打开，则看是否是二级,判断一级是否开启给2级
                if(userInfo.getAgentLevel().intValue()==2){
                    if ("1".equals(StringUtils.filterNull(functionManageDao.getAgentInfoTerminal(userInfo.getAgentNo())))) {
                        return true;
                    }else{
                        return false;
                    }
                }
            }
            return result;
        }
        return false;
    }
}
