package com.esdemo.modules.bean.Vo;

import lombok.Data;

@Data
public class AgentActivityCheckData {

    private boolean checkSta;//数据是否一致1 是 0否
    private AgentActivityVo checkInfo;//校验缓存实体

    public AgentActivityCheckData(boolean checkSta, AgentActivityVo checkInfo) {
        this.checkSta = checkSta;
        this.checkInfo = checkInfo;
    }
}
