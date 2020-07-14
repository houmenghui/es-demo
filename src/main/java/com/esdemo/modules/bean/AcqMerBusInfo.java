package com.esdemo.modules.bean;

import lombok.Data;

import java.util.List;

/**
 * @program: agentApi2
 * @description:
 * @author: zhangly
 * @create: 2020/04/08
 */
@Data
public class AcqMerBusInfo {

    private List<MerBusId> changeBusinessInfo;

    @lombok.Data
    public static class MerBusId {
        private String oldBpId;
        private String newBpId;
    }
}
