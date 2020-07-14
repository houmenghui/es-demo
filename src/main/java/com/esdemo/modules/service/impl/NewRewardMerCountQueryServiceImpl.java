package com.esdemo.modules.service.impl;


import com.esdemo.frame.enums.ActivityDataEnum;
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
 * 新欢乐送第n次考核奖励
 */
@Service
public class NewRewardMerCountQueryServiceImpl implements ActivityDataQueryService {

    @Resource
    ActivityDataDao activityDataDao;

    @Resource
    ActivityDataService activityDataService;

    @Override
    public ActivityData activityDataCount(ActivityAndDataQueryBean activityAndDataQueryBean , int activtyMerCount ,ActivityData activityData) {

        String currentCycle=  getCurrentCycle(activityAndDataQueryBean.getActivityData());
        if(StringUtils.isEmpty(currentCycle)){
            return activityData;
        }
        if(activtyMerCount > 0) {
            activityData.setExamine(newRewardAmountMerCount(currentCycle, "examine", activityAndDataQueryBean));
            activityData.setReachStandard(newRewardAmountMerCount(currentCycle, "reachStandard", activityAndDataQueryBean));
            activityData.setNotStandard(newRewardAmountMerCount(currentCycle, "notStandard", activityAndDataQueryBean));
            activityData.setNotBegin(newRewardAmountMerCount(currentCycle, "notBegin", activityAndDataQueryBean));
        }
        return activityData;
    }

    @Override
    public ActivityDataDetail activityDataCountDetail(ActivityAndDataQueryBean activityAndDataQueryBean) {
        ActivityDataDetail activityDataDetail =  activityDataDao.xhlfActivityOrderRewardDetail(activityAndDataQueryBean.getMerchantNo(),getCurrentCycle(activityAndDataQueryBean.getActivityData()));
        if(activityDataDetail != null){
            activityDataDetail.setTransAmount(activityDataDetail.getTransAmount() == null ? BigDecimal.ZERO : activityDataDetail.getTransAmount());
        }

        return activityDataDetail;
    }

    @Override
    public List<Map<String,Object>> activityDataMerchantList(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //奖励不存在这两种状态，直接返回空
        if(Objects.equals("4",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("5",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  null;
        }
        //奖励不存在扣款数据 直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getDeductionStatus())){
            return  null;
        }
        return activityDataDao.newDeductionAndFullAmountMerList(getCurrentCycle(activityAndDataQueryBean.getActivityData()),activityAndDataQueryBean);
    }

    @Override
    public Map<String, Object> activityDataMerchantListCensus(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //奖励不存在这两种状态，直接返回空
        if(Objects.equals("4",activityAndDataQueryBean.getActivityDataStatus()) || Objects.equals("5",activityAndDataQueryBean.getActivityDataStatus()) ){
            return  new HashMap<>();
        }
        //奖励不存在扣款数据 直接返回空
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getDeductionStatus())){
            return  new HashMap<>();
        }
        return activityDataDao.newDeductionAndFullAmountMerCensus(getCurrentCycle(activityAndDataQueryBean.getActivityData()),activityAndDataQueryBean);

    }

    @Override
    public BigDecimal countMerchantTrans(ActivityAndDataQueryBean activityAndDataQueryBean) {

        String cycle = getCurrentCycle(activityAndDataQueryBean.getActivityData());
        Map<String,Object> order = activityDataDao.findXhlfActivityOrder(activityAndDataQueryBean.getMerchantNo(),cycle);

        BigDecimal mertrans = activityDataService.queryMerchantTransTotal(order,StringUtils.filterNull(order.get("agent_trans_total_type")));
        return mertrans;
    }

    public int newRewardAmountMerCount(String currentCycle, String type, ActivityAndDataQueryBean activityAndDataQueryBean) {
        Map<String,Object> countMap =  activityDataDao.newRewardAmountMerCount(currentCycle ,type,activityAndDataQueryBean);
        return Integer.parseInt(StringUtils.ifEmptyThen(countMap.get("activityMerCount"),"0"));
    }

    public String getCurrentCycle(ActivityDataEnum activityDataEnum){
        if(Objects.equals("newOneRewardAmount",activityDataEnum.getActivityDataType())){
            return "1";
        }
        if(Objects.equals("newTwoRewardAmount",activityDataEnum.getActivityDataType())){
            return "2";
        }
        if(Objects.equals("newThreeRewardAmount",activityDataEnum.getActivityDataType())){
            return "3";
        }
        if(Objects.equals("newFourRewardAmount",activityDataEnum.getActivityDataType())){
            return "4";
        }
        return null;
    }

}
