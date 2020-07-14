package com.esdemo.modules.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.config.SpringHolder;
import com.esdemo.frame.enums.ActivityDataEnum;
import com.esdemo.frame.enums.ActivityEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.ActivityData;
import com.esdemo.modules.bean.ActivityAndDataQueryBean;
import com.esdemo.modules.bean.ActivityDataDetail;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.dao.ActivityDataDao;
import com.esdemo.modules.service.ActivityDataQueryService;
import com.esdemo.modules.service.ActivityDataService;
import com.esdemo.modules.service.AgentInfoService;
import com.esdemo.modules.service.SysDictService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ActivityDataServiceImpl implements ActivityDataService {

    @Resource
    private ActivityDataDao activityDataDao;


    @Resource
    private SysDictService sysDictService;

    @Resource
    private AgentInfoService agentInfoService;


    @Override
    public List<Map<String,Object>> agentActivityBySubType(String agentNo,String subType) {
        return  activityDataDao.agentActivityBySubType(agentNo,subType);
    }

    /**
     * 数据-活动详情-下拉选项
     * @param activityDataQueryBean
     * @return
     */
    @Override
    public ResponseBean activityDataTypeQuery(ActivityAndDataQueryBean activityDataQueryBean) {
        if(StringUtils.isEmpty(activityDataQueryBean.getAgentNo())){ //为空表示查询登录代理商
            activityDataQueryBean.setAgentNo(activityDataQueryBean.getUserInfoBean().getAgentNo());
            activityDataQueryBean.setAgentNode(activityDataQueryBean.getUserInfoBean().getAgentNode());
        }else { //非空表示手动选择了筛选条件
            AgentInfo agentInfo = agentInfoService.queryAgentInfo(activityDataQueryBean.getAgentNo());
            activityDataQueryBean.setAgentNode(agentInfo.getAgentNode());
        }
        List<Map<String,Object>> activityDataEnums = activityDataDao.activityMerCountConfig();
        Map<String,String> activityDataEnumMap =   activityDataEnums.stream().collect(Collectors.toMap(item -> item.get("activity_mer_count_subtype").toString() , item -> StringUtils.filterNull(item.get("sub_activity_title"))));

        //保存 所有的子活动
        Map<String,String> activityDataEnumTempMap = new HashMap<>();

        Map<String,Object> actDataQuery = new HashMap<>();
        //第一个下拉框（活动）
        List<Map<String,Object>> activitys = sysDictService.sysDicts("ACTIVITY_MER_COUNT_TYPE");

        Iterator<Map<String,Object>> iterator = activitys.iterator();
        while(iterator.hasNext()) {
            String activityDataType = StringUtils.filterNull(iterator.next().get("sys_value"));
            ActivityEnum act = EnumUtils.getEnum(ActivityEnum.class ,activityDataType);
            //第二个下拉框 子类型
            List<Map<String,Object>> agentActsType = new ArrayList<>();
            String subType = act.getActivitySubType();
            List<Map<String,Object>> agentActivities = agentActivityBySubType(activityDataQueryBean.getAgentNo(),subType);
            if(agentActivities == null || agentActivities.size() == 0){
                iterator.remove();
                continue;
            }

            agentActivities.stream().forEach(agentAct -> {
                List<Map<String,Object>> activityDataEnumsTemp = new ArrayList<>();
                String actTypeNo = StringUtils.filterNull(agentAct.get("activity_type_no"));
                Map<String,Object> data = new HashMap<>();
                data.put("sys_name",agentAct.get("activity_type_name"));
                data.put("sys_value",actTypeNo);
                agentActsType.add(data);
                for(ActivityDataEnum activityDataEnum : ActivityDataEnum.values()){
                    //当前子活动如果关闭 则不统计子活动数据
                    if(StringUtils.isEmpty(activityDataEnumMap.get(activityDataEnum.getActivityDataType()))){
                        continue;
                    }

                    String actSureLink = activityDataEnum.getSureLink();
                    if(!StringUtils.isEmpty(actSureLink)){
                        String actLinkValue = StringUtils.filterNull(agentAct.get(actSureLink));
                        if(StringUtils.isEmpty(actLinkValue) || "0.00".equals(actLinkValue) || "0".equals(actLinkValue)){
                            continue;
                        }
                    }

                    //判断当前子类型活动是否包含当前子活动，包含则保存
                    String[] actLinks = activityDataEnum.getActivityDataLink();
                    //没有配置表示不需要检验
                    if(actLinks.length <= 0 ){
                        Map<String, Object> map = new HashMap<>();
                        map.put("sys_value", activityDataEnum.getActivityDataType());
                        map.put("sys_name", activityDataEnumMap.get(activityDataEnum.getActivityDataType()));
                        activityDataEnumsTemp.add(map); // 数据统计时使用
                        //返回给前端做筛选条件
                        activityDataEnumTempMap.put(activityDataEnum.getActivityDataType(), activityDataEnumMap.get(activityDataEnum.getActivityDataType()));
                    }else {
                        //配置了 则需要校验
                        for (String actLink : actLinks) {
                            String actLinkValue = StringUtils.filterNull(agentAct.get(actLink));
                            //值非空
                            if (!StringUtils.isEmpty(actLinkValue) && !"0.00".equals(actLinkValue)) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("sys_value", activityDataEnum.getActivityDataType());
                                map.put("sys_name", activityDataEnumMap.get(activityDataEnum.getActivityDataType()));
                                activityDataEnumsTemp.add(map); // 数据统计时使用
                                //返回给前端做筛选条件
                                activityDataEnumTempMap.put(activityDataEnum.getActivityDataType(), activityDataEnumMap.get(activityDataEnum.getActivityDataType()));
                                break;
                            }
                        }
                    }
                }
                //保存当前子类型下的子活动，活动数据统计会用到
                actDataQuery.put(actTypeNo ,activityDataEnumsTemp);
            } );
            actDataQuery.put(act.getActivityDataType() , agentActsType);
        }
        activityDataEnums.clear();

        //所有的子活动转成 list 保存
        activityDataEnumTempMap.keySet().stream().forEach(item -> {
            Map<String ,Object> map = new HashMap<>();
            map.put("sys_value",item);
            map.put("sys_name",activityDataEnumMap.get(item));
            activityDataEnums.add(map);
        });
        actDataQuery.put("activitys",activitys);
        actDataQuery.put("activityData" , activityDataEnums);
        return ResponseBean.success(actDataQuery);
    }


    /**
     * 当agentNo 字段为空表示查询当前登录代理商 则赋值下节点
     * 当agentNo 有值则表示查询的是输入的代理商编号
     *
     * @param activityDataQueryBean
     */
    public ActivityAndDataQueryBean dealAgentNo(ActivityAndDataQueryBean activityDataQueryBean){
        if(StringUtils.isEmpty(activityDataQueryBean.getAgentNo())){ //为空表示查询登录代理商
            activityDataQueryBean.setAgentNo(activityDataQueryBean.getUserInfoBean().getAgentNo());
            activityDataQueryBean.setAgentNode(activityDataQueryBean.getUserInfoBean().getAgentNode());
        }else { //非空表示手动选择了筛选条件
            AgentInfo agentInfo = agentInfoService.queryAgentInfo(activityDataQueryBean.getAgentNo());
            if(!agentInfo.getAgentNode().startsWith(activityDataQueryBean.getUserInfoBean().getAgentNode())){
                return null;
            }
            activityDataQueryBean.setAgentNode(agentInfo.getAgentNode());
        }
        return activityDataQueryBean;
    }
    /**
     * 活动数据统计入口
     * @param activityAndDataQueryBean
     * @return
     */
    @Override
    public ResponseBean activityDataCountQuery(ActivityAndDataQueryBean activityAndDataQueryBean) {

       final ActivityAndDataQueryBean activityDataQueryBean = dealAgentNo(activityAndDataQueryBean);
        if(activityDataQueryBean == null){
            return ResponseBean.error("查询失败！");
        }
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (ActivityEnum actEnum : ActivityEnum.values()) {
            Map<String,Object> actConfigs =activityMerCountConfig(actEnum.getActivityDataType());
            //当前活动的总开关未配置或者状态关闭 则不统计数据
            if(actConfigs == null){
                continue;
            }
            List<ActivityData> result = new ArrayList<>();
            Map<String,Object> resultMap = new HashMap<>();
            //查询活动数据统计配置，只查询状态打开的 需要排序显示
            List<Map<String,Object>> actMerCountConfig = activityMerCountSubConfig(actEnum.getActivityDataType());
            //过滤当前代理商活动，如果当前代理商没有参加活动 则把配置移除，（前端不展示对应活动）
            ResponseBean res = activityDataTypeQuery(activityDataQueryBean);
            List<Map<String,Object>> actCountConfig =(List<Map<String,Object>>)((Map<String,Object>)res.getData()).get("activityData");

            final Map actCountConfigMap =  actCountConfig.stream().collect(Collectors.toMap(item ->item.get("sys_value") , item -> item.get("sys_name")));

            Iterator<Map<String,Object>> iterator = actMerCountConfig.iterator();
            while (iterator.hasNext()){
                Map<String,Object> map = iterator.next();
                String activityDataEnumType = StringUtils.filterNull(map.get("activity_mer_count_subtype"));
                if(!actCountConfigMap.containsKey(activityDataEnumType)) {
                    iterator.remove();
                }
            }
            //过滤当前代理商活动，如果当前代理商没有参加活动 则把配置移除，（前端不展示对应活动）
            if(actMerCountConfig.size()<=0){
                continue;
            }

            String activity = sysDictService.sysDictByKeyAndValue("ACTIVITY_MER_COUNT_TYPE",actEnum.getActivityDataType()).getSysName();
            resultMap.put("activityName" ,activity);
            resultMap.put("subType" ,actEnum.getActivityDataType());


            //查询当前活动商户的总数
            activityAndDataQueryBean.setSubType(actEnum);
            int activtyMerCount = countActivityMerchantBySubType(activityAndDataQueryBean);
            resultMap.put("activtyMerCount",activtyMerCount);
            resultList.add(resultMap);
            //遍历活动配置，统计每个活动的数据
            actMerCountConfig.stream().forEach(item-> {
                //根据类型获取对应的统计类型枚举
                String activityDataEnumType = StringUtils.filterNull(item.get("activity_mer_count_subtype"));

                ActivityDataEnum activityDataEnum = EnumUtils.getEnum(ActivityDataEnum.class, activityDataEnumType);
                activityDataQueryBean.setActivityData(activityDataEnum);
                //通过不同类型调用不用的实现做数据统计
                ActivityData activityData = new ActivityData();
                activityData.setExamine(0);
                activityData.setNotStandard(0);
                activityData.setReachStandard(0);

                ActivityDataQueryService activityDataQueryService = SpringHolder.getBean(activityDataEnum.getQueryService());
                activityData = activityDataQueryService.activityDataCount(activityDataQueryBean, activtyMerCount ,activityData);

                activityData.setActivityDataName(StringUtils.filterNull(item.get("sub_activity_title")));
                activityData.setActivityData(activityDataEnumType);
                activityData.setActivity(activity);
                result.add(activityData);

            });
            resultMap.put("subActivity",result);
        }

        return ResponseBean.success(resultList);
    }

    /**
     * 获取活动商户统计配置
     * @return
     */
    @Override
    public List<Map<String, Object>> activityMerCountSubConfig(String activityMerCountType) {
       return activityDataDao.activityMerCountConfigByActType(activityMerCountType);
    }

    @Override
    public  Map<String, Object> activityMerCountConfig(String activityMerCountType) {
        //查询主配置是否打开、关闭时不查询活动数据
        return activityDataDao.activityMerCountConfigByStatus(activityMerCountType);
    }

    @Override
    public int countActivityMerchantBySubType(ActivityAndDataQueryBean activityAndDataQueryBean ) {
        Map<String,Object> countMap =  activityDataDao.countActivityMerchantBySubType(activityAndDataQueryBean);
        return Integer.parseInt(StringUtils.ifEmptyThen(countMap.get("activityMerCount"),"0"));
    }



    @Override
    public ResponseBean activityDataCountDetailQuery(ActivityAndDataQueryBean activityDataQueryBean) {

        ResponseBean result = ResponseBean.success();
       final ActivityAndDataQueryBean activityAndDataQueryBean = dealAgentNo(activityDataQueryBean);
        if(activityAndDataQueryBean == null){
            return ResponseBean.success();
        }

        if(activityAndDataQueryBean.getPageNo() == 1){
            activityAndDataQueryBean.setCountOrDetail("count");
            Map<String,Object> actMerCounts =  activityDataCountDetailCensus(activityAndDataQueryBean);
            result.setCount(Integer.parseInt(StringUtils.ifEmptyThen(actMerCounts.get("merAciCensus"),"0")));
        }
        activityAndDataQueryBean.setCountOrDetail("detail");
        List<Map<String,Object>> activityDataEnums = activityDataDao.activityMerCountConfig();
        //子活动标题（只查询状态打开的），状态关闭的 则不做数据展示
        Map<String,String> activityDataEnumMap =   activityDataEnums.stream().collect(Collectors.toMap(item -> StringUtils.filterNull(item.get("activity_mer_count_subtype")) , item -> StringUtils.filterNull(item.get("sub_activity_title"))));
        //该查询功能需要先查询商户列表，然后在生成每个商户的活动数据
        List<Map<String,Object>> actMerchants = new ArrayList<>();
        //筛选了子活动和未筛选走的方法不一样 ，未筛选走主表 ad  筛选了走具体的活动明细
        PageHelper.startPage(activityAndDataQueryBean.getPageNo(),activityAndDataQueryBean.getPageSize(),false);
        if(activityAndDataQueryBean.getActivityData() !=null){
            ActivityDataQueryService activityDataQueryService = SpringHolder.getBean(activityAndDataQueryBean.getActivityData().getQueryService());
            actMerchants = activityDataQueryService.activityDataMerchantList(activityAndDataQueryBean);
        }else {
            actMerchants = activityMerchants(activityAndDataQueryBean);
        }

        //当前 activityAndDataQueryBean 的代理商的所有的活动 子类型 和 子类型对应的子活动数据
        ResponseBean actTypes =  activityDataTypeQuery(activityAndDataQueryBean);
        Map<String ,Object> actTypesMap = (Map<String, Object>) actTypes.getData();
        if(actMerchants == null){
            return ResponseBean.success();
        }
        List<Map<String,Object>> transTypeDesc = sysDictService.sysDicts("XHLS_TRANS_TOTAL_TYPES");
        Map<String,String> transTypeDescMap =  transTypeDesc.stream().collect(Collectors.toMap(item -> StringUtils.filterNull(item.get("sys_value")),item -> StringUtils.filterNull(item.get("sys_name"))));
        //遍历商户列表查询商户活动数据详情
        actMerchants.stream().forEach(item ->{
            String actTypeNo = StringUtils.filterNull(item.get("activityTypeNo"));
            List<Map<String,Object>> actDatas = ( List<Map<String,Object>>)actTypesMap.get(actTypeNo);
            List<ActivityDataDetail> merchantActDatail = new ArrayList<>();
            //对应子类型的子活动配置如果为空 （后台配置开关关闭） 则 不统计商户活动数据
            if(actDatas == null){
                return;
            }
            actDatas.stream().forEach(actDatasItem -> {
                ActivityDataEnum activityDataEnum = EnumUtils.getEnum(ActivityDataEnum.class ,StringUtils.filterNull(actDatasItem.get("sys_value")));
                ActivityDataQueryService activityDataQueryService = SpringHolder.getBean(activityDataEnum.getQueryService());
                ActivityAndDataQueryBean actQueryBean = new ActivityAndDataQueryBean();
                actQueryBean.setActivityData(activityDataEnum);
                actQueryBean.setMerchantNo(StringUtils.filterNull(item.get("merchantNo")));
                //根据不同的统计类型调用不同的实现 枚举ActivityDataEnum
                ActivityDataDetail activityDataDetail = activityDataQueryService.activityDataCountDetail(actQueryBean); //根据不同的枚举调用不同的数据统计方法

                if(activityDataDetail!=null) {
                    activityDataDetail.setActivityTypeName(StringUtils.filterNull(item.get("activityTypeName")));//子类型名称
                    activityDataDetail.setActivityDataName(activityDataEnumMap.get(activityDataEnum.getActivityDataType())); //子活动标题
                    activityDataDetail.setSubType(activityAndDataQueryBean.getSubType());
                    activityDataDetail.setActivityData(activityDataEnum);
                    activityDataDetail.setTransTypeDesc(transTypeDescMap.get(activityDataDetail.getTransType()));
                    activityDataDetail.setTransTypeRemark(sysDictService.getDictSysValue("ACT_DATA_TRANS_REMARK"));

                    //组装返回数据
                    merchantActDatail.add(activityDataDetail);
                }
            });
            item.put("phone" ,StringUtils.mask4MobilePhone(StringUtils.filterNull(item.get("phone"))));
            item.put("merActDetail",merchantActDatail);
        });

        //数据响应给客户端
        result.setData(actMerchants);
        return result;
    }

    @Override
    public Map<String,Object> activityDataCountDetailCensus(ActivityAndDataQueryBean activityDataQueryBean) {
        final ActivityAndDataQueryBean activityAndDataQueryBean = dealAgentNo(activityDataQueryBean);
        if(activityAndDataQueryBean == null){
            return null;
        }

        List<Map<String,Object>> activityDataEnums = activityDataDao.activityMerCountConfig();
        //子活动标题（只查询状态打开的），状态关闭的 则不做数据展示
        Map<String,String> activityDataEnumMap =   activityDataEnums.stream().collect(Collectors.toMap(item -> StringUtils.filterNull(item.get("activity_mer_count_subtype")) , item -> StringUtils.filterNull(item.get("sub_activity_title"))));
        //该查询功能需要先查询商户列表，然后在生成每个商户的活动数据
       Map<String,Object> actMerchantsCencus = new HashMap<>();
        //筛选了子活动和未筛选走的方法不一样 ，未筛选走主表 ad  筛选了走具体的活动明细
        if(activityAndDataQueryBean.getActivityData() !=null){
            ActivityDataQueryService activityDataQueryService = SpringHolder.getBean(activityAndDataQueryBean.getActivityData().getQueryService());
            actMerchantsCencus = activityDataQueryService.activityDataMerchantListCensus(activityAndDataQueryBean);
        }else {
            actMerchantsCencus = activityMerchantsCensus(activityAndDataQueryBean);
        }


        return  actMerchantsCencus;
    }

    @Override
    public List<ActivityDataDetail> merActivityDataDetail(ActivityAndDataQueryBean activityDataQueryBean) {
        List<ActivityDataDetail> merchantActDatail = new ArrayList<>();
        try {
            final ActivityAndDataQueryBean activityAndDataQueryBean = dealAgentNo(activityDataQueryBean);



            for (ActivityEnum actEnum : ActivityEnum.values()) {
                Map<String, Object> actConfigs = activityMerCountConfig(actEnum.getActivityDataType());
                //当前活动的总开关未配置或者状态关闭 则不统计数据
                if (actConfigs == null) {
                    continue;
                }
                Map<String, Object> activityDetail = activityDataDao.findMerActivityDetail(activityAndDataQueryBean.getMerchantNo());
                if (activityDetail == null) {
                    return null;
                }
                String activityTypeNo = StringUtils.filterNull(activityDetail.get("activity_type_no"));
                //查询活动数据统计配置，只查询状态打开的 需要排序显示
                List<Map<String, Object>> actMerCountConfig = activityMerCountSubConfig(actEnum.getActivityDataType());
                //过滤当前代理商活动，如果当前代理商没有参加活动 则把配置移除，（前端不展示对应活动）
                ResponseBean res = activityDataTypeQuery(activityDataQueryBean);
                List<Map<String, Object>> actCountConfig = (List<Map<String, Object>>) ((Map<String, Object>) res.getData()).get(activityTypeNo);

                final Map actCountConfigMap = actCountConfig.stream().collect(Collectors.toMap(item -> item.get("sys_value"), item -> item.get("sys_name")));



                Iterator<Map<String, Object>> iterator = actMerCountConfig.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> map = iterator.next();
                    String activityDataEnumType = StringUtils.filterNull(map.get("activity_mer_count_subtype"));
                    if (!actCountConfigMap.containsKey(activityDataEnumType)) {
                        iterator.remove();
                    }
                }
                //过滤当前代理商活动，如果当前代理商没有参加活动 则把配置移除，（前端不展示对应活动）
                if (actMerCountConfig.size() <= 0) {
                    continue;
                }




                actMerCountConfig.stream().forEach(item -> {

                    ActivityDataEnum activityDataEnum = EnumUtils.getEnum(ActivityDataEnum.class, StringUtils.filterNull(item.get("activity_mer_count_subtype")));
                    ActivityDataQueryService activityDataQueryService = SpringHolder.getBean(activityDataEnum.getQueryService());
                    ActivityAndDataQueryBean actQueryBean = new ActivityAndDataQueryBean();
                    actQueryBean.setActivityData(activityDataEnum);
                    actQueryBean.setMerchantNo(StringUtils.filterNull(activityAndDataQueryBean.getMerchantNo()));
                    //根据不同的统计类型调用不同的实现 枚举ActivityDataEnum
                    ActivityDataDetail activityDataDetail = activityDataQueryService.activityDataCountDetail(actQueryBean); //根据不同的枚举调用不同的数据统计方法

                    if (activityDataDetail != null) {
                        activityDataDetail.setActivityDataName(StringUtils.filterNull(actCountConfigMap.get(activityDataEnum.getActivityDataType()))); //子活动标题
                        activityDataDetail.setActivityData(activityDataEnum);
                        if ("考核中".equals(activityDataDetail.getActivityDataStatus())) {
                            BigDecimal transCount = activityDataQueryService.countMerchantTrans(actQueryBean);

                            activityDataDetail.setTransAmount(transCount.add(activityDataDetail.getTransAmount() == null ? BigDecimal.ZERO : activityDataDetail.getTransAmount()));
                        }
                        //组装返回数据
                        merchantActDatail.add(activityDataDetail);
                    }

                });
            }
        } catch (Exception e) {
            log.error("==商户详情活动数据查询异常", e);
        }
        return merchantActDatail;
    }

    private Map<String, Object> activityMerchantsCensus(ActivityAndDataQueryBean activityAndDataQueryBean) {
        return  activityDataDao.activityMerchantsCensus(activityAndDataQueryBean);
    }

    /**
     * 默认获取活动商户列表
     * @param activityAndDataQueryBean
     * @return
     */
    public List<Map<String,Object>> activityMerchants(ActivityAndDataQueryBean activityAndDataQueryBean){
        return  activityDataDao.activityMerchants(activityAndDataQueryBean);
    }



    /**
     * 统计商户今日交易金额
     * @param order
     * @param type 统计交易表的金额,目的是给:1.xhlf_activity_order累计金额,2.xhlf_activity_merchant_order累计金额
     * 1.只统计所有已绑卡后的交易
     * 2.统计所有POS刷卡交易
     * 5.统计绑卡后所有pos刷卡交易量 （新增）
     * 6.统计绑定卡交易量及pos刷卡交易量（新增）（PS：第一周期绑定卡刷卡交易，后续所有pos刷卡）
     * PS:3,4：3.hlf_agent_reward_order扫码交易累计金额,4.hlf_agent_reward_order全部交易累计金额，目前只在银盛用到
     * @return
     */
    @Override
    public BigDecimal queryMerchantTransTotal(Map<String,Object> order, String type) {

        Date date = new Date();
        DateTime timeBegin = DateUtil.beginOfDay(date);
        DateTime timeEnd = DateUtil.endOfDay(date);
        order.put("transStartTime",timeBegin.toString());
        order.put("transEndTime",timeEnd.toString());

        String cycle = StringUtils.filterNull(order.get("current_cycle"));
        String merchant_no = StringUtils.filterNull(order.get("merchant_no"));
        BigDecimal merchantTodayTrans = null;
        Map<String,Object> addCreaditcardLog;
        switch (type) {
            case "1":
                merchantTodayTrans = activityDataDao.queryTransTotal(order);
                break;
            case "2":
                merchantTodayTrans = activityDataDao.queryMerchantTransTotal(order);
                break;
             case "2.2":
                merchantTodayTrans = activityDataDao.queryMerchantTransTotal2(order);
                break;
            case "5":
                //先查询出第一次绑卡时间
                addCreaditcardLog = activityDataDao.selectFirstMerchantCreditcard(merchant_no);
                if(addCreaditcardLog != null && addCreaditcardLog.get("create_time") != null) {
                    //如果绑卡时间大于交易统计开始时间，则以绑卡时间为交易统计开始时间
                    //绑卡时间2019-11-25 06:00:00 ，交易统计开始时间2019-11-25 00:00:00，则交易统计开始时间为2019-11-25 06:00:00

                    DateTime dateTime = DateUtil.parse(StringUtils.filterNull(addCreaditcardLog.get("create_time")));
                    if(timeBegin.compareTo(dateTime)<0) {
                        order.put("transStartTime",dateTime.toString());
                    }
                    //统计的所有交易必须在绑卡之后，如果找不到第一次绑卡时间（未绑卡），则交易量为0
                    merchantTodayTrans = activityDataDao.queryMerchantTransTotal(order);
                }
                break;
            case "6":
                //第一周期绑定卡刷卡交易

                if("1".equals(cycle)){
                    merchantTodayTrans = activityDataDao.queryTransTotal(order);
                } else {
                    //后续所有pos刷卡
                    merchantTodayTrans = activityDataDao.queryMerchantTransTotal(order);
                }
                break;
            case "7":
                //第一周期绑定信用卡后的所有信用卡刷卡交易,且交易卡未在被其他活动商户交易
                //xhlf_merchant_trans_card：新欢乐送商户交易卡信息表
                //not exists (select 1 from xhlf_merchant_trans_card card where card.card_no = '交易卡号' and card.merchant_no <> '商户号')

                if("1".equals(cycle)){
                    //先查询出第一次绑卡时间
                    addCreaditcardLog = activityDataDao.selectFirstMerchantCreditcard(merchant_no);
                    if(addCreaditcardLog == null || addCreaditcardLog.get("create_time") == null) {
                        break;
                    }
                    //如果绑卡时间大于交易统计开始时间，则以绑卡时间为交易统计开始时间
                    //绑卡时间2019-11-25 06:00:00 ，交易统计开始时间2019-11-25 06:00:00，则交易统计开始时间为2019-11-25 06:00:00
                    DateTime dateTime = DateUtil.parse(StringUtils.filterNull(addCreaditcardLog.get("create_time")));

                    if(timeBegin.compareTo(dateTime) < 0) {
                        order.put("transStartTime",dateTime.toString());
                    }
                    //根据商户号查询今天的交易list，且交易卡不在别人的xhlf_merchant_trans_card表里
                    List<Map<String,Object>> transList = activityDataDao.queryMerchantTransListType7(order);
                    if(transList == null || transList.isEmpty()) {
                        break;
                    }
                    merchantTodayTrans = BigDecimal.ZERO;
                    List<Map<String,Object>> xhlfMerchantTransCardList = new ArrayList<>();
                    Set<String> bankIdList = new HashSet<>();
                    for(Map<String,Object> itemTrans: transList) {
                        merchantTodayTrans = merchantTodayTrans.add( new BigDecimal(StringUtils.ifEmptyThen(itemTrans.get("trans_amount"),"0")));

                    }


                } else {
                    //后续所有信用卡pos刷卡
                    merchantTodayTrans = activityDataDao.queryMerchantCreditTransTotal(order);
                }
                break;
            default:
                break;
        }
        if(merchantTodayTrans == null) {
            merchantTodayTrans = BigDecimal.ZERO;
        }
        return merchantTodayTrans;

    }







}
