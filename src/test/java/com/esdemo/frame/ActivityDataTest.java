package com.esdemo.frame;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.enums.ActivityEnum;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.modules.bean.ActivityAndDataQueryBean;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.ActivityDataService;
import com.esdemo.modules.service.MerchantEsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AgentApiApplication.class)
@SpringBootTest(properties = {"spring.profiles.active=dev"})
@Slf4j
public class ActivityDataTest {
    @Resource
    private ActivityDataService activityDataService;

    @Test
    public void activityDataTypeQuery(){

        UserInfoBean u =  new UserInfoBean();
        u.setAgentNo( "107");
        ActivityAndDataQueryBean activityAndDataQueryBean = new ActivityAndDataQueryBean();
        activityAndDataQueryBean.setUserInfoBean(u);
        log.info(""+ JSONUtil.toJsonStr(activityDataService.activityDataTypeQuery(activityAndDataQueryBean)));

    }
    @Test
    public void activityDataCountQuery(){

        UserInfoBean u =  new UserInfoBean();
        u.setAgentNo( "107");
        u.setAgentNode( "0-107-");
        ActivityAndDataQueryBean adb = new ActivityAndDataQueryBean();
        adb.setUserInfoBean(u);
        adb.setQueryScope(QueryScope.ALL);
        log.info("活动"+ JSONUtil.toJsonStr(activityDataService.activityDataCountQuery(adb)));
//        String[] str = {"0","1","2"};
//        for (String s : str) {
//
////        adb.setMerchantNo("258121000000011");
////        adb.setPhone("13148758546"''  ‘’);
//            adb.setSubType(ActivityEnum.happyBack);
//            adb.setRewardStatus(s);
//            adb.setDeductionStatus(s);
//            for (ActivityDataEnum value : ActivityDataEnum.values()) {
//                adb = new ActivityAndDataQueryBean();
//                adb.setUserInfoBean(u);
//                adb.setQueryScope(QueryScope.ALL);
//                adb.setStartTime("2020-04-24 00:00:00");
//                adb.setEndTime("2020-04-25 00:00:00");
//                adb.setEndActivityTime("2020-04-25 00:00:00");
//                adb.setStartActivityTime("2020-04-24 00:00:00");
//                log.info(value.getActivityDataType()+"======="+ JSONUtil.toJsonStr(activityDataService.activityDataCountQuery(adb)));
//            }
//        }



    }

    @Test
    public void activityDataCountDetailQuery(){

        UserInfoBean u =  new UserInfoBean();
        u.setAgentNo( "107");
        u.setAgentNode( "0-107-");
        ActivityAndDataQueryBean adb = new ActivityAndDataQueryBean();
        adb.setUserInfoBean(u);
        adb.setQueryScope(QueryScope.ALL);
//        adb.setStartTime("2020-04-24 00:00:00");
//        adb.setEndTime("2020-04-25 00:00:00");
//        adb.setMerchantNo("258121000000011");
//        adb.setPhone("13359201013");
//        adb.setSubType(ActivityEnum.happyBack);
        adb.setSubType(ActivityEnum.newHappyGive);

        log.info("活动"+ JSONUtil.toJsonStr(activityDataService.activityDataCountDetailQuery(adb)));
        String[] str = {"0","1","2"};
//        for (String s : str) {
//
//            adb.setRewardStatus(s);
//            adb.setDeductionStatus(s);
//            for (ActivityDataEnum value : ActivityDataEnum.values()) {
////                adb.setEndActivityTime("2020-04-25 00:00:00");
////                adb.setStartActivityTime("2020-04-24 00:00:00");
//                log.info(value.getActivityDataType()+"======="+ JSONUtil.toJsonStr(activityDataService.activityDataCountDetailQuery(adb)));
//            }
//        }



    }


    @Test
    public void merActivityDataDetail(){

        UserInfoBean u =  new UserInfoBean();
        u.setAgentNo( "107");
        u.setAgentNode( "0-107-");
        ActivityAndDataQueryBean adb = new ActivityAndDataQueryBean();
        adb.setUserInfoBean(u);
        adb.setMerchantNo("458121003173003");

        log.info("活动"+ JSONUtil.toJsonStr(activityDataService.merActivityDataDetail(adb)));



    }

    @Resource
    MerchantEsService merchantEsService;


    @Test
    public void merchantDetail(){
        System.out.println(JSONUtil.toJsonStr(merchantEsService.getMerchantDetails("458121003173003","107")));
    }


}
