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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 新欢乐送不达标扣款
 */
@Service
public class NewDeductionAmountQueryServiceImpl implements ActivityDataQueryService {

    @Resource
    ActivityDataDao activityDataDao;

    @Resource
    ActivityDataService activityDataService;

    @Override
    public ActivityData activityDataCount(ActivityAndDataQueryBean activityAndDataQueryBean ,int activtyMerCount ,ActivityData activityData) {

        activityData.setStatusType("deductionStatus");
        if(activtyMerCount > 0) {
            activityData.setExamine(newDeductionAmountMerCount("examine", activityAndDataQueryBean));
            activityData.setReachStandard(newDeductionAmountMerCount("reachStandard", activityAndDataQueryBean));
            activityData.setNotStandard(newDeductionAmountMerCount("notStandard", activityAndDataQueryBean));
        }

        activityData.setExamineName("考核中");
        activityData.setReachStandardName("无需扣款");
        activityData.setNotStandardName("需扣款");


        return activityData;
    }

    @Override
    public ActivityDataDetail activityDataCountDetail(ActivityAndDataQueryBean activityAndDataQueryBean) {
        ActivityDataDetail activityDataDetail = activityDataDao.xhlfActivityOrderDeductionDetail(activityAndDataQueryBean.getMerchantNo());
        if(activityDataDetail != null) {
            activityDataDetail.setTransAmount(activityDataDetail.getTransAmount() == null ? BigDecimal.ZERO : activityDataDetail.getTransAmount());
        }
        return activityDataDetail;
    }

    @Override
    public List<Map<String,Object>> activityDataMerchantList(ActivityAndDataQueryBean activityAndDataQueryBean) {

        //扣款不存在这三种状态，直接返回空 0 未开始 2 已达标    3 未达标
        if(Objects.equals("0",activityAndDataQueryBean.getActivityDataStatus()) ||Objects.equals("2",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("3",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  null;
        }
        //扣款不存在奖励状态，直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getRewardStatus())){
            return null;
        }
        return activityDataDao.newDeductionAndFullAmountMerList("1",activityAndDataQueryBean);
    }

    @Override
    public Map<String, Object> activityDataMerchantListCensus(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //扣款不存在这三种状态，直接返回空 0 未开始 2 已达标    3 未达标
        if(Objects.equals("0",activityAndDataQueryBean.getActivityDataStatus()) ||Objects.equals("2",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("3",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  new HashMap<>();
        }
        //扣款不存在奖励状态，直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getRewardStatus())){
            return new HashMap<>();
        }
        return activityDataDao.newDeductionAndFullAmountMerCensus("1",activityAndDataQueryBean);
    }

    @Override
    public BigDecimal countMerchantTrans(ActivityAndDataQueryBean activityAndDataQueryBean) {
        String merchantNo = activityAndDataQueryBean.getMerchantNo();

        Map<String,Object> order = new HashMap<>();
        order.put("merchant_no" ,merchantNo);

        BigDecimal merTrans = activityDataService.queryMerchantTransTotal( order,"2" );
        return merTrans;
    }

    public int newDeductionAmountMerCount(String type, ActivityAndDataQueryBean activityAndDataQueryBean) {
        Map<String,Object> countMap =  activityDataDao.newDeductionAmountMerCount(type,activityAndDataQueryBean);
        return Integer.parseInt(StringUtils.ifEmptyThen(countMap.get("activityMerCount"),"0"));
    }




}
