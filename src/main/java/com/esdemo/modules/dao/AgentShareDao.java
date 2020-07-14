package com.esdemo.modules.dao;

import com.esdemo.modules.bean.AgentShareRule;
import com.esdemo.modules.bean.AgentShareRuleTask;
import com.esdemo.modules.bean.ProfitUpdateRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description 修改分润
 * @date 2019/6/13
 */
@Mapper
public interface AgentShareDao {

    /**
     * 插入修改分润记录
     * @return
     */
    Integer insertShareUpdateRecord(@Param("record")ProfitUpdateRecord record);

    String findAgentNo(@Param("shareId") Long shareId);

    List<ProfitUpdateRecord> selectAgentShare(@Param("shareId") Long shareId,@Param("agentNo") String agentNo);

    AgentShareRuleTask selectByShareId(@Param("shareId") Long shareId);

    /**
     * 获取同样类型的父级代理商的分润规则
     * @param shareId 下级的代理商的分润规则的主表id
     * @return 上级代理商同样类型的分润规则
     */
    AgentShareRule getSameTypeParentAgentShare(@Param("shareId") Long shareId);

    int insertAgentShareListTask(@Param("agent")AgentShareRuleTask shareList);

    /**
     * 通过shareId查找组员的业务产品id
     * @param shareId shareId
     * @return
     */
    List<Long> queryMemberBpId(@Param("shareId")Long shareId);

    /**
     * 根据队员的业务id和队长的共享Id获取到队员的共享id
     * @param memberBpId        队员的业务id
     * @param leaderShareId     队长的共享Id
     */
    Long getMemberShareId(@Param("memberBpId") Long memberBpId, @Param("leaderShareId") Long leaderShareId);

    /**
     * 获取同样类型的顶级代理商的最小服务费率
     *
     * @param rule       分润规则
     * @param oneLevelId 顶级代理商
     * @return
     */
    Map<String, Object> getSameTypeRootAgentMinServiceRate(@Param("rule") AgentShareRuleTask rule, @Param("oneLevelId") String oneLevelId, @Param("agentNo") String agentNo);

    /**
     * 获取同样类型的顶级代理商的最大服务费率
     *
     * @param rule       分润规则
     * @param oneLevelId 顶级代理商
     * @return
     */
    Map<String, Object> getSameTypeRootAgentMaxServiceRate(@Param("rule") AgentShareRule rule, @Param("oneLevelId") String oneLevelId, @Param("agentNo") String agentNo);

    /**
     * 查询代理商固定分润百分比
     *
     * @return
     */
    Map<String, Object> selectAgentShareRule(@Param("agentNo") String agentNo, @Param("serviceId") String serviceId, @Param("cardType") String cardType);

    /**
     * 查询代理商分润列表
     * @param agentNo
     * @return
     */
    List<AgentShareRule> getAgentShareList(@Param("agentNo")String agentNo);
}
