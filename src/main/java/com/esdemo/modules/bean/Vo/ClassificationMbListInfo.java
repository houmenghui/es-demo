package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ClassificationMbListInfo {

    private Map<String, List<AgentBpIdInfoVo>> mapBp;
    private List<AgentBpIdInfoVo> bpListAdd;

    public ClassificationMbListInfo(Map<String, List<AgentBpIdInfoVo>> mapBp, List<AgentBpIdInfoVo> bpListAdd) {
        this.mapBp = mapBp;
        this.bpListAdd = bpListAdd;
    }
}
