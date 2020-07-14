package com.esdemo.modules.bean;

import lombok.Data;

import java.util.Date;

/**
 * 商户实体
 */
@Data
public class MerchantInfo {
    private Long id;

    private String merchantNo;//商户ID

    private String merchantName;//商户名称(传)

    private String merchantType;//商户类型:1-个人，2-个体商户，3-企业商户

    private String lawyer;//法人姓名(传)

    private String businessType;//经营范围-商户类别：餐娱类；批发类；民生类；一般类；房车类；其他；(传,假设传参为1代表民生类)

    private String industryType;//行业类型(传MCC)

    private String industryMcc;//行业类型(传MCC)

    private String idCardNo;//法人身份证号(传)

    private String province;//经营地址（省）

    private String city;//经营地址（市）

    private String district;

    private String address;//经营地址:详细地址

    private String mobilephone;//手机号(传)

    private String email;

    private String operator;//业务人员

    private String agentNo;// 商户所属代理商编号
    private String agentName; // 商户所属代理商名称

    private Date createTime;//创建时间

    private String status;//状态
    private String statusZh;//状态

    private String parentNode;//上级代理商节点

    private String saleName;//销售人员（谁拓展的商户）

    private String creator;//创建人

    private String mender;//修改人

    private Date lastUpdateTime;//最后更新时间

    private String remark;//备注(传)

    private String oneAgentNo;

    private String teamId;
    private String teamEntryId;

    private Integer merAccount;

    private String registerSource;

    private String riskStatus;

    private String examinationOpinions;//审核意见

    private String posType;//设备类型 1移联商宝,2传统POS,3移小宝,4移联商通,5超级刷
    private String businessTypeName;
    private String industryTypeName;
    private String hlfActive;

    public String getHlfActive() {
        return hlfActive;
    }

    public void setHlfActive(String hlfActive) {
        this.hlfActive = hlfActive;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName == null ? null : merchantName.trim();
    }

    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType == null ? null : merchantType.trim();
    }

    public void setLawyer(String lawyer) {
        this.lawyer = lawyer == null ? null : lawyer.trim();
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType == null ? null : businessType.trim();
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType == null ? null : industryType.trim();
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo == null ? null : idCardNo.trim();
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone == null ? null : mobilephone.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public void setAgentNo(String agentNo) {
        this.agentNo = agentNo == null ? null : agentNo.trim();
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode == null ? null : parentNode.trim();
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName == null ? null : saleName.trim();
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public void setMender(String mender) {
        this.mender = mender == null ? null : mender.trim();
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public void setOneAgentNo(String oneAgentNo) {
        this.oneAgentNo = oneAgentNo == null ? null : oneAgentNo.trim();
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId == null ? null : teamId.trim();
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource == null ? null : registerSource.trim();
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus == null ? null : riskStatus.trim();
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setIndustryTypeName(String industryTypeName) {
        this.industryTypeName = industryTypeName;
    }

    public String getIndustryTypeName() {
        return industryTypeName;
    }
}