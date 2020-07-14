package com.esdemo.modules.dao;

import com.esdemo.modules.bean.AgentSelectVo;
import com.esdemo.modules.bean.AgentTrend;
import com.esdemo.modules.bean.MerTrend;
import com.esdemo.modules.bean.TeamSelect;
import com.esdemo.modules.bean.ThreeDataCollect;
import com.esdemo.modules.bean.ThreeDataHome;
import com.esdemo.modules.bean.ThreeDataTetailQo;
import com.esdemo.modules.bean.TradeTrend;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 三方数据数据操作
 *
 * @author Qiu Jian
 *
 */
@Mapper
public interface ThreeDataDao {

	List<AgentSelectVo> selectChildrenAgentByAgentNoAndKeyword(@Param("agentNo") String agentNo,
			@Param("keyword") String keyword);

	@Select({ "SELECT ", //
			"	count( * )  ", //
			"FROM ", //
			"	agent_authorized_link  ", //
			"WHERE ", //
			"	agent_authorized =  #{agentNo,jdbcType=VARCHAR}", //
			"	AND record_status = 1  ", //
			"	AND record_check = 1  " //
	})
	int countAgentLinkByAgentNo(String agentNo);

	@Select({ "SELECT distinct ", //
			"	team_id,  ", //
			"	team_name  ", //
			"FROM ", //
			"	team_info WHERE team_id=#{teamId,jdbcType=VARCHAR}" })
	@ResultType(TeamSelect.class)
	TeamSelect selectTeamSelectByTeamId(String teamId);

	@Select({ "SELECT ", //
			"	agent_link  ", //
			"FROM ", //
			"	agent_authorized_link  ", //
			"WHERE ", //
			"	agent_authorized = #{agentNo,jdbcType=VARCHAR}  ", //
			"	AND record_status = 1  ", //
			"	AND record_check = 1  ", //
			"	AND is_look = 1  ", //
			"	AND link_level <= 5" })
	List<String> selectLookAgentNo(String agentNo);

	@Select({ "SELECT ", //
			"	agent_link  ", //
			"FROM ", //
			"	agent_authorized_link  ", //
			"WHERE ", //
			"	agent_authorized = #{currentAgentNo,jdbcType=VARCHAR}  ", //
			"	AND agent_link =  #{agentNo,jdbcType=VARCHAR}" })
	String selectAgentLinkByCurrentAgentNoAndAgentNo(@Param("currentAgentNo") String currentAgentNo,
			@Param("agentNo") String agentNo);

	ThreeDataCollect countThreeDataCollect(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId);

	@Select({ "SELECT ", //
			"	create_time  ", //
			"FROM ", //
			"	trade_sum_info  ", //
			"GROUP BY ", //
			"	create_time  ", //
			"ORDER BY ", //
			"	create_time DESC  ", //
			"	LIMIT 1" })
	Date selectLastUpdateTime();

	List<ThreeDataCollect> selectDetailByQo(ThreeDataTetailQo threeDataTetailQo);

	String countTerminalSumByAgentNoListAndTeamIdAndCreateTime(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId, @Param("date") Date date);

	ThreeDataCollect selectThreeDataCollectByQo(ThreeDataTetailQo threeDataTetailQo);

	ThreeDataCollect selectThreeDataCollectSectionByQo(ThreeDataTetailQo threeDataTetailQo);

	ThreeDataHome getYesterdayTradeSumAndTotalMerchantNumByAgentNoList(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId);

	Long getYesterdayTotalMerchantNumByAgentNoList(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId);

	Long countTotalAgent(@Param("agentNo") String agentNo, @Param("teamId") String teamId);

	ThreeDataHome getCurrentMonthTradeSumAndCurrentMonthTradeCountByAgentNoList(
			@Param("agentNoList") List<String> agentNoList, @Param("teamId") String teamId);

	Long getCurrentMonthAddAgentNumByAgentNo(@Param("agentNo") String agentNo, @Param("teamId") String teamId);

	Long getCurrentMonthAddMerchantNumByAgentNoList(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId);

	List<TradeTrend> tradeTrend(@Param("agentNoList") List<String> agentNoList, @Param("teamId") String teamId,
			@Param("type") String type);

	List<MerTrend> merTrend(@Param("agentNoList") List<String> agentNoList, @Param("teamId") String teamId,
			@Param("type") String type);

	List<AgentTrend> agentTrend(@Param("agentNo") String agentNo, @Param("teamId") String teamId,
			@Param("type") String type);

	Long getTotalMerchantNumByAgentNoList(@Param("agentNoList") List<String> agentNoList,
			@Param("teamId") String teamId);

}
