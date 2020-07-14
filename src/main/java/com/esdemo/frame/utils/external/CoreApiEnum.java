package com.esdemo.frame.utils.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoreApiEnum {

    ZF_MERCHANT_UPDATE("/zfMerchant/zfMerUpdate"),
    RISK_130_URL("/riskhandle/risk130"),
    CJT_SHARE_FOR_ALIYUN("/cjt/cjtShareForAliYun"),
    CLEAN_LONG_TOKEN("mer/user/clearLongToken"),
    //core解绑机具接口
    MER_BINDORUNBIND_TMSTPOS("/zfMerchant/merBindOrUnBindTmstpos");

    private String path;
}