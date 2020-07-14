package com.esdemo.modules.controller;

import cn.hutool.core.map.MapUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.exception.AppException;
import com.esdemo.frame.utils.*;
import com.esdemo.frame.utils.md5.Md5;
import com.esdemo.frame.utils.redis.RedisUtils;
import com.esdemo.frame.utils.swagger.SwaggerNoteLmc;
import com.esdemo.modules.bean.AgentAccountBalance;
import com.esdemo.modules.bean.ServiceQuota;
import com.esdemo.modules.bean.ServiceRate;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.ProfitService;
import com.esdemo.modules.service.SysDictService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lmc
 * @date 2019/5/29 14:40
 */
@Slf4j
@Api(description = "收入模块")
@RestController
@RequestMapping("/profit")
public class ProfitController {
    @Resource
    ProfitService profitService;
    @Resource
    SysDictService sysDictService;
    /**
     * 该接口仅供旧版本使用 ，账户余额和 收入统计一个接口下发效率较低，
     * 新版本接口拆分为两个，余额  getProfitBalance   收入汇总  getProfitDay
     * @param userInfoBean
     * @return
     */
    @OldSwaggerDeveloped
    @ApiOperation(value = "查询我的收入列表", notes = SwaggerNoteLmc.GET_PROFIT_LIST)
    @PostMapping("/getProfitList")
    public ResponseBean getProfitList(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agent_no = userInfoBean.getAgentNo();
        String user_id = userInfoBean.getUserId();
        /*返回信息*/
        Map<String, Object> map = new HashMap<>();


        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //当月月首日期
        String month_time = now.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        //6个月前
        LocalDateTime m6 = now.minusMonths(6);
        //1个月前
        LocalDateTime m1 = now.minusMonths(1);
        //6月前月首日期
        LocalDateTime m6first = m6.with(TemporalAdjusters.firstDayOfMonth());
        //1个月前月末日期
        LocalDateTime m1last = m1.with(TemporalAdjusters.lastDayOfMonth());
        //格式化日期
        String half_year_start_time = m6first.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        String half_year_end_time = m1last.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));

        //取代理商级别跟节点
        Long agent_level =userInfoBean.getAgentLevel();
        String agent_node = userInfoBean.getAgentNode();

        //近6个月累计收入不包含当月
        String accumulated_income = profitService.getAccumulatedIncome(agent_no, half_year_start_time, half_year_end_time);
        String today_income = "0.00";
        try {
            String cacheKey = String.format("agentApi2:getProfitList:todayIncome:%s:%s:%s", agent_no, user_id,userInfoBean.getLoginToken());
            today_income = RedisUtils.get(cacheKey);
            log.info("从redis获取今日收入数据"+today_income);
            if (StringUtils.isEmpty(today_income)) {
                today_income = profitService.getTodayIncome(DataBundle.build()
                        .bind("agent_no", agent_no)
                        .bind("agent_level", agent_level)
                        //根据代理商节点，查询所有名下商户
                        .bind("agent_node", agent_node + "%"));
                try {
                    int ttl = 120;
                    String ttlStr = sysDictService.getSysDictValueByKey("PROFIT_DAY_REDIS");
                    if(StringUtils.isNotBlank(ttlStr)){
                        ttl = Integer.parseInt(ttlStr);
                    }
                    RedisUtils.set(cacheKey, today_income, ttl, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.info("公共数据放入缓存异常{}", e);
                }
            }
        } catch (Exception e) {
            log.error("从redis获取公共数据异常{}", e);
        }
        //本月收入
        String month_income = profitService.getMonthIncome(agent_no, month_time);

        //分润账户
        Map<String, Object> share_account_map = profitService.getShareAccount(agent_no);
        String share_account = "0.00";
        if(!CollectionUtils.isEmpty(share_account_map)){
            share_account = StringUtils.filterNull(share_account_map.get("available_balance"));
        }
        //活动补贴
        Map<String, Object> activity_subsidy_map = profitService.getActivitySubsidy(agent_no);
        String activity_subsidy = "0.00";
        if(!CollectionUtils.isEmpty(activity_subsidy_map)){
            activity_subsidy = StringUtils.filterNull(activity_subsidy_map.get("available_balance"));
        }

        map.put("accumulated_income", accumulated_income);
        map.put("today_income", today_income);
        map.put("month_income", month_income);
        map.put("share_account", share_account);
        map.put("activity_subsidy", activity_subsidy);
        //放入缓存
        return ResponseBean.success(map);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询我的今日收入", notes = SwaggerNoteLmc.GET_PROFIT_LIST)
    @PostMapping("/getProfitDay")
    public ResponseBean getProfitDay(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agent_no = userInfoBean.getAgentNo();
        String user_id = userInfoBean.getUserId();
        /*返回信息*/
        Map<String, Object> map = new HashMap<>();
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //取代理商级别跟节点
        Long agent_level =userInfoBean.getAgentLevel();
        String agent_node = userInfoBean.getAgentNode();

        //近6个月累计收入不包含当月
        String today_income = "0.00";
        try {
            String cacheKey = String.format("agentApi2:getProfitList:todayIncome:%s:%s:%s", agent_no, user_id,userInfoBean.getLoginToken());
            today_income = RedisUtils.get(cacheKey);
            log.info("从redis获取今日收入数据"+today_income);
            if (StringUtils.isEmpty(today_income)) {
                today_income = profitService.getTodayIncome(DataBundle.build()
                        .bind("agent_no", agent_no)
                        .bind("agent_level", agent_level)
                        //根据代理商节点，查询所有名下商户
                        .bind("agent_node", agent_node + "%"));
                try {
                    int ttl = 120;
                    String ttlStr = sysDictService.getSysDictValueByKey("PROFIT_DAY_REDIS");
                    if(StringUtils.isNotBlank(ttlStr)){
                        ttl = Integer.parseInt(ttlStr);
                    }
                    RedisUtils.set(cacheKey, today_income, ttl, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.info("公共数据放入缓存异常{}", e);
                }
            }
        } catch (Exception e) {
            log.error("从redis获取公共数据异常{}", e);
        }

        map.put("today_income", today_income);
        //放入缓存
        return ResponseBean.success(map);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询我的收入", notes = SwaggerNoteLmc.GET_PROFIT_LIST)
    @PostMapping("/getProfitBalance")
    public ResponseBean getProfitBalance(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agent_no = userInfoBean.getAgentNo();
        String user_id = userInfoBean.getUserId();
        /*返回信息*/
        Map<String, Object> map = new HashMap<>();


        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //当月月首日期
        String month_time = now.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        //6个月前
        LocalDateTime m6 = now.minusMonths(6);
        //1个月前
        LocalDateTime m1 = now.minusMonths(1);
        //6月前月首日期
        LocalDateTime m6first = m6.with(TemporalAdjusters.firstDayOfMonth());
        //1个月前月末日期
        LocalDateTime m1last = m1.with(TemporalAdjusters.lastDayOfMonth());
        //格式化日期
        String half_year_start_time = m6first.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        String half_year_end_time = m1last.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));


        //近6个月累计收入不包含当月
        String accumulated_income = profitService.getAccumulatedIncome(agent_no, half_year_start_time, half_year_end_time);
        //本月收入
        String month_income = profitService.getMonthIncome(agent_no, month_time);

        //分润账户
        Map<String, Object> share_account_map = profitService.getShareAccount(agent_no);
        String share_account = "0.00";
        if(!CollectionUtils.isEmpty(share_account_map)){
            share_account = StringUtils.filterNull(share_account_map.get("available_balance"));
        }
        //活动补贴
        Map<String, Object> activity_subsidy_map = profitService.getActivitySubsidy(agent_no);
        String activity_subsidy = "0.00";
        if(!CollectionUtils.isEmpty(activity_subsidy_map)){
            activity_subsidy = StringUtils.filterNull(activity_subsidy_map.get("available_balance"));
        }

        map.put("accumulated_income", accumulated_income);
        map.put("month_income", month_income);
        map.put("share_account", share_account);
        map.put("activity_subsidy", activity_subsidy);
        //放入缓存
        return ResponseBean.success(map);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询我的收入趋势", notes = SwaggerNoteLmc.GET_PROFIT_TENDENCY)
    @PostMapping("/getProfitTendency")
    public ResponseBean getProfitTendency(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String select_type = StringUtils.filterNull(params_map.get("select_type"));
        String agent_no = userInfoBean.getAgentNo();
        List<Map<String, Object>> list = null;

//        list = getProfitTendency(select_type, agent_no);
        list = getProfitTendencyGroupByTime(select_type, agent_no);

        return ResponseBean.success(list);
    }

    private List<Map<String, Object>> getProfitTendencyGroupByTime(String select_type, String agent_no) {
        //起始时间
        String start_time = null;
        //结束时间
        String end_time = null;
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //初始数据，默认值均为0
        Map initData = new HashMap();
        Period per = null;
        if (Objects.equals("1", select_type)) {
            LocalDateTime d7 = now.minusDays(7);
            LocalDateTime d1 = now.minusDays(1);
            start_time = d7.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
            end_time = d1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));

            per = Period.between(d7.toLocalDate(), d1.toLocalDate());
            int sub = per.getDays() + 1;
            for (int i = 0; i < sub; i++) {
                initData.put(d7.toLocalDate().plusDays(i).format(DateTimeFormatter.ofPattern("MM-dd")), 0.0);
            }
        }
        if (Objects.equals("2", select_type)) {
            //6月前月首日期
            LocalDateTime m6 = now.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
            //1个月前月末日期
            LocalDateTime m1 = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            start_time = m6.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
            end_time = m1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));

            per = Period.between(m6.toLocalDate(), m1.toLocalDate());
            int sub = per.getMonths() + 1;
            for (int i = 0; i < sub; i++) {
                initData.put(m6.toLocalDate().plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM")), 0.0);
            }
        }

        DataBundle data = DataBundle.build()
                .bind("select_type", select_type)
                .bind("agent_no", agent_no)
                .bind("start_time", start_time)
                .bind("end_time", end_time);
        //从库里查询数据
        List<Map<String, Object>> list = profitService.getProfitTendencyGroupByTime(data);
        //集合转字典
        Map list2Map = list.stream().collect(Collectors.toMap(it -> it.get("X"), it -> it.get("Y")));
        //将查询到的数据填充至初始数据中
        initData.putAll(list2Map);
        //将初始数据转为List结构
        list = (List<Map<String, Object>>) initData.keySet().stream()
                //按键升序排序,其实把initData的实现改成TreeMap就可以免除此排序,此次仅为了应用新功能而应用
                .sorted(Comparator.comparing(key -> StringUtils.filterNull(key)))
                .map((key) -> MapUtil.builder().put("X", key).put("Y", initData.get(key)).build())
                .collect(Collectors.toList());
        return list;
    }

    private ArrayList<Map<String, Object>> getProfitTendency(String select_type, String agent_no) {
        ArrayList<Map<String, Object>> list = new ArrayList();

        //坐标X轴数据量
        String[] xMax = null;
        //起始时间
        String[] start_time = null;
        //结束时间
        String[] end_time = null;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        if ("1".equals(select_type)) {
            //近七日
            //初始化横坐标
            xMax = new String[7];
            start_time = new String[7];
            end_time = new String[7];
            for (int i = 0; i < 7; i++) {
                c.add(Calendar.DAY_OF_YEAR, -1);
                Date m = c.getTime();
                xMax[i] = new SimpleDateFormat("MM-dd").format(m);
                start_time[i] = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(m);
                end_time[i] = new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(m);
            }
        } else if ("2".equals(select_type)) {
            //近半年
            //初始化横坐标
            xMax = new String[6];
            start_time = new String[6];
            end_time = new String[6];
            for (int i = 0; i < 6; i++) {
                c.add(Calendar.MONTH, -1);
                //设置默认日期为每月的最后一天
                c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date m = c.getTime();
                xMax[i] = new SimpleDateFormat("yyyy-MM").format(m);
                start_time[i] = new SimpleDateFormat("yyyy-MM-01 00:00:00").format(m);
                end_time[i] = new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(m);
            }
        }

        if (xMax != null) {
            //按顺序循环查询
            for (int j = xMax.length-1; j >= 0; j--) {
                Map<String, Object> map = new HashMap<>();
                DataBundle dataBundle = DataBundle.build()
                        .bind("select_type", select_type)
                        .bind("agent_no", agent_no)
                        .bind("start_time", start_time[j])
                        .bind("end_time", end_time[j]);
                String record_amount = profitService.getProfitTendency(dataBundle);
                map.put("X", xMax[j]);
                map.put("Y", record_amount);
                list.add(map);
            }
        }
        return list;
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "账户明细", notes = SwaggerNoteLmc.GET_PROFIT_DETAIL)
    @PostMapping("/getProfitDetail")
    public ResponseBean getProfitDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,  @RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        String agent_no = userInfoBean.getAgentNo();
        params_map.put("agent_no", agent_no);
        Map<String, Object> map = new HashMap<>();

        //设置分页信息，分别是当前页数和每页显示的总记录数
        PageHelper.startPage(pageNo, pageSize, false);
        List<Map<String, Object>> list = profitService.getProfitDetail(params_map);

        List<Map<String, Object>> result_list = new ArrayList<>();

        for(Map<String, Object> detail_map : list){
            //冻结
            if("freeze".equals(detail_map.get("debit_credit_side"))){
                detail_map.put("summary_info", "冻结");
            }
            //解冻
            if("unFreeze".equals(detail_map.get("debit_credit_side"))){
                detail_map.put("summary_info", "解冻");
            }
            result_list.add(detail_map);
        }

        BigDecimal bigdecimal_income = new BigDecimal("0.00");
        BigDecimal bigdecimal_outcome = new BigDecimal("0.00");

        //第一页查询总计信息
        if (pageNo == 1) {
            Map<String, Object> temp_map = profitService.getProfitCount(params_map);
            if (!CollectionUtils.isEmpty(temp_map)) {
                //收入
                bigdecimal_income = new BigDecimal(temp_map.get("credit").toString());
                //支出
                bigdecimal_outcome = new BigDecimal(temp_map.get("debit").toString());
            }
        }
        String income = bigdecimal_income.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
        String outcome = bigdecimal_outcome.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
        map.put("detail_list", result_list);
        map.put("income", income);
        map.put("outcome", outcome);
        return ResponseBean.success(map);
    }

    //@SignValidate(needSign = false)
    @OldSwaggerDeveloped
    @ApiOperation(value = "账户提现前客户端需要数据下发", notes = SwaggerNoteLmc.GET_PROFIT_PUB_DATA)
    @PostMapping("/getProfitPubData")
    public ResponseBean getProfitPubData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String select_type = StringUtils.filterNull(params_map.get("select_type"));
        String agent_no = userInfoBean.getAgentNo();
        Map<String,Object> defaultMap = profitService.selectDefaultStatus();
        String default_status = defaultMap.get("status").toString();//默认总开关的状态
        log.info("账户控制总开关状态为: " + default_status);
        //留存金额
        String retain_amount = "0.00";
        String balance = "0.00";
        String available_balance = "0.00";
        String freeze_amount = "0.00"; // 冻结金额
        String pre_freeze_amount = "0.00"; // 预冻结金额
        //只有活动补贴才有留存金额之说
        if("2".equals(select_type)) {
            Map<String, Object> activity_subsidy_map = profitService.getActivitySubsidy(agent_no);
            if (!CollectionUtils.isEmpty(activity_subsidy_map)) {
                balance = StringUtils.filterNull(activity_subsidy_map.get("balance"));
                available_balance = StringUtils.filterNull(activity_subsidy_map.get("available_balance"));
                freeze_amount = StringUtils.filterNull(activity_subsidy_map.get("freeze_amount"));
            }
            // 活动补贴预冻结金额
            Map<String, Object> pre_freeze_map = profitService.getPreFreezeInActivitySubSidy(agent_no);
            if (!CollectionUtils.isEmpty(pre_freeze_map)) {
                pre_freeze_amount = StringUtils.filterNull(pre_freeze_map.get("pre_freeze_amount"));
            }
            BigDecimal bigBalance = new BigDecimal(balance).subtract(new BigDecimal(pre_freeze_amount));
            balance = bigBalance.compareTo(new BigDecimal(0)) == -1 ? "0.00" : bigBalance.toString();

            if (1 == userInfoBean.getAgentLevel()) {
                if ("1".equals(default_status)) {
                    Map<String, Object> map = profitService.selectRetainAmount(agent_no);
                    if (map != null) {
                        if ("1".equals(map.get("status").toString())) {
                            retain_amount = map.get("retain_amount").toString();
                        } else {
                            default_status = "0";
                        }
                    } else {
                        retain_amount = defaultMap.get("retain_amount").toString();
                    }
                }
            } else {
                //不是一级代理商,没有留存金额的概念,默认不显示留存金额
                default_status = "0";
                retain_amount = "0.00";
            }
        } else {
            Map<String, Object> share_account_map = profitService.getShareAccount(agent_no);
            if (!CollectionUtils.isEmpty(share_account_map)){
                balance = StringUtils.filterNull(share_account_map.get("balance"));
                available_balance = StringUtils.filterNull(share_account_map.get("available_balance"));
                freeze_amount = StringUtils.filterNull(share_account_map.get("freeze_amount"));
            }
            // 分润预冻结金额
            Map<String, Object> pre_freeze_map = profitService.getPreFreezeInShareAccount(agent_no);
            if (!CollectionUtils.isEmpty(pre_freeze_map)) {
                pre_freeze_amount = StringUtils.filterNull(pre_freeze_map.get("pre_freeze_amount"));
            }
            BigDecimal bigBalance = new BigDecimal(balance).subtract(new BigDecimal(pre_freeze_amount));
            balance = bigBalance.compareTo(new BigDecimal(0)) == -1 ? "0.00" : bigBalance.toString();

        }
        String serviceId = WebUtils.getDictValue("ACCOUNT_FEE_5");
        ServiceRate serviceRate = null;
        if (StringUtils.isNotBlank(serviceId)) {
            serviceRate = profitService.getFristAgentServiceRateById(serviceId);
        }else {
            return ResponseBean.error("ACCOUNT_FEE_5字典值没有配置");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("default_status", default_status);
        map.put("retain_amount", retain_amount);
        map.put("available_balance", available_balance);
        map.put("balance", balance);
        map.put("freeze_amount", freeze_amount);
        map.put("pre_freeze_amount", pre_freeze_amount);

        //服务费率各种字段
        map.put("rate_type", StringUtils.filterNull(serviceRate.getRateType()));
        map.put("single_num_amount", StringUtils.filterNull(serviceRate.getSingleNumAmount()));
        map.put("rate", StringUtils.filterNull(serviceRate.getRate()));
        map.put("capping", StringUtils.filterNull(serviceRate.getCapping()));
        map.put("safe_line", StringUtils.filterNull(serviceRate.getSafeLine()));
        map.put("ladder1_rate", StringUtils.filterNull(serviceRate.getLadder1Rate()));
        map.put("Ladder1_Max", StringUtils.filterNull(serviceRate.getLadder1Max()));
        map.put("ladder2_rate", StringUtils.filterNull(serviceRate.getLadder2Rate()));
        map.put("Ladder2_Max", StringUtils.filterNull(serviceRate.getLadder2Max()));
        map.put("ladder3_rate", StringUtils.filterNull(serviceRate.getLadder3Rate()));
        map.put("Ladder3_Max", StringUtils.filterNull(serviceRate.getLadder3Max()));
        map.put("ladder4_rate", StringUtils.filterNull(serviceRate.getLadder4Rate()));
        map.put("Ladder4_Max", StringUtils.filterNull(serviceRate.getLadder4Max()));

        return ResponseBean.success(map);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "校验资金密码", notes = SwaggerNoteLmc.HANDLER_CASH_PASSWORD)
    @PostMapping("/handlerCashPassword")
    public ResponseBean handlerCashPassword(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String password = StringUtils.filterNull(params_map.get("password"));
        if (StringUtils.isBlank(password)) {
            return ResponseBean.error("资金密码有误，请重新输入");
        }
        String decr_password = RSAUtils.decryptDataOnJava(password, Constants.LOGIN_PRIVATE_KEY);
        String md5_password = Md5.md5Str(decr_password + "{" + userInfoBean.getAgentNo() + "}");
        if(!md5_password.equals(profitService.getSafePassword(userInfoBean.getAgentNo()))){
            return ResponseBean.error("资金密码有误，请重新输入");
        }
        return ResponseBean.success(null, "校验成功");
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "账户提现", notes = SwaggerNoteLmc.WITHDRAW_DEPOSIT)
    @PostMapping("/withdrawDeposit")
    public ResponseBean withdrawDeposit(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String select_type = StringUtils.filterNull(params_map.get("select_type"));
        String money = StringUtils.filterNull(params_map.get("money"));
        String password = StringUtils.filterNull(params_map.get("password"));

        if (StringUtils.isBlank(password)) {
            return ResponseBean.error("资金密码有误，请重新输入");
        }
        String decr_password = RSAUtils.decryptDataOnJava(password, Constants.LOGIN_PRIVATE_KEY);
        String md5_password = Md5.md5Str(decr_password + "{" + userInfoBean.getAgentNo() + "}");
        if(!md5_password.equals(profitService.getSafePassword(userInfoBean.getAgentNo()))){
            return ResponseBean.error("资金密码有误，请重新输入");
        }
        // 1-分润账户提现，2-活动补贴提现
        if ("1".equals(select_type)) {
            log.info("start : 账户提现");
            String accountant_share_accounting = WebUtils.getDictValue("ACCOUNTANT_SHARE_ACCOUNTING");
            if("1".equalsIgnoreCase(accountant_share_accounting)){
                return ResponseBean.error("财务正在进行分润入账，请稍后再来提现");
            }else{
                try {
                    String agentNo = userInfoBean.getAgentNo();
                    String userId = userInfoBean.getUserId();
                    String serviceType = "8";//提现类型对应的出款服务类型
                    checkBalance(serviceType);
                    withdrawCash(agentNo, userInfoBean.getAgentNode(), money, userId);
                }catch (AppException e){
                    log.error("异常信息 : " + e.getMessage());
                    return ResponseBean.error(e.getMessage());
                }catch (Exception e){
                    log.error("服务器异常,异常信息 : " + e.getMessage());
                    return ResponseBean.error("提现失败，请稍后再试");
                }
            }
            log.info("end : 账户提现");
        }

        // 1-分润账户提现，2-活动补贴提现
        if ("2".equals(select_type)) {
            log.info("start : 活动补贴提现");
            BigDecimal bigDecimal = new BigDecimal(money);
            String accountant_share_accounting = WebUtils.getDictValue("ACCOUNTANT_SHARE_ACCOUNTING");
            log.info("活动余额提现开关，值:" + accountant_share_accounting);
            String happyTixianSwitch = WebUtils.getDictValue("HAPPY_TIXIAN_SWITCH");//欢乐返提现开关

            //各种校验
            if ("1".equals(happyTixianSwitch)) {
                return ResponseBean.error("系统正在执行入账，请稍后再试！");
            }

            if("1".equalsIgnoreCase(accountant_share_accounting)) {
                return ResponseBean.error("财务正在进行分润入账，请稍后再来提现");
            }

            ServiceQuota serviceQuota = profitService.queryHlsServiceQuota();
            if (serviceQuota == null){
                return ResponseBean.error("提现服务限额为空");
            }

            if (bigDecimal.compareTo(serviceQuota.getSingleMinAmount()) < 0) {
                return ResponseBean.error("最小提现金额不能小于" + serviceQuota.getSingleMinAmount() + "元.");
            }

            if (bigDecimal.compareTo(serviceQuota.getSingleCountAmount()) > 0){
                return ResponseBean.error("输入的提现金额须小于或等于最大交易金额 "+serviceQuota.getSingleCountAmount()+"元,请重新输入!");
            }
            if (!profitService.canWithdrawCash(serviceQuota.getServiceId())){
                ResponseBean.error("当前时段不能进行提现操作.");
            }
            try {
                String settleType = "2";
                String settleUserType = "A";
                String settleUserNo = userInfoBean.getAgentNo();;
                String agentNode = userInfoBean.getAgentNode();
                String holidaysMark = "0";
                String acqenname = "neweptok";//出款通道
                String subType = "4"; //出款子类型
                String serviceType = "10";//提现类型对应的出款服务类型
                checkBalance(serviceType);

                String retain_amount = "0.00";
                Map<String,Object> defaultMap = profitService.selectDefaultStatus();
                String default_status = defaultMap.get("status").toString();//默认总开关的状态
                if (1 == userInfoBean.getAgentLevel()) {
                    if ("1".equals(default_status)) {
                        Map<String, Object> map = profitService.selectRetainAmount(userInfoBean.getAgentNo());
                        if(!CollectionUtils.isEmpty(map)){
                            if ("1".equals(map.get("status").toString())) {
                                retain_amount = map.get("retain_amount").toString();
                            }
                        } else {
                            retain_amount = defaultMap.get("retain_amount").toString();
                        }
                    }
                }

                Map<String, Object> retain_amount_map = profitService.selectRetainAmount(userInfoBean.getAgentNo());
                if(!CollectionUtils.isEmpty(retain_amount_map)){
                    retain_amount = StringUtils.filterNull(retain_amount_map.get("retain_amount"));
                }

                String available_balance = "0.00";
                Map<String, Object> activity_subsidy_map = profitService.getActivitySubsidy(userInfoBean.getAgentNo());
                if(!CollectionUtils.isEmpty(activity_subsidy_map)){
                    available_balance = StringUtils.filterNull(activity_subsidy_map.get("available_balance"));
                }

                if("".equals(available_balance) || new BigDecimal(available_balance).compareTo(BigDecimal.ZERO) == 0 ) {
                    return ResponseBean.error("账户无余额");
                }

                if(new BigDecimal(money).compareTo(new BigDecimal(available_balance).subtract(new BigDecimal(retain_amount))) > 0){
                    return ResponseBean.error("提现金额大于余额");
                }

                String createUser = userInfoBean.getUserId();
                //判断该代理商是否存在已经提交提现的
                Map<String, Object> withDrawCash = profitService.findWithDrawCash(userInfoBean.getAgentNo(), subType);
                if (withDrawCash != null){
                    return ResponseBean.error("存在已提交的提现订单");
                }
                final Map<String, Object> map = new HashMap<>();
                map.put("createTime", new Date());//创建时间
                map.put("sourceSystem", "agentapp");//来源系统 agentweb交易系统 account账户系统  boss运营系统',
                map.put("settleStatus", "0");
                map.put("synStatus", "1");
                map.put("settleOrderStatus", "1");
                map.put("settleType", settleType);//结算类型 1T0交易；2手工提现；3T1线上代付；4T1线下代付',
                map.put("createUser", createUser);//创建人
                map.put("settleUserType", settleUserType);//用户类型M代表商户  A代表代理商
                map.put("settleUserNo", settleUserNo);//用户号
                map.put("settleAmount", money);//金额
                map.put("agentNode", agentNode);//代理商节点
                map.put("holidaysMark", holidaysMark);//假日标志:1-只工作日，2-只节假日，0-不限
                map.put("acqenname", acqenname);//出款通道,银盛
                map.put("subType", subType);//提现类型(1:手刷,2:实体商户,3：欢乐送商户,4:欢乐送代理商,5:账户余额提现)
                int i = profitService.insertWithDrawCash(map);
                if (i <= 0) {
                    return ResponseBean.error("活动补贴提现提交失败");
                }
                new Thread(()->{
                    try {
                        //调用晓明出款接口
                        Thread.sleep(100);
                        ClientInterface.transfer(StringUtils.filterNull(map.get("settle_order")), "2", "2");
                    } catch (InterruptedException e) {
                        log.info("出款异常{}", e);
                    }
                }).start();

            } catch (Exception e) {
                log.error("活动补贴提现失败====>" + e.getMessage());
                return ResponseBean.error("活动补贴提现失败异常");
            }
            log.info("end : 活动补贴提现");
        }

        return ResponseBean.success(null, "提现成功");
    }

    /**
     加限制,上游余额大于等于设置的剩余提现额度值
     */
    private void checkBalance(String serviceType) {
        if ("1".equals(WebUtils.getDictValue("AGENT_WITHDRAW_SWITCH"))) {
            String balance = WebUtils.getDictValue("AGENT_WITHDRAW_BALANCE");//设置的剩余额度值
            List<Map<String, Object>> list = profitService.selectByServiceType(serviceType);
            if (list.size() < 1) {
                log.info("===查询到可用通道条数 :+ "+ list.size() +" 条====");
                throw new AppException("系统繁忙，请稍后重试");
            }
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                BigDecimal userBalance = new BigDecimal(map.get("user_balance").toString());//user_balance 平台在上游余额
                if (userBalance.compareTo(new BigDecimal(balance)) == -1 && i == list.size()-1) {//大于1,等于0,小于-1,而且是最后一个,就提示
                    profitService.updateWithdrawSwitch(Integer.valueOf(map.get("id").toString()));
                    log.info("====最后一条通道,上游可用余额为: " + userBalance + "元 小于 boss设置的剩余额度值:"+ balance + " =====");
                    throw new AppException("系统繁忙，请稍后重试");//若所有服务“上游账户余额”都低于设置的剩余额度值，则提示代理商：系统繁忙，请稍后重试
                }else if(userBalance.compareTo(new BigDecimal(balance)) == -1){
                    //海涛,国栋,水育确认上游金额小于设置金额,自动关闭通道开关,然后账务手动开启
                    log.info("====上游可用余额为: " + userBalance + "元 小于 boss设置的剩余额度值:"+ balance + " =====");
                    profitService.updateWithdrawSwitch(Integer.valueOf(map.get("id").toString()));
                    continue;
                }else if(userBalance.compareTo(new BigDecimal(balance)) != -1){
                    log.info("====上游可用余额为: " + userBalance + "元 , boss设置的剩余额度值:"+ balance + " ====执行正常提现=====");
                    break;
                }
            }
        }
    }

    public void withdrawCash(String agentNo,String agentNode, String money, String userId) {
        String accountant_share_accounting = WebUtils.getDictValue("ACCOUNTANT_SHARE_ACCOUNTING");
        if("1".equalsIgnoreCase(accountant_share_accounting)){
            throw new AppException("财务正在进行分润入账，请稍后再来提现");
        }else{
            String subType = "5";
            AgentAccountBalance withdrawCashInfo = getWithdrawCashInfo(agentNo);
            BigDecimal moneyNum = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
            ServiceQuota serviceQuota = profitService.queryHlsServiceQuota();
            if (serviceQuota == null){
                throw new AppException("提现服务限额为空.");
            }

            if (moneyNum.compareTo(serviceQuota.getSingleMinAmount()) < 0){
                throw new AppException("最小提现金额不能小于"+serviceQuota.getSingleMinAmount()+"元.");
            }
            if (moneyNum.compareTo(serviceQuota.getSingleCountAmount()) > 0){
                throw new AppException("输入的提现金额须小于或等于最大交易金额 "+serviceQuota.getSingleCountAmount()+"元,请重新输入!");
            }

            if (moneyNum.compareTo(new BigDecimal("0")) <= 0){
                throw new AppException("提现金额不能低于0元.");
            }

            if (moneyNum.compareTo(withdrawCashInfo.getAvaliBalance()) > 0){
                throw new AppException("提现余额大于可用余额.");
            }
            if (!profitService.canWithdrawCash(serviceQuota.getServiceId())){
                throw new AppException("当前时段不能进行提现操作.");
            }
            //判断该代理商是否存在已经提交提现的
            Map<String, Object> withDrawCash = profitService.findWithDrawCash(agentNo, subType);
            if (withDrawCash != null){
                throw new AppException("存在已提交的提现订单.");
            }
            final Map<String, Object> map=new HashMap<>();
            map.put("createTime", new Date());//创建时间
            map.put("settleType", "2");//结算类型 1T0交易；2手工提现；3T1线上代付；4T1线下代付',
            map.put("sourceSystem", "agentapp");//来源系统 agentweb交易系统 account账户系统  boss运营系统',
            map.put("createUser", userId);//创建人
            map.put("settleUserType", "A");//用户类型M代表商户  A代表代理商
            map.put("settleUserNo", agentNo);//用户号
            map.put("settleStatus", "0");
            map.put("synStatus", "1");
            map.put("settleOrderStatus", "1");
            map.put("settleAmount",money);//金额
            map.put("agentNode", agentNode);//代理商节点
            map.put("holidaysMark", "0");//假日标志:1-只工作日，2-只节假日，0-不限
            map.put("acqenname", "neweptok");//出款通道,银盛
            map.put("subType",subType);//提现类型(1:手刷,2:实体商户,3：欢乐送商户,4:欢乐送代理商,5:账户余额提现)
            int i = profitService.insertWithDrawCash(map);
            if (i <= 0){
                throw new AppException("提现提交失败");
            }else{
//            ClientInterface.transfer(StringUtils.filterNull(map.get("settle_order")), "2", "2");
                new Thread(()->{
                    try {
                        Thread.sleep(100);
                        //调用晓明出款接口
                        ClientInterface.transfer(StringUtils.filterNull(map.get("settle_order")), "2", "2");
                    } catch (Exception e) {
                        log.error("异常{}", e);
                    }
                }).start();
            }
        }
    }

    public AgentAccountBalance getWithdrawCashInfo(String agentNo) {
//        if (!isFristAgent(agentNo)){
//            throw new BaseException("该代理商不是一级代理商.");
//        }
//        AgentInfo agentInfo = agentDao.queryAgentInfoByNo(agentNo);
//        if (agentInfo.getHasAccount() == 0){
//            throw new BaseException("该代理商未开户");
//        }
        String result = ClientInterface.getAgentAccountBalance(agentNo);
        if (StringUtils.isBlank(result)){
            throw new AppException("查询余额接口失败");
        }
        AgentAccountBalance agentAccountBalance = GsonUtils.fromJson2Bean(result, AgentAccountBalance.class);
        if (!agentAccountBalance.isStatus()){
            log.error("账户余额提现,调用记账接口返回false,错误信息:" + agentAccountBalance.getMsg());
            throw new AppException("账户余额提现失败: " + agentAccountBalance.getMsg());
        }
        String serviceId = WebUtils.getDictValue("ACCOUNT_FEE_5");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(serviceId)){
            ServiceRate serviceRate = profitService.getFristAgentServiceRateById(serviceId);
            agentAccountBalance.setServiceRate(serviceRate);
        }
        return agentAccountBalance;
    }


    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        //过去七天
        c.setTime(new Date());
        c.add(Calendar.DATE, - 7);
        Date d = c.getTime();
        String day = format.format(d);
        System.out.println("过去七天："+day);

        //过去一月
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String mon = format.format(m);
        System.out.println("过去一个月："+mon);

        //过去三个月
        c.setTime(new Date());
        c.add(Calendar.MONTH, -3);
        Date m3 = c.getTime();
        String mon3 = format.format(m3);
        System.out.println("过去三个月："+mon3);

        //过去一年
        c.setTime(new Date());
        c.add(Calendar.YEAR, -1);
        Date y = c.getTime();
        String year = format.format(y);
        System.out.println("过去一年："+year);

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-01 00:00:00");

        System.out.println(format2.format(c.getTime()));
        System.out.println(format3.format(c.getTime()));

    }
}
