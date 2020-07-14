package com.esdemo.modules.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理商
 */
@Data
public class AgentInfo {
	private Long id;

	private String agentNo;

	private Integer openFloor9Points;//是否开通积分兑业务

	private String convertLink;//跳转9楼链接

	private String agentNode;

	private String agentName;

	private String agentLevel;

	private String parentId;

	private String sign;

	private Boolean isDirectChild;//是否是直属下级

	private String smsCode;

	private String parentName;

	private String oneLevelId;

	private String isOem;

	private Long teamId;

	private String email;

	private String phone;

	private String cluster;

	private String invest;

	private String agentArea;

	private String mobilephone;

	private String linkName;

	private BigDecimal investAmount;

	private String address;

	private String accountName;

	private String accountType;

	private String accountNo;

	private String bankName;

	private String cnapsNo;

	private String saleName;

	private String creator;

	private String mender;

	private Date lastUpdateTime;

	private String status;

	private Date createDate;

	private String publicQrcode;

	private String managerLogo;

	private String logoRemark;

	private String clientLogo;

	private String customTel;

	private Integer isApprove;

	private Integer countLevel;

	private Integer hasAccount;

	private String province;

	private String city;

	private String area;

	private String subBank;

	private String accountProvince;

	private String accountCity;

	private String userId;

	private String oneAgentNo;
	
	private String agentOem;
	
	private String agentType;
	private String keyword;

	private String safephone;
	private String safePassword;

	private int fullPrizeSwitch;// 满奖功能开关
	private int notFullDeductSwitch;// 不满扣功能开关

	private String registType;//拓展代理为1,其他为空

	public void setSafephone(String safephone) {
		this.safephone = safephone == null ? null : safephone.trim();
	}

	public void setSafePassword(String safePassword) {
		this.safePassword = safePassword == null ? null : safePassword.trim();
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo == null ? null : agentNo.trim();
	}

	public void setAgentNode(String agentNode) {
		this.agentNode = agentNode == null ? null : agentNode.trim();
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName == null ? null : agentName.trim();
	}

	public void setAgentLevel(String agentLevel) {
		this.agentLevel = agentLevel == null ? null : agentLevel.trim();
	}

	public void setParentId(String parentId) {
		this.parentId = parentId == null ? null : parentId.trim();
	}

	public void setOneLevelId(String oneLevelId) {
		this.oneLevelId = oneLevelId == null ? null : oneLevelId.trim();
	}

	public void setIsOem(String isOem) {
		this.isOem = isOem == null ? null : isOem.trim();
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public void setCluster(String cluster) {
		this.cluster = cluster == null ? null : cluster.trim();
	}

	public void setInvest(String invest) {
		this.invest = invest == null ? null : invest.trim();
	}

	public void setAgentArea(String agentArea) {
		this.agentArea = agentArea == null ? null : agentArea.trim();
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone == null ? null : mobilephone.trim();
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName == null ? null : linkName.trim();
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName == null ? null : accountName.trim();
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType == null ? null : accountType.trim();
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo == null ? null : accountNo.trim();
	}

	public void setBankName(String bankName) {
		this.bankName = bankName == null ? null : bankName.trim();
	}

	public void setCnapsNo(String cnapsNo) {
		this.cnapsNo = cnapsNo == null ? null : cnapsNo.trim();
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

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public void setPublicQrcode(String publicQrcode) {
		this.publicQrcode = publicQrcode == null ? null : publicQrcode.trim();
	}

	public void setManagerLogo(String managerLogo) {
		this.managerLogo = managerLogo == null ? null : managerLogo.trim();
	}

	public void setLogoRemark(String logoRemark) {
		this.logoRemark = logoRemark == null ? null : logoRemark.trim();
	}

	public void setClientLogo(String clientLogo) {
		this.clientLogo = clientLogo == null ? null : clientLogo.trim();
	}

	public void setCustomTel(String customTel) {
		this.customTel = customTel == null ? null : customTel.trim();
	}

	public void setProvince(String province) {
		this.province = province == null ? null : province.trim();
	}

	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}

	public void setArea(String area) {
		this.area = area == null ? null : area.trim();
	}
	public void setSubBank(String subBank) {
		this.subBank = subBank == null ? null : subBank.trim();
	}

	public void setAccountProvince(String accountProvince) {
		this.accountProvince = accountProvince == null ? null : accountProvince.trim();
	}

	public void setAccountCity(String accountCity) {
		this.accountCity = accountCity == null ? null : accountCity.trim();
	}
}