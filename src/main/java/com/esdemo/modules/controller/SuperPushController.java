package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.SwaggerNoteLmc;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.SuperPushService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lmc
 * @date 2019/6/6 10:29
 */
@Api(description = "超级推")
@RestController
@RequestMapping("/superPush")
public class SuperPushController {

    @Resource
    SuperPushService superPushService;

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询我的超级推收益", notes = SwaggerNoteLmc.OLD_SUPER_PUSH_LIST)
    @PostMapping("/getOldSuperPushList")
    public ResponseBean getOldSuperPushList(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {

        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String select_type = StringUtils.filterNull(params_map.get("select_type"));
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        String agent_no = userInfoBean.getAgentNo();
        String agent_node = userInfoBean.getAgentNode();
        params_map.put("agent_no", agent_no);
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        //"  - select_type: 1-交易收益，2-邀请好友奖励，必填，位于请求body中\n" +
        if("1".equals(select_type)){
            PageHelper.startPage(pageNo, pageSize, false);
            list= superPushService.getSuperPushShareRecord(params_map);
        }else {
            PageHelper.startPage(pageNo, pageSize, false);
            list= superPushService.getInvPriMerInfoRecord(params_map);
        }

        Map<String, Object> all_count_map = superPushService.getSuperPushShareCount(params_map);
        BigDecimal sum_share_amount = new BigDecimal(String.valueOf(all_count_map.get("sum_share_amount")==null ? "0.00" : all_count_map.get("sum_share_amount")));

        BigDecimal all_count_amount = sum_share_amount;
        BigDecimal sum_prizes_amount;//合计推荐有奖的数据

        if(1 == userInfoBean.getAgentLevel()){//如果是一级，则加上推荐有奖的数据
            params_map.put("agent_node", agent_node);
            Map<String, Object> allCount_inv = superPushService.countInvPriMerInfoRecord(params_map);//统计所有推荐有奖
            sum_prizes_amount = new BigDecimal(String.valueOf(allCount_inv.get("sum_prizes_amount")==null ? "0.00" : allCount_inv.get("sum_prizes_amount")));
            all_count_amount = sum_share_amount.add(sum_prizes_amount);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        params_map.put("start_time", date + " 00:00:00");
        params_map.put("end_time", date + " 23:59:59");
        Map<String, Object> today_count = superPushService.getSuperPushShareCount(params_map);
        sum_share_amount = new BigDecimal(String.valueOf(today_count.get("sum_share_amount")==null ? "0.00" : today_count.get("sum_share_amount")));
        BigDecimal today_count_amount = sum_share_amount;
        if(1 == userInfoBean.getAgentLevel()){//如果是一级，则加上推荐有奖的数据
            Map<String, Object> todayCount_inv = superPushService.countInvPriMerInfoRecord(params_map);//统计今日推荐有奖
            sum_prizes_amount = new BigDecimal(String.valueOf(todayCount_inv.get("sum_prizes_amount")==null ? "0.00" : todayCount_inv.get("sum_prizes_amount")));
            today_count_amount = sum_share_amount.add(sum_prizes_amount);
        }

        map.put("list", list);
        map.put("today_income", today_count_amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        map.put("accumulated_income", all_count_amount.setScale(2, BigDecimal.ROUND_HALF_UP));

        return ResponseBean.success(map);
    }
}
