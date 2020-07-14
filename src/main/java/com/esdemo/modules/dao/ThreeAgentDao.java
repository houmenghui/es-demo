package com.esdemo.modules.dao;

import com.esdemo.modules.bean.AgentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Title：agentApi2
 * @Description：三方代理商数据源
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@Mapper
public interface ThreeAgentDao {

    @Select(" SELECT t1.agent_link AS agentNo, t2.agent_node, t2.agent_name FROM agent_authorized_link t1 " +
            " LEFT JOIN agent_info t2 ON t1.agent_link = t2.agent_no WHERE t1.agent_authorized = #{agentNo}" +
            " AND t1.record_status = 1 AND t1.record_check = 1 AND t1.is_look = 1 ORDER BY t2.create_date DESC ")
    @ResultType(AgentInfo.class)
    List<AgentInfo> getDirectChildThreeAgent(@Param("agentNo") String agentNo);

    @Select(" select count(id) FROM agent_authorized_link WHERE agent_authorized = #{loginAgentNo} " +
            " AND agent_link = #{queryAgentNo} ")
    @ResultType(int.class)
    int canAccessTheThreeAgent(@Param("loginAgentNo") String loginAgentNo, @Param("queryAgentNo") String queryAgentNo);
}
