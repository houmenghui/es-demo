package com.esdemo.modules.dao;


import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.MerchantInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 调单管理
 * @date 2019/5/16
 */
@Mapper
public interface SurveyOrderInfoDao {

	/**
	 * 根据交易订单号查交易信息
	 * @param transOrderNo
	 * @return
	 */
	Map<String,Object> selectTransOrder( @Param("transOrderNo")String transOrderNo);

	/**
	 * 根据商户号查商户信息
	 * @param merchantNo
	 * @return
	 */
	MerchantInfo getMerchantByNo(@Param("merchantNo")String merchantNo);

	/**
	 * 根据 sys_key 查询字典集合
	 * @param sys_key
	 * @return
	 */
	List<Map<String,Object>> getDictList(@Param("sys_key")String sys_key);

	/**
	 * 根据代理商编号查询代理商信息
	 * @param agentNo
	 * @return
	 */
	AgentInfo queryAgentInfoByNo(@Param("agentNo")String agentNo);

	/**
	 * 根据代理商节点查代理商信息
	 * @param agentNode
	 * @return
	 */
    AgentInfo selectByAgentNode(@Param("agentNode")String agentNode);

	List<Map<String,Object>> selectSurveyOrderInfoByOneAgent(@Param("agentNo") String agentNo, @Param("agentNode") String agentNode);

	List<Map<String,Object>> selectSurveyOrderInfo(@Param("agentNo") String agentNo, @Param("agentNode") String agentNode);

	Map<String,Object> selectByOrderNo(@Param("orderNo") String orderNo);

	Map<String, Object> selectAddrees();

	Map<String, Object> selectSurveyOrderDetail(@Param("orderNo") String orderNo,@Param("agentNode") String agentNode);

	Map<String, Object> selectReplyDetail(@Param("orderNo") String orderNo);

	List<Map<String, Object>> selectReplyRecord(@Param("orderNo") String orderNo);

	Integer insertReply(@Param("params") Map<String, Object> params);

	Integer updateReply(@Param("params") Map<String, Object> params);

	Integer updateSurveyOrderInfo(@Param("orderNo") String orderNo, @Param("replyStatus") String replyStatus);

	Integer updateSurveyOrderInfoByOneAgent(@Param("orderNo") String orderNo, @Param("replyStatus") String replyStatus);

	Integer updateDealStatus(@Param("orderNo") String orderNo);

	Integer updateFinalHaveLookNo(@Param("finalHaveLookNo") String finalHaveLookNo, @Param("orderNo") String orderNo);

	List<Map<String, Object>> selectSurveyOrderByConditions(@Param("params") Map<String, Object> params);

	Integer selectReplyRecordCount(@Param("orderNo") String orderNo);

	Map<String,Object> selectForUpdate(@Param("id") long id);

	String selectReplyRoleNo(@Param("orderNo") String orderNo);

	String selectFinalHaveLookNo(@Param("orderNo") String orderNo);
}
