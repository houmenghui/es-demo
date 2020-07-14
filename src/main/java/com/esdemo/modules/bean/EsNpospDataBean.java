package com.esdemo.modules.bean;

import lombok.Builder;
import lombok.Data;


/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-03 08:56
 */
@Data
public class EsNpospDataBean {

    private TypeName typeName;
    private String type;
    private String hlfActive;
    private String agentNo;
    private String parentId;
    private String agentNode;
    private String agentName;
    private String oneLevelId;
    private String province;
    private String city;
    private String district;
    private String bpId;
    private String bpName;
    private String bpType;
    private String teamId;
    private String teamEntryId;
    private String allowIndividualApply;
    private String mid;
    private String merchantNo;
    private String mobilephone;
    private String merchantName;
    private String cardType;
    private String accountNo;
    private String status;
    private String orderNo;
    private String payMethod;
    private String transStatus;
    private String transType;
    private String serviceId;
    private String serviceName;
    private String hpTypeName;
    private String versionNu;
    private String transTime;
    private String activeTime;
    private String createTime;
    private String lastUpdateTime;
    private String recommendedSource;
    private String riskStatus;
    private String settlementMethod;
    private String settleType;
    private String settleStatus;
    private String deviceSn;
    private String transAmount;
    @Data
    @Builder
    public static class TypeName{
        private String name;
        private String parent;
    }
}

