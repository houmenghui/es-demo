package com.esdemo.frame;

import com.esdemo.AgentApiApplication;
import com.esdemo.frame.enums.QueryScope;
import com.esdemo.modules.service.MerchantEsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-15 11:06
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AgentApiApplication.class)
public class TestMerchantEsService {

    @Resource
    private MerchantEsService merchantEsService;

    @Test
    public void test() {
        merchantEsService.queryMerchantEarlyWarning(QueryScope.ALL, "0-1446-", "1446");
    }
    @Test
    public void testCountByBusinessProduct(){
//        List<MerchantSumBean> merchantSumBeans = merchantEsService.statisMerchantByBusinessProduct("0-1446-", "200010", false);
//        System.out.println(GsonUtils.toJson(merchantSumBeans));
    }

    @Test
    public void queryMerchant() {
//        UserInfoBean userInfoBean = new UserInfoBean();
//        Tuple<List<MerchantEsResultBean>, Long> listLongTuple = MerchantSearchUtils.listMerchantInfo(null, PageRequest.of(0, 1000), "1446", "0-1446-");
//        System.out.println(GsonUtils.toJson(listLongTuple));
    }
    @Test
    public void queryMerchant2() {
//        UserInfoBean userInfoBean = new UserInfoBean();
//        userInfoBean.setAgentNo("1446");
//        userInfoBean.setAgentNode("0-1446-");
//        MerchantSearchBean searchBean = new MerchantSearchBean();
//        searchBean.setSortType(MerchantSearchBean.SortType.ALL_TRANS_ASC);
//        Tuple<List<MerchantEsResultBean>, Long> listLongTuple = MerchantSearchUtils.listMerchantInfo(searchBean, PageRequest.of(0, 1000), "1446", "0-1446-");
//        System.out.println(GsonUtils.toJson(listLongTuple));
    }
}
