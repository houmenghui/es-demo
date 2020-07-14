package com.esdemo.modules.service;


import com.esdemo.modules.bean.UserInfoBean;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 调单管理
 * @date 2019/5/16
 */
public interface SurveyOrderInfoService {
    /**
     * 一级代理商
     * 查询是否存在 未提交、已提交、已逾期、逾期提交的正常调单记录
     * @param agentNode 代理商节点
     * @return
     */
    List<Map<String,Object>> selectSurveyOrderInfoByOneAgent(String agentNo, String agentNode);

    /**
     *调单查询
     * @param agentNo 代理商编号
     * @param agentNode 代理商节点
     * @return
     */
    List<Map<String,Object>> selectSurveyOrderInfo(String agentNo,String agentNode);

    /**
     * 下发地址下拉列表
     * @return
     */
    Map<String, Object> selectAddrees();

    /**
     * 根据前端传来的数据字典key值查询
     * @param sysKey
     * @return
     */
    List<Map<String, Object>> selectBySysKey(String sysKey);

    /**
     * 调单管理查询
     * @param params
     * @return
     */
    List<Map<String, Object>> selectSurveyOrderByConditions(Map<String, Object> params,UserInfoBean userInfoBean);

    /**
     * 调单详情
     * @param orderNo 调单号
     * @return
     */
    Map<String, Object> selectSurveyOrderDetail(String orderNo, UserInfoBean userInfoBean);

    /**
     * 回复详情
     * @param orderNo 调单号
     * @return
     */
    Map<String, Object> selectReplyDetail(String orderNo);

    /**
     * 查询reply_role_no
     * @param orderNo 调单号
     * @return
     */
    String selectReplyRoleNo(String orderNo);

    /**
     * 查询final_have_look_no
     * @param orderNo 调单号
     * @return
     */
    String selectFinalHaveLookNo(String orderNo);

    /**
     * 提交回复
     * @param request
     * @return
     */
    Map<String, Object> insertOrUpdateReply(Map<String, Object> request, UserInfoBean userInfoBean);

    /**
     * 调单回复记录列表
     * @param orderNo 调单号
     * @return
     */
    List<Map<String, Object>> selectReplyRecord(String orderNo);

    /**
     * 一级代理商提交回复,进行审核
     * @param entityId 当前登录代理商编号
     * @param orderNo 调单号
     * @return
     */
    Map<String, Object> updateReplyStatus(String entityId,String orderNo,UserInfoBean userInfoBean);

    /**
     * 根据当前登录代理商编号查询回复记录总条数
     * @param orderNo
     * @return
     */
    Integer selectReplyRecordCount(String orderNo);

    /**
     * 根据交易订单号从历史库查询订单状态
     * @param transOrderNo
     * @return
     */
    Map<String, Object> selectTransOrder(String transOrderNo);

    /**
     * 查看详情的时候修改字段
     * 1、调单终态时，点击了调单管理内的某条调单后要发请求到后台，
     * 后台记录浏览者编号，商户号、所属代理商编号、一级代理商编号。survey_order_info表新增了final_have_look_no字段。
     * @param orderNo
     * @return
     */
    Integer updateFinalHaveLookNo(String finalHaveLookNo,String orderNo);

    /**
     * 处理状态下拉列表
     * 要求客户端下拉列表只显示三种状态,约定为 1 可以查询所有已处理的状态 1234567
     * @return
     */
    List<Map<String, Object>> orderDealStatusList();
}
