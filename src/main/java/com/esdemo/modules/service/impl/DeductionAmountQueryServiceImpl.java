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
 * 欢乐返不满扣
 */
@Service
public class DeductionAmountQueryServiceImpl implements ActivityDataQueryService {

    @Resource
    ActivityDataDao activityDataDao;

    @Resource
    ActivityDataService activityDataService;
    @Override
    public ActivityData activityDataCount(ActivityAndDataQueryBean activityAndDataQueryBean , int activtyMerCount ,ActivityData activityData) {

        if(activtyMerCount > 0 ) {
            activityData.setExamine(deductionAmountMerCount("examine", activityAndDataQueryBean));
            activityData.setReachStandard(deductionAmountMerCount("reachStandard", activityAndDataQueryBean));
            activityData.setNotStandard(deductionAmountMerCount("notStandard", activityAndDataQueryBean));
        }
        activityData.setStatusType("deductionStatus");
        activityData.setExamineName("考核中");
        activityData.setReachStandardName("无需扣款");
        activityData.setNotStandardName("需扣款");
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
        //扣款不存在这两种状态，直接返回空
        if(Objects.equals("2",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("3",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  null;
        }
        //扣款不存在奖励状态，直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getRewardStatus())){
            return null;
        }
        return activityDataDao.deductionAndFullAmountMerList(activityAndDataQueryBean);
    }

    @Override
    public Map<String, Object> activityDataMerchantListCensus(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //扣款不存在这两种状态，直接返回空
        if(Objects.equals("2",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("3",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  new HashMap<>();
        }
        //扣款不存在奖励状态，直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getRewardStatus())){
            return  new HashMap<>();
        }
        return activityDataDao.deductionAndFullAmountMerCensus(activityAndDataQueryBean);
    }

    @Override
    public BigDecimal countMerchantTrans(ActivityAndDataQueryBean activityAndDataQueryBean) {

        String merchantNo = activityAndDataQueryBean.getMerchantNo();

        Map<String,Object> order = new HashMap<>();
        order.put("merchant_no" ,merchantNo);

        BigDecimal merTrans = activityDataService.queryMerchantTransTotal( order,"2.2" );
        return merTrans;
    }

    public int deductionAmountMerCount(String type, ActivityAndDataQueryBean activityAndDataQueryBean) {
        Map<String,Object> countMap =  activityDataDao.deductionAmountMerCount(type,activityAndDataQueryBean);
        return Integer.parseInt(StringUtils.ifEmptyThen(countMap.get("activityMerCount"),"0"));
    }
}
