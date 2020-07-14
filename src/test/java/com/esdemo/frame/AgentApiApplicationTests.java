package com.esdemo.frame;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.enums.OrderTransStatus;
import com.esdemo.frame.utils.ALiYunOssUtil;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.external.AccountApiEnum;
import com.esdemo.frame.utils.external.CoreApiEnum;
import com.esdemo.frame.utils.external.ExternalApiUtils;
import com.esdemo.frame.utils.external.FlowmoneyApiEnum;
import com.esdemo.frame.utils.redis.RedisUtils;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.KeyValueBean;
import com.esdemo.modules.dao.AcqMerchantDao;
import com.esdemo.modules.dao.MerchantDao;
import com.esdemo.modules.dao.SmsDao;
import com.esdemo.modules.dao.TransOrderDao;
import com.esdemo.modules.service.*;
import com.esdemo.modules.utils.ThreeAgentUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;

/**
 * 系统测试类
 */
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AgentApiApplication.class)
@SpringBootTest(properties = {"spring.profiles.active=dev"})
public class AgentApiApplicationTests {

    @Test
    public void redisTest() {
        AppDeviceInfo deviceInfo = new AppDeviceInfo();
        deviceInfo.setName("abc");
        deviceInfo.setAppName("asdsadsad");
        RedisUtils.set("deviceInfo", deviceInfo);
        AppDeviceInfo dbInfo = RedisUtils.get("deviceInfo");
        dbInfo.setName("123456");
        dbInfo.setAppName("0987654321");
        AppDeviceInfo dbInfo2 = RedisUtils.get("deviceInfo");
        System.out.println(JSONUtil.toJsonStr(dbInfo2));
        RedisUtils.del("dbInfo2");
    }

    @Test
    public void sysConfigTest() {
        System.out.println(WebUtils.getSysConfigValueByKey("PURSE_PAY_OUT_ACC_NO_NEWEPTOK"));
    }


    @Test
    public void signTest() {
        Map<String, String> bodyParams = new HashMap<>();
        String signSrc = WebUtils.buildSignSrc(false, bodyParams, "sign");
        System.out.println(signSrc);
    }


    @Test
    public void queryAgentInfoByAgentNodeOrAgentNoTest() {
        AgentEsService agentEsService = SpringHolder.getBean(AgentEsService.class);
        Map<String, Object> agentInfo = agentEsService.queryAgentInfoByAgentNodeOrAgentNo("0-1572-2113-");
        System.out.println(JSONUtil.toJsonStr(agentInfo));
    }

    @Test
    public void orderTransStatusTest() {
        System.out.println(OrderTransStatus.SUCCESS.getStatus());
        System.out.println(OrderTransStatus.getZhByStatus(OrderTransStatus.SUCCESS.getStatus()));
    }

    @Test
    public void dateUtilTest() {
        Date now = new Date();
        System.out.println(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    public void transOrderDaoTest() {
        TransOrderDao transOrderDao = SpringHolder.getBean(TransOrderDao.class);
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", "SK756779786131036468");
        List<Map<String, Object>> maps = transOrderDao.queryTransOrderByParams(params);
        System.out.println(maps.toString());

        Map<String, BigDecimal> profitMap = transOrderDao.queryAgentProfitByOrderNo("SK756779786131036468", new String[]{"profits_1", "profits_2", "profits_3", "profits_4"});
        System.out.println(profitMap);
    }

    @Test
    public void smsDaoTest() {
        SmsDao smsDao = SpringHolder.getBean(SmsDao.class);
        System.out.println(smsDao.getLatest5MinuteSmsCode("18998718665", "200010"));
        int count = smsDao.insertSmsCode("18603049008", "123456", "200010");
        System.out.println(count);
    }

    @Test
    public void java8MapTest() {
        MerchantDao merchantDao = SpringHolder.getBean(MerchantDao.class);
        Map<String, String> resultMap = new HashMap<>();
        List<Map<String, Object>> result = merchantDao.listTeamNameByAgentNo("1446");
        Optional.ofNullable(result)
                .orElse(new ArrayList<>())
                .forEach(item -> {
                    resultMap.put(Objects.toString(item.get("team_id")), Objects.toString(item.get("team_name")));
                });
        List<KeyValueBean> teamInfos = new ArrayList<>();
        Optional.ofNullable(resultMap)
                .orElse(new HashMap<>())
                .forEach((k, v) ->
                        teamInfos.add(new KeyValueBean(k, v))
                );
        System.out.println(teamInfos);
    }

    @Test
    public void testApi() {
        Arrays.stream(AccountApiEnum.values()).forEach(item -> System.out.println(ExternalApiUtils.getAccountPath(item)));
        Arrays.stream(CoreApiEnum.values()).forEach(item -> System.out.println(ExternalApiUtils.getCorePath(item)));
        Arrays.stream(FlowmoneyApiEnum.values()).forEach(item -> System.out.println(ExternalApiUtils.getFlowmoneyPath(item)));
    }

    @Test
    public void getAgentTeamsTest() {
        MerchantInfoService merchantInfoService = SpringHolder.getBean(MerchantInfoService.class);
        List<Map<String, Object>> teamMapList = merchantInfoService.getAgentTeams("1446", false);
        System.out.println(teamMapList.toString());
    }

    @Test
    public void updateMbpAndOrderEntryTeamByMerTest() throws Exception {
        EsDataMigrateService esDataMigrateService = SpringHolder.getBean(EsDataMigrateService.class);
//        esDataMigrateService.updateMbpAndOrderEntryTeamByMer("258121000031112", "100070-002");
        Thread.sleep(1000 * 500);
    }

    @Test
    public void aliYunGenUrlTest() {
        String imgUrl = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, "abcd_1584926408363_84085.jpg");
        System.out.println("海报来了：" + imgUrl);
    }

    @Test
    public void aliYunGenUrlTest2() {
        String agentNo = "1114669";
        AgentInfoService agentInfoService = SpringHolder.getBean(AgentInfoService.class);
        AgentInfo search = agentInfoService.queryAgentInfo(agentNo);
        System.out.println(search.getAgentNo() + "-------" + search.getAgentNode());
    }

    @Test
    public void censusTerminalCountTest() {
        String agentNode = "0-4606-";
        AcqMerchantDao acqMerchantDao = SpringHolder.getBean(AcqMerchantDao.class);
        long count = acqMerchantDao.censusTerminalCount(agentNode, "CHILDREN", false);
        System.out.println("-------" + count);
    }

    @Test
    public void sumTodayIncomeTest() {
        String agentNo = "4606";
        IncomeService incomeService = SpringHolder.getBean(IncomeService.class);
        BigDecimal count = incomeService.sumTodayTotalIncome(agentNo);
        System.out.println("-------" + count);
    }

    @Test
    public void threeAgentTest() {
        String agentNo = "23142";
        List<AgentInfo> resSet = new ArrayList<>();
        ThreeAgentUtils.getAllThreeChildAgentNos(agentNo, resSet);
        System.out.println("-------" + resSet.toString());
    }
}
