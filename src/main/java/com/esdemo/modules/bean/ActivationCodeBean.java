package com.esdemo.modules.bean;

import com.esdemo.frame.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 激活码对象
 */
@Data
/** 注解的作用是序列化json时，如果是null对象，key也会消失 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class ActivationCodeBean {
    private long id;
    private String uuidCode;
    private String unifiedMerchantNo;
    private String unifiedMerchantName;
    private String oneAgentNo;
    private String oneAgentName;
    private String agentNo;
    private String agentName;
    private String parentId;
    private String agentNode;
    private String status;
    private Date activateTime;
    private Date createTime;
    private String nfcOrigCode;
    private String publicFlag;

    public String getPublicFlag() {
        if (StringUtils.isBlank(this.nfcOrigCode)) {
            return "否";
        }
        return "是";
    }
}
