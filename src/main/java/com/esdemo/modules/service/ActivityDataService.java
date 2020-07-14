package com.esdemo.modules.service;

import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.modules.bean.ActivityAndDataQueryBean;
import com.esdemo.modules.bean.ActivityDataDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface ActivityDataService {


     List<Map<String,Object>> agentActivityBySubType(String agentNo, String subType);


     ResponseBean activityDataTypeQuery(ActivityAndDataQueryBean activityDataQueryBean);

     ResponseBean activityDataCountQuery(ActivityAndDataQueryBean activityDataQueryBean );

     List<Map<String,Object>> activityMerCountSubConfig(String activityMerCountType);

     Map<String,Object> activityMerCountConfig(String activityMerCountType);

     int countActivityMerchantBySubType(ActivityAndDataQueryBean activityAndDataQueryBean);


     ResponseBean activityDataCountDetailQuery(ActivityAndDataQueryBean activityAndDataQueryBean);

     Map<String, Object> activityDataCountDetailCensus(ActivityAndDataQueryBean activityAndDataQueryBean);

     List<ActivityDataDetail> merActivityDataDetail(ActivityAndDataQueryBean activityAndDataQueryBean);

     BigDecimal queryMerchantTransTotal(Map<String,Object> order, String type);
}
