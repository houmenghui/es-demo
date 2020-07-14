package com.esdemo.modules.service.impl;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.utils.ALiYunOssUtil;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.MerchantInfo;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.dao.SurveyOrderInfoDao;
import com.esdemo.modules.service.SurveyOrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tgh
 * @description 调单管理
 * @date 2019/5/16
 */
@Service
@Slf4j
public class SurveyOrderInfoServiceImpl implements SurveyOrderInfoService {

    @Resource
    private SurveyOrderInfoDao surveyOrderInfoDao;

    @Override
    public Map<String, Object> selectAddrees() {
        return surveyOrderInfoDao.selectAddrees();
    }

    @Override
    public List<Map<String,Object>> selectSurveyOrderInfoByOneAgent(String agentNo,String agentNode) {
        return surveyOrderInfoDao.selectSurveyOrderInfoByOneAgent(agentNo,agentNode);
    }

    @Override
    public List<Map<String,Object>> selectSurveyOrderInfo(String agentNo,String agentNode) {
        return surveyOrderInfoDao.selectSurveyOrderInfo(agentNo,agentNode);
    }

    @Override
    public List<Map<String, Object>> selectBySysKey(String sysKey) {
        if ("ORDER_DEAL_STATUS".equals(sysKey)) {
            List<Map<String, Object>> list = surveyOrderInfoDao.getDictList(sysKey);
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if ("9".equals(map.get("sys_value"))) {
                    list.remove(i);
                }
            }
            return list;
        }
        if ("REPLY_STATUS_CODE".equals(sysKey)) {
            List<Map<String, Object>> list = surveyOrderInfoDao.getDictList(sysKey);
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if ("6".equals(map.get("sys_value"))) {
                    list.remove(i);
                }
            }
            return list;
        }
        if ("PAY_METHOD_TYPE".equals(sysKey)) {
            List<Map<String, Object>> list = surveyOrderInfoDao.getDictList(sysKey);
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if ("INT".equals(map.get("sys_value"))) {
                    map.put("sys_name", "全部");
                }
            }
            return list;
        }
        if ("ORDER_SERVICE_CODE".equals(sysKey)) {
            List<Map<String, Object>> list = surveyOrderInfoDao.getDictList(sysKey);
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if ("3".equals(map.get("sys_value"))) {
                    list.remove(i);
                }
            }
            return list;
        }
        return surveyOrderInfoDao.getDictList(sysKey);
    }

    @Override
    public List<Map<String, Object>> selectSurveyOrderByConditions(Map<String, Object> params,UserInfoBean userInfoBean) {
        List<Map<String, Object>> list = surveyOrderInfoDao.selectSurveyOrderByConditions(params);
        if(list !=null && list.size() > 0){
            //判断商户跟当前登录代理商的关系
            for (Map<String, Object> map : list) {
                //代理商不能看到除调单处理状态为【已退回】外的调单备注信息;
                if (!"8".equals(map.get("deal_status").toString())) {
                    map.put("deal_remark","");
                }
                if ("old".equals(defualt(map.get("trans_order_database")))) {
                    if (map.get("trans_order_no") != null && map.get("trans_order_no") != "") {
                        Map<String,Object> resultMap = surveyOrderInfoDao.selectTransOrder(map.get("trans_order_no").toString());
                        getTransStatus(map, resultMap);
                    }
                }
                String merchantNo = map.get("merchant_no").toString();
                MerchantInfo merchantInfo = surveyOrderInfoDao.getMerchantByNo(merchantNo);
                //1.一级代理商可以查看,改
                //2.商户的代理商编号等于当前登录的,就是直属,可以查看,改
                String own_status = "0";
                String entityId = userInfoBean.getAgentNo();//当前登录代理商编号
                Long agentLevel = userInfoBean.getAgentLevel();//当前登录代理商级别
                if (entityId.equals(merchantInfo.getAgentNo())) {
                    own_status = "2";//直属
                }
                if(agentLevel == 1){
                    own_status = "1";//一级
                }
                map.put("own_status", own_status);
                //调单类型
                valueToName(map);
                String defualtString = defualt(map.get("deal_status_value"));
                if (StringUtils.isNotBlank(defualtString) && "1,2,3,4,5,6,7".contains(defualtString)) {
                    map.put("deal_status", "已处理");
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String, Object> selectSurveyOrderDetail(String orderNo, UserInfoBean userInfoBean) {
        Map<String,Object> result = new HashMap<>();
        String agentNo = userInfoBean.getAgentNo();
        Map<String, Object> surveyOrderDetailMap = surveyOrderInfoDao.selectSurveyOrderDetail(orderNo,userInfoBean.getAgentNode());
        if (surveyOrderDetailMap == null){
            log.info("没有查询到数据,orderNo为{}",orderNo);
            surveyOrderDetailMap = new HashMap<>();
            surveyOrderDetailMap.put("msg", "数据为空");
            return surveyOrderDetailMap;
        }
        boolean flag = false;
        String finalHaveLookNo = defualt(selectFinalHaveLookNo(orderNo));
        //如果是终态,且 final_have_look_no 字段没有包含当前登录代理商编号就更新 final_have_look_no
        //处理状态为：【持卡人承认交易 2】、【全部提供 3】、【逾期全部提供 6】的调单为终态
        String delStatus = defualt(surveyOrderDetailMap.get("deal_status"));
        String replyStatus = defualt(surveyOrderDetailMap.get("reply_status"));
        if (("2,3,6".contains(delStatus)) && (!finalHaveLookNo.contains(agentNo))){
            finalHaveLookNo = StringUtils.isBlank(finalHaveLookNo) ? agentNo : (finalHaveLookNo + "," + agentNo);
            Integer num = updateFinalHaveLookNo(finalHaveLookNo,orderNo);
            log.info("更新字段 final_have_look_no成功 === " + num + " ===条数据");
        }
        String replyRoleNo = selectReplyRoleNo(orderNo);
        if (agentNo.equals(replyRoleNo) && ("1,4".contains(replyStatus))){//回复的角色是当前登录代理商,回复状态是已提交或者是逾期提交
            flag = true;
        }
        //直属提交后,就是已提交,逾期提交两种状态,一级再登录应该显示立即回复或修改按钮,返回true
        AgentInfo entityInfo = surveyOrderInfoDao.queryAgentInfoByNo(agentNo);
        if ("1".equals(entityInfo.getAgentLevel()) &&
                ("1,4".contains(replyStatus) || (("2,5".contains(replyStatus)) && (("0,8".contains(delStatus)))))){
            flag = true;
        }

        Map<String,Object> replyDetailMap = selectReplyDetail(orderNo);
        log.info("=====调单详情结果 surveyOrderDetailMap====>" + surveyOrderDetailMap + "======");
        log.info("=====回复详情结果 replyDetail====>" + replyDetailMap + "======");
        if (surveyOrderDetailMap != null) {
            valueToName(surveyOrderDetailMap);
            AgentInfo info = surveyOrderInfoDao.selectByAgentNode(surveyOrderDetailMap.get("agent_node").toString());
            surveyOrderDetailMap.put("agent_name", info == null ? "" : info.getAgentName());
            String defualtString = defualt(surveyOrderDetailMap.get("deal_status_value"));
            if (StringUtils.isNotBlank(defualtString) && "1,2,3,4,5,6,7".contains(defualtString)) {
                surveyOrderDetailMap.put("deal_status", "已处理");
            }
            Object object = surveyOrderDetailMap.get("template_files_name");
            if (object != null && object != "") {
                String reply_files_name = object.toString();
                aliyunPictrueUrl(surveyOrderDetailMap, reply_files_name);
            }
            if ("old".equals(defualt(surveyOrderDetailMap.get("trans_order_database")))) {
                Map<String,Object> resultMap = selectTransOrder(defualt(surveyOrderDetailMap.get("trans_order_no")));
                getTransStatus(surveyOrderDetailMap, resultMap);
            }
            result.put("surveyOrderDetail", surveyOrderDetailMap);
        }
        if (replyDetailMap != null) {
            valueToName(replyDetailMap);
            String reply_result = defualt(replyDetailMap.get("reply_result"));
            List<Map<String, Object>> replyResultCodeList = selectBySysKey("ORDER_REPLY_RESULT");//回复结果
            for (Map<String, Object> reply_result_map : replyResultCodeList) {
                String value = reply_result_map.get("sys_value").toString();
                if (reply_result.equals(value)) {
                    replyDetailMap.put("reply_result",reply_result_map.get("sys_name"));
                    replyDetailMap.put("reply_result_value",value);
                }
            }
            Object object = replyDetailMap.get("reply_files_name");
            if (object != null && object != "") {
                aliyunPictrueUrl(replyDetailMap, object.toString());
            }
            String reply_record_count = "0";
            //根据当前登录代理商节点查询回复记录条数
            Integer count = selectReplyRecordCount(orderNo);
            if (count > 1) {
                reply_record_count = "1";
            }
            replyDetailMap.put("reply_record_count",reply_record_count);//是否有一条以上回复记录:1 >1条; 0 <= 1条
            replyDetailMap.put("province_city",replyDetailMap.get("province") + "-" + replyDetailMap.get("city"));
            result.put("replyDetail", replyDetailMap);
            result.put("hasReplyDetail", true);//ios需要,如果回复详情为空返回false
        }else{
            result.put("hasReplyDetail", false);//ios需要,如果回复详情为空返回false
        }
        result.put("flag",flag);//是否显示修改
        return result;
    }

    /**
     * 图片处理
     * @param surveyOrderDetailMap
     * @param reply_files_name
     */
    private void aliyunPictrueUrl(Map<String, Object> surveyOrderDetailMap, String reply_files_name) {
        List<Map<String,String>> picsList = new ArrayList<>();
        if (StringUtils.isNoneBlank(reply_files_name)) {
            String[] split = reply_files_name.split(",");
            for (int i = 0; i < split.length; i++) {
                Map<String,String> map = new HashMap<>();
                String pictureName = split[i];
                String aliYunUrl = ALiYunOssUtil.genUrl(Constants.ALIYUN_OSS_ATTCH_TUCKET, pictureName,new Date(new Date().getTime() + 36000000));
                log.info("图片上传地址====>" + aliYunUrl);
                map.put("pictureName",pictureName);
                map.put("aliYunUrl",aliYunUrl);
                picsList.add(map);
            }
        }
        surveyOrderDetailMap.put("picsList",picsList);
    }

    /**
     * 处理交易状态
     * @param surveyOrderDetailMap
     * @param resultMap
     */
    private void getTransStatus(Map<String, Object> surveyOrderDetailMap, Map<String, Object> resultMap) {
        List<Map<String, Object>> transStatusList = selectBySysKey("TRANS_STATUS");//交易状态
        for (Map<String, Object> map2 : transStatusList) {
            if (defualt(resultMap.get("trans_status")).equals(defualt(map2.get("sys_value")))) {
                surveyOrderDetailMap.put("trans_status", map2.get("sys_name"));
            }
        }
    }

    @Override
    public Map<String, Object> selectReplyDetail(String orderNo) {
        return surveyOrderInfoDao.selectReplyDetail(orderNo);
    }

    @Override
    public String selectReplyRoleNo(String orderNo) {
        return surveyOrderInfoDao.selectReplyRoleNo(orderNo);
    }

    @Override
    public String selectFinalHaveLookNo(String orderNo) {
        return surveyOrderInfoDao.selectFinalHaveLookNo(orderNo);
    }

    /**
     * 存在回复记录,并修改
     */
    private Integer update(Map<String, Object> paramsMap, String orderNo, AgentInfo entityInfo,
                           MerchantInfo merchantInfo, Integer count, String replyStatus,UserInfoBean userInfoBean) {
        Map<String, Object> m = selectReplyDetail(orderNo);
        if (m != null) {
            Map<String, Object> map = updateSurveyOrderInfo( orderNo, entityInfo, merchantInfo, count, replyStatus,userInfoBean);
            count += Integer.valueOf(map.get("count").toString());
            log.info("回复记录修改成功:" + count + "条数据");
            paramsMap.put("id",m.get("id").toString());
            count += surveyOrderInfoDao.updateReply(paramsMap);
//			count += updateSurveyOrderInfo( orderNo, entityInfo, merchantInfo, count, replyStatus);
        }
        return count;
    }

    /**
     * 新增回复记录,并修改调单状态
     */
    private Integer insert(Map<String, Object> paramsMap, String orderNo, AgentInfo entityInfo,
                           MerchantInfo merchantInfo, Integer count, String replyStatus, String dealStatu,
                           String replyStatu,UserInfoBean userInfoBean) {
        /**
         * 调单状态为【已回复即已确认2】，处理状态为：【持卡人承认交易 2】、【全部提供 3】、【逾期全部提供 6】的调单为终态，
         * 调单类型为此三个的调单不给代商回复按钮，处理状态为其他状态的调单，代理商可继续回复，回复后新增一条回复记录
         */
        //回复状态未提交0或已逾期3或(未提交并处理状态为已回退) 回复新增
        if (("0".equals(replyStatu) || "3".equals(replyStatu) || ("0".equals(replyStatu) && "8".equals(dealStatu)))
                || (("2".equals(replyStatus) || "5".equals(replyStatus))
                && ("1".equals(dealStatu) || "4".equals(dealStatu) || "5".equals(dealStatu) || "7".equals(dealStatu)))) {
            Map<String, Object> map = updateSurveyOrderInfo( orderNo, entityInfo, merchantInfo, count, replyStatus,userInfoBean);
            count += Integer.valueOf(map.get("count").toString());
            count += surveyOrderInfoDao.insertReply(paramsMap);
            log.info("新增回复记录时修改状态 count应该等于2,count = " + count + "条数据");
        }
        return count;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String, Object> insertOrUpdateReply(Map<String, Object> paramsMap, UserInfoBean userInfoBean) {
        Map<String, Object> map = new HashMap<>();
        String orderNo = paramsMap.get("order_no").toString();
        String entityId = paramsMap.get("agent_no").toString();
        Boolean continueCommit = (Boolean) paramsMap.get("continueCommit");
        paramsMap.put("reply_role_type", "A");//A:代理商 M:商户
        paramsMap.put("reply_role_no", userInfoBean.getAgentNo());//执行修改的代理商即当前登录代理商
        Map<String, Object> replyDetailMap = GsonUtils.fromJson2Map(paramsMap.get("replyDetail").toString(), Object.class);
        paramsMap.putAll(replyDetailMap);
        String provinceCity = paramsMap.get("province_city").toString();
        paramsMap.put("province", provinceCity != null ? provinceCity.substring(0,provinceCity.indexOf("-")) : "");
        paramsMap.put("city", provinceCity != null ? provinceCity.substring(provinceCity.indexOf("-") + 1,provinceCity.length()) : "");
        AgentInfo entityInfo = surveyOrderInfoDao.queryAgentInfoByNo(entityId);
        Map<String, Object> surveyOrder = surveyOrderInfoDao.selectSurveyOrderDetail(orderNo,entityInfo.getAgentNode());
        if (surveyOrder == null) {
            log.info("根据调单编号orderNo :" + orderNo + "没有查到数据");
            throw new RuntimeException("没有找到调单订单 " + orderNo);
        }
        String merchantNo = surveyOrder.get("merchant_no").toString();
        MerchantInfo merchantInfo = surveyOrderInfoDao.getMerchantByNo(merchantNo);
        //根据回复状态判断是一级确认还是所属代理商确认 1已提交，2已确认，5逾期确认
        String reply_status = surveyOrder.get("reply_status").toString();
        if ("1".equals(entityInfo.getAgentLevel()) && ("1,4".contains(reply_status) && !continueCommit)) {//确认提交continueCommit
            map.put("flag", true);
            map.put("msg", "所属代理商已回复,是否继续提交?");
            map.put("continueCommit", true);
            return map;
        }
        if (!"1".equals(entityInfo.getAgentLevel()) && (("1,4".contains(reply_status)
                //如果不是一级,而且最后一条回复记录的reply_role_no不是当前登录代理商编号
                && !(entityId.equals(selectReplyRoleNo(orderNo)))) || "2,5".contains(reply_status))) {
            map.put("flag", true);
            map.put("msg", "已有其他人更新了调单内容,请返回调单列表查看");
            map.put("continueCommit", false);
            return map;
        }
        //处理状态为：【持卡人承认交易 2】、【全部提供 3】、【逾期全部提供 6】的调单为终态
        String delStatus = surveyOrder.get("deal_status") == null ? "" : surveyOrder.get("deal_status").toString();
        if ("2,3,6".contains(delStatus)){//如果是终态就给出提示
            map.put("flag", true);
            map.put("msg", "提交失败,该调单已处理完毕");
            map.put("continueCommit", false);
            return map;
        }

        Integer count = 0;
        String replyStatus = "2";
        Map<String, Object> surveyOrderInfoMap = surveyOrderInfoDao.selectByOrderNo(orderNo);
        String dealStatu = surveyOrderInfoMap.get("deal_status").toString();//处理状态
        String replyStatu = surveyOrderInfoMap.get("reply_status").toString();//回复状态
        //1.一级代理商可以查看,改
        //2.商户的代理商编号等于当前登录的,就是直属,可以查看,改
        if ("1".equals(entityInfo.getAgentLevel())) {//一级
            //回复状态未提交0或已逾期3或(未提交并处理状态为已回退) 新增回复记录
            count = insert(paramsMap, orderNo, entityInfo, merchantInfo, count, replyStatus, dealStatu, replyStatu,userInfoBean);
            //只要一级回复了,调单状态就改成未处理 ("1".equals(dealStatu) || "4".equals(dealStatu) || "5".equals(dealStatu) || "7".equals(dealStatu)))
            if (("1".equals(dealStatu) || "4".equals(dealStatu) || "5".equals(dealStatu) || "7".equals(dealStatu)
                    || "8".equals(dealStatu))) {
                count += surveyOrderInfoDao.updateDealStatus(orderNo);
            }
            //回复状态 已提交或逾期提交或(已确认&&处理状态为未处理)或(逾期确认并处理状态为未处理)或(已确认并已回退)或(逾期确认并已回退) 修改回复记录
            if ("1".equals(replyStatu) || "4".equals(replyStatu) || ("2".equals(replyStatu) && "0".equals(dealStatu))
                    || (("5".equals(replyStatu) && "0".equals(dealStatu))) || ("2".equals(replyStatu) && "8".equals(dealStatu))
                    || (("5".equals(replyStatu) && "8".equals(dealStatu)))) {
                count = update(paramsMap, orderNo, entityInfo, merchantInfo, count, replyStatus,userInfoBean);
            }
        }else if(entityId.equals(merchantInfo.getAgentNo())){//直属
            //回复状态未提交0或已逾期3或(未提交并处理状态为已回退) 回复新增
            count += insert(paramsMap, orderNo, entityInfo, merchantInfo, count, replyStatus, dealStatu, replyStatu,userInfoBean);
            //回复状态 已提交或逾期提交   修改回复记录
            if ("1".equals(replyStatu) || "4".equals(replyStatu)) {
                count = update(paramsMap, orderNo, entityInfo, merchantInfo, count, replyStatus,userInfoBean);
            }
        }
        if (count > 0) {
            map.put("flag", true);
            map.put("msg", "提交成功");
            map.put("continueCommit", false);
            return map;
        }else{
            map.put("flag", false);
            map.put("msg", "提交失败");
            map.put("continueCommit", false);
            return map;
        }
    }


    private Map<String,Object> updateSurveyOrderInfo(String orderNo, AgentInfo entityInfo, MerchantInfo merchantInfo,
                                                     Integer count, String replyStatus,UserInfoBean userInfoBean){
        Map<String,Object> resultMap = new HashMap<String, Object>();
        String entityId = entityInfo.getAgentNo();
        Map<String,Object> map = surveyOrderInfoDao.selectForUpdate(Long.valueOf(surveyOrderInfoDao.selectByOrderNo(orderNo).get("id").toString()));
        if (map != null) {
            String reply_status = map.get("reply_status") == null ? "" : map.get("reply_status").toString();
            if ("1".equals(entityInfo.getAgentLevel()) && ("1,4".contains(reply_status))) {//确认提交continueCommit
                resultMap.put("flag", true);
                resultMap.put("msg", "所属代理商已回复,是否继续提交?");
            }
            if (!"1".equals(entityInfo.getAgentLevel()) && (("1,4".contains(reply_status)
                    //如果不是一级,而且最后一条回复记录的reply_role_no不是当前登录代理商编号
                    && !(entityId.equals(selectReplyRoleNo(orderNo)))) || "2,5".contains(reply_status))) {
                resultMap.put("flag", true);
                resultMap.put("msg", "已有其他人更新了调单内容,请返回调单列表查看");
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> detailMap = surveyOrderInfoDao.selectSurveyOrderDetail(orderNo,userInfoBean.getAgentNode());
        if (detailMap == null) {
            throw new RuntimeException("没有找到调单订单==>" + orderNo);
        }
        Date now = new Date();
        try {
            long reply_end_time = sdf.parse(detailMap.get("reply_end_time").toString()).getTime();
            log.info("====回复记录新增成功====:" + count + "条数据");
            if ("1".equals(entityInfo.getAgentLevel())) {//一级
                replyStatus = "2";
                if ( now.getTime() > reply_end_time) {
                    replyStatus = "5";//逾期确认
                }
            }
            if((!"1".equals(entityInfo.getAgentLevel())) && (entityInfo.getAgentNo()).equals(merchantInfo.getAgentNo())){//直属
                replyStatus = "1";
                if ( now.getTime() > reply_end_time) {
                    replyStatus = "4";//逾期提交
                }
            }
        } catch (Exception e) {
            log.error("异常{}", e);
        }
        count += surveyOrderInfoDao.updateSurveyOrderInfo(orderNo,replyStatus);
        resultMap.put("count", count);
        return resultMap;
    }

    @Override
    public List<Map<String, Object>> selectReplyRecord(String orderNo) {
        List<Map<String, Object>> list = surveyOrderInfoDao.selectReplyRecord(orderNo);
        if (list != null && list.size() > 0) {
            log.info("=====查询到 ==" + list.size() + "==条数据=====");
            for (Map<String, Object> map : list) {
                valueToName(map);
                List<Map<String, Object>> replyResultCodeList = selectBySysKey("ORDER_REPLY_RESULT");//回复结果
                for (Map<String, Object> reply_result_map : replyResultCodeList) {
                    String value = reply_result_map.get("sys_value").toString();
                    String reply_result = map.get("reply_result").toString();
                    if (reply_result.equals(value)) {
                        map.put("reply_result",reply_result_map.get("sys_name"));
                    }
                }
                Object object = map.get("reply_files_name");
                if (object != null && object != "") {
                    aliyunPictrueUrl(map,object.toString());
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (map.get("create_time") != null && map.get("create_time") != "") {
                    String format = dateFormat.format(map.get("create_time"));
                    map.put("createTime",format);
                }
                map.put("provinceCity",defualt(map.get("province")) + "-" + defualt(map.get("city")));
            }
        }
        return list;
    }

    @Override
    @Transactional
    @DataSourceSwitch(DataSourceType.WRITE)
    public Map<String, Object> updateReplyStatus(String entityId,String orderNo,UserInfoBean userInfoBean) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> map = new HashMap<>();
        AgentInfo entityInfo = surveyOrderInfoDao.queryAgentInfoByNo(entityId);
        if (!"1".equals(entityInfo.getAgentLevel())) {
            map.put("flag", false);
            map.put("msg", "当前登录代理商不是一级代理商");
            return map;
        }
        Map<String, Object> detailMap = surveyOrderInfoDao.selectSurveyOrderDetail(orderNo,userInfoBean.getAgentNode());
        if (detailMap == null) {
            throw new RuntimeException("没有找到调单订单 " + orderNo);
        }
        Date now = new Date();
        long reply_end_time;
        try {
            reply_end_time = sdf.parse(detailMap.get("reply_end_time").toString()).getTime();
            String replyStatus = "2";
            if ( now.getTime() > reply_end_time) {
                replyStatus = "5";//逾期确认
            }
            if (surveyOrderInfoDao.updateSurveyOrderInfoByOneAgent(orderNo, replyStatus) > 0){
                map.put("flag", true);
                map.put("msg", "提交成功");
            }
        } catch (ParseException e) {
            log.error("异常{}", e);
        }
        return map;
    }

    @Override
    public Integer selectReplyRecordCount(String orderNo) {
        return surveyOrderInfoDao.selectReplyRecordCount(orderNo);
    }

    @Override
    public Map<String, Object> selectTransOrder(String transOrderNo) {
        return surveyOrderInfoDao.selectTransOrder(transOrderNo);
    }

    @Override
    @DataSourceSwitch(DataSourceType.WRITE)
    public Integer updateFinalHaveLookNo(String finalHaveLookNo,String orderNo) {
        return surveyOrderInfoDao.updateFinalHaveLookNo(finalHaveLookNo,orderNo);
    }

    private String defualt(Object o){
        return o == null ? "" : o.toString();
    }

    private void valueToName(Map<String, Object> map) {
        List<Map<String, Object>> orderTypeCodeList = selectBySysKey("ORDER_TYPE_CODE");//调单类型
        List<Map<String, Object>> orderServiceCodeList = selectBySysKey("ORDER_SERVICE_CODE");//业务类型
        List<Map<String, Object>> replyStatusCodeList = selectBySysKey("REPLY_STATUS_CODE");//回复状态
//		List<Map<String, Object>> orderDealStatusList = selectBySysKey("ORDER_DEAL_STATUS");//处理状态
        List<Map<String, Object>> orderDealStatusList = orderDealStatusList();
        List<Map<String, Object>> payMethodTypeList = selectBySysKey("PAY_METHOD_TYPE");//交易方式
        List<Map<String, Object>> transStatusList = selectBySysKey("TRANS_STATUS");//交易状态
//		List<Map<String, Object>> replyResultCodeList = surveyOrderInfoService.selectBySysKey("REPLY_RESULT_CODE");//回复结果
        String order_type_code = map.get("order_type_code") != null ? map.get("order_type_code").toString() : "";
        String order_service_code = map.get("order_service_code") != null ? map.get("order_service_code").toString() : "";
        String reply_status = map.get("reply_status") != null ? map.get("reply_status").toString() : "";
        String deal_status = map.get("deal_status") != null ? map.get("deal_status").toString() : "";
        String pay_method = map.get("pay_method") != null ? map.get("pay_method").toString() : "";
        String trans_status = map.get("trans_status") != null ? map.get("trans_status").toString() : "";
        for (Map<String, Object> order_type_code_map : orderTypeCodeList) {
            String value = order_type_code_map.get("sys_value").toString();
            if (order_type_code.equals(value)) {
                map.put("order_type_code",order_type_code_map.get("sys_name"));
                map.put("order_type_code_value",value);
            }
        }
        for (Map<String, Object> order_service_code_map : orderServiceCodeList) {
            String value = order_service_code_map.get("sys_value").toString();
            if (order_service_code.equals(value)) {
                map.put("order_service_code",order_service_code_map.get("sys_name"));
                map.put("order_service_code_value",value);
            }
        }
        for (Map<String, Object> reply_status_map : replyStatusCodeList) {
            String value = reply_status_map.get("sys_value").toString();
            if (reply_status.equals(value)) {
                map.put("reply_status",reply_status_map.get("sys_name"));
                map.put("reply_status_value",value);
            }
        }
        for (Map<String, Object> deal_status_map : orderDealStatusList) {
            String value = deal_status_map.get("sys_value").toString();
            if (deal_status.equals(value)) {
                map.put("deal_status",deal_status_map.get("sys_name"));
                map.put("deal_status_value",value);
            }
            if (("1,2,3,4,5,6,7".contains(deal_status))) {
                map.put("deal_status","已处理");
                map.put("deal_status_value",deal_status);
            }
        }
        for (Map<String, Object> pay_method_map : payMethodTypeList) {
            String value = pay_method_map.get("sys_value").toString();
            if (pay_method.equals(value)) {
                map.put("pay_method",pay_method_map.get("sys_name"));
                map.put("pay_method_value",value);
            }
        }
        for (Map<String, Object> trans_status_map : transStatusList) {
            String value = trans_status_map.get("sys_value").toString();
            if (trans_status.equals(value)) {
                map.put("trans_status",trans_status_map.get("sys_name"));
                map.put("trans_status_value",value);
            }
        }
    }

    /**
     * 处理状态下拉列表
     * 要求客户端下拉列表只显示三种状态,约定为 1 可以查询所有已处理的状态 1234567
     * @return
     */
    @Override
    public List<Map<String, Object>> orderDealStatusList() {
        List<Map<String, Object>> orderDealStatusList = new ArrayList<>();//处理状态
        Map<String,Object> map1 = new HashMap<>();
        Map<String,Object> map2 = new HashMap<>();
        Map<String,Object> map3 = new HashMap<>();
        map1.put("sys_name", "未处理");
        map1.put("sys_value", "0");
        map2.put("sys_name", "已处理");
        map2.put("sys_value", "1");
        map3.put("sys_name", "已回退");
        map3.put("sys_value", "8");
        orderDealStatusList.add(map1);
        orderDealStatusList.add(map2);
        orderDealStatusList.add(map3);
        return orderDealStatusList;
    }
}
