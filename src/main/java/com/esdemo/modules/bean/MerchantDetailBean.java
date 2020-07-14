package com.esdemo.modules.bean;

import lombok.Data;

import java.util.List;

/**
 * 商户详情
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-23 11:28
 */
@Data
public class MerchantDetailBean {

    private boolean isDirectMerchant;       // 是否为直营商户
    private String currentTransMoney;       // 本月交易量
    private Long currentTransTotal;       // 本月交易笔数
    private String allTransMoney;           // 累积交易量
    private Long allTransTotal;           // 累积交易笔数

    private MerchantInfo merchantInfo;
    private List<KeyValueBean> sevenDayDatas;   // 近七日数据
    private List<KeyValueBean> halfYearDatas;   // 半年数据
    private List<MerchantBpBean> bpDatas;    // 业务产品信息
    private List<KeyValueBean> hardwares;       // 硬件产品信息
    private boolean openAgentUpdateBpSwitch;          //是否允许代理商更改业务产品的开关

    private boolean isShowApplyAcqMerButton;       // 是否显示申请特约商户按钮
    private String acqMerStatus;       // 客户端显示特约商户状态（对应枚举）
    private String acq_into_no;       // 特约商户进件编号
    private List<ActivityDataDetail> activityDataDetails;
}
