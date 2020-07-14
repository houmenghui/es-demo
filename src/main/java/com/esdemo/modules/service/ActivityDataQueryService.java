package com.esdemo.modules.service;


import com.esdemo.modules.bean.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ActivityDataQueryService {


    ActivityData activityDataCount(ActivityAndDataQueryBean activityAndDataQueryBean ,int activtyMerCount,ActivityData activityData);

    ActivityDataDetail activityDataCountDetail(ActivityAndDataQueryBean activityAndDataQueryBean);

    //获取活动商户列表
    List<Map<String,Object>> activityDataMerchantList(ActivityAndDataQueryBean activityAndDataQueryBean);


    Map<String, Object> activityDataMerchantListCensus(ActivityAndDataQueryBean activityAndDataQueryBean);

    BigDecimal countMerchantTrans(ActivityAndDataQueryBean activityAndDataQueryBean);


}
