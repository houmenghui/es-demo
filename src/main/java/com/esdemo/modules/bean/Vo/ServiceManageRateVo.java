package com.esdemo.modules.bean.Vo;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ServiceManageRateVo {

    private String bpId;//业务产品ID
    private String rateType;//费率类型:1-每笔固定金额，2-扣率，3-扣率带保底封顶，4-扣率+固定金额,5-单笔阶梯 扣率
    private BigDecimal singleNumAmount;//每笔固定值
    private BigDecimal rate;//扣率
}
