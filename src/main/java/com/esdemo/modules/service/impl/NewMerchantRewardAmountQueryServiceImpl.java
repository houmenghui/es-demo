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
 * 新欢乐送商户奖励
 */
@Service
public class NewMerchantRewardAmountQueryServiceImpl implements ActivityDataQueryService {

    @Resource
    ActivityDataDao activityDataDao;

    @Resource
    ActivityDataService activityDataService;

    @Override
    public ActivityData activityDataCount(ActivityAndDataQueryBean activityAndDataQueryBean ,int activtyMerCount ,ActivityData activityData) {

        if(activtyMerCount  > 0) {
            activityData.setExamine(newMerchantRewardAmountMerCount("examine", activityAndDataQueryBean));
            activityData.setReachStandard(newMerchantRewardAmountMerCount("reachStandard", activityAndDataQueryBean));
            activityData.setNotStandard(newMerchantRewardAmountMerCount("notStandard", activityAndDataQueryBean));

        }
        return activityData;
    }

    @Override
    public ActivityDataDetail activityDataCountDetail(ActivityAndDataQueryBean activityAndDataQueryBean) {
        ActivityDataDetail activityDataDetail = activityDataDao.newMerchantRewardAmountCountDetail(activityAndDataQueryBean.getMerchantNo());
        if(activityDataDetail != null) {
            activityDataDetail.setTransAmount(activityDataDetail.getTransAmount() == null ? BigDecimal.ZERO : activityDataDetail.getTransAmount());
        }
        return activityDataDetail;
    }

    @Override
    public List<Map<String,Object>> activityDataMerchantList(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款
        if(Objects.equals("0",activityAndDataQueryBean.getActivityDataStatus()) ||
                Objects.equals("4",activityAndDataQueryBean.getActivityDataStatus())||
                Objects.equals("5",activityAndDataQueryBean.getActivityDataStatus())){
            return null;
        }
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getDeductionStatus())){
            return null;
        }
//        // 传过来是 1 考核中  2 已达标    3 未达标
//        //数据库 0 1 2
//        if(!StringUtils.isEmpty(activityAndDataQueryBean.getActivityDataStatus())) {
//            int status = Integer.parseInt(activityAndDataQueryBean.getActivityDataStatus());
//            status = status - 1;
//            activityAndDataQueryBean.setActivityDataStatus(status + "");
//        }
        return activityDataDao.newMerchantRewardAmountMerList(activityAndDataQueryBean);
    }

    @Override
    public Map<String, Object> activityDataMerchantListCensus(ActivityAndDataQueryBean activityAndDataQueryBean) {
        //0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款
        if(Objects.equals("0",activityAndDataQueryBean.getActivityDataStatus()) ||
                Objects.equals("4",activityAndDataQueryBean.getActivityDataStatus())||
                Objects.equals("5",activityAndDataQueryBean.getActivityDataStatus())){
            return new HashMap<>();
        }
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getDeductionStatus())){
            return new HashMap<>();
        }
        if(!StringUtils.isEmpty(activityAndDataQueryBean.getActivityDataStatus())) {
            int status = Integer.parseInt(activityAndDataQueryBean.getActivityDataStatus());
            status = status - 1;
            activityAndDataQueryBean.setActivityDataStatus(status + "");
        }
        return activityDataDao.newMerchantRewardAmountMerCensus(activityAndDataQueryBean);
    }

    @Override
    public BigDecimal countMerchantTrans(ActivityAndDataQueryBean activityAndDataQueryBean) {

        String merchantNo = activityAndDataQueryBean.getMerchantNo();

        Map<String,Object> order = new HashMap<>();
        order.put("merchant_no" ,merchantNo);

        BigDecimal merTrans = activityDataService.queryMerchantTransTotal( order,"2" );
        return merTrans;
    }

    public int newMerchantRewardAmountMerCount(String type, ActivityAndDataQueryBean activityAndDataQueryBean) {
        Map<String,Object> countMap =  activityDataDao.newMerchantRewardAmountMerCount(type,activityAndDataQueryBean);
        return Integer.parseInt(StringUtils.ifEmptyThen(countMap.get("activityMerCount"),"0"));
    }
}
