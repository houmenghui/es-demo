package com.esdemo.modules.service;

import com.esdemo.modules.bean.UserInfoBean;

public interface FunctionManageService {

    boolean getFunctionSwitch(UserInfoBean userInfo, String functionNumber);

    boolean getAgentTerminalRelease(UserInfoBean userInfo);
}
