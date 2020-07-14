package com.esdemo.modules.service.impl;

import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.ActivityAndDataQueryBean;
import com.esdemo.modules.bean.ActivityData;
import com.esdemo.modules.bean.ActivityDataDetail;
import com.esdemo.modules.dao.ActivityDataDao;
import com.esdemo.modules.service.ActivityDataQueryService;
import com.esdemo.modules.service.ActivityDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 欢乐返满奖
 */
@Service
public class FullRewardAmountQueryServiceImpl implements ActivityDataQueryService {
    @Resource
    ActivityDataDao activityDataDao;
    @Resource
    ActivityDataService activityDataService;

    @Override
    public ActivityData activityDataCount(ActivityAndDataQueryBean activityAndDataQueryBean ,int activtyMerCount,ActivityData activityData) {
        if(activtyMerCount > 0) {
            activityData.setExamine(fullRewardMerCount("examine", activityAndDataQueryBean));
            activityData.setReachStandard(fullRewardMerCount("reachStandard", activityAndDataQueryBean));
            activityData.setNotStandard(fullRewardMerCount("notStandard", activityAndDataQueryBean));
        }
        return activityData;
    }

    @Override
    public ActivityDataDetail activityDataCountDetail(ActivityAndDataQueryBean activityAndDataQueryBean) {

        ActivityDataDetail activityDataDetail =  activityDataDao.happyBackCountDetail(activityAndDataQueryBean.getMerchantNo(),activityAndDataQueryBean.getActivityData().getActivityDataType());
        if(activityDataDetail != null) {
            activityDataDetail.setTransAmount(activityDataDetail.getTransAmount() == null ? BigDecimal.ZERO : activityDataDetail.getTransAmount());

        }
        return activityDataDetail;
    }

    @Override
    public List<Map<String,Object>> activityDataMerchantList(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //满奖不存在这两种状态，直接返回空
        if(Objects.equals("4",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("5",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  null;
        }
        //奖励不存在扣款数据 直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getDeductionStatus())){
            return  null;
        }
        return activityDataDao.deductionAndFullAmountMerList(activityAndDataQueryBean);
    }

    @Override
    public Map<String, Object> activityDataMerchantListCensus(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //满奖不存在这两种状态，直接返回空
        if(Objects.equals("4",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("5",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  new HashMap<>();
        }
        //奖励不存在扣款数据 直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getDeductionStatus())){
            return  new HashMap<>();
        }
        return activityDataDao.deductionAndFullAmountMerCensus(activityAndDataQueryBean);
    }

    @Override
    public BigDecimal countMerchantTrans(ActivityAndDataQueryBean activityAndDataQueryBean) {
        String merchantNo = activityAndDataQueryBean.getMerchantNo();

        Map<String,Object> order = new HashMap<>();
        order.put("merchant_no" ,merchantNo);

        BigDecimal merTrans = activityDataService.queryMerchantTransTotal(order,"2.2" );
        return merTrans;
    }

    public int fullRewardMerCount(String type,ActivityAndDataQueryBean activityAndDataQueryBean) {
        Map<String,Object> countMap =  activityDataDao.fullRewardMerCount(type,activityAndDataQueryBean);
        return Integer.parseInt(StringUtils.ifEmptyThen(countMap.get("activityMerCount"),"0"));
    }
}
