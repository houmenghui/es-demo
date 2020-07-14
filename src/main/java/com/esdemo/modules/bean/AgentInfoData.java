package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理商信息实体
 * 对应表 agent_info
 */
@Data
public class AgentInfoData {

    private Long id;// id
    private String agentNo;//代理商编号
    private String agentNode;//代理商节点
    private String agentName;//代理商名称
    private String agentLevel;//代理商级别
    private String parentId;//上级代理商ID
    private String oneLevelId;//一级代理商ID
    private String isOem;//是否OEM
    private Long teamId;// 组织ID
    private String email;//邮箱
    private String phone;//联系电话
    private String cluster;//归属集群
    private String invest;//是否投资
    private String agentArea;//代理区域
    private String mobilephone;//手机号
    private String linkName;//代理商联系人
    private BigDecimal investAmount;//投资金额
    private String address;//代理商地址
    private String accountName;//开户名
    private String accountType;//账户类型 1-对公 2-对私
    private String accountNo;//开户账户
    private String bankName;//开户行全称
    private String cnapsNo;//联行行号
    private String saleName;//销售人员（谁拓展的代理商)
    private String creator;//创建人
    private String mender;//修改人
    private Date lastUpdateTime;//最后更新时间
    private String status;//状态：正常-1，关闭进件-0，冻结-2
    private Date createDate;//创建时间
    private String publicQrcode;//公众号二维码URL
    private String managerLogo;//管理系统LOGO
    private String logoRemark;//备注
    private String clientLogo;//客户端LOGO
    private String customTel;//客服电话
    private Integer isApprove;//一级代理商对商户进件进行初审:0-否，1-是
    private Integer countLevel;//代理商层级链条长度限制:-1:不限
    private Integer hasAccount;//是否已有账号：1已开，0否开
    private String province;//省
    private String city;//市
    private String area;//区
    private String subBank;//支行
    private String accountProvince;//开户行地区：省
    private String accountCity;//开户行地区：市
    private String sourceSys;
    private String sourceReferenceNo;
    private String sourceReferenceNo2;
    private Integer profitSwitch;//分润开关 1-打开, 0-关闭
    private Integer promotionSwitch;//代理商推广开关 1-打开, 0-关闭
    private Integer cashBackSwitch;//返现开关 1-打开, 0-关闭
    private String agentOem;//所属品牌
    private String agentType;//代理商类型
    private String agentShareLevel;//交易分润等级最高可调级数
    private String idCardNo;//身份证号
    private String safephone;//安全手机
    private String safePassword;//安全密码
    private Integer terminalBindSwitch;//增解绑开关 1-打开, 0-关闭
    private Integer fullPrizeSwitch;//满奖开关 1-打开，0-关闭
    private Integer notFullDeductSwitch;//不满扣开关 1-打开，0-关闭
    private String registType;//代理商注册类型:拓展代理为1,其他为空
    private Integer updateRateStatus;//代理商费率是否改动，0-否，1-是
    private String shareRuleInit;//1=上级未初始化结算价,2=上级已初始化结算价,3=忽略

}
