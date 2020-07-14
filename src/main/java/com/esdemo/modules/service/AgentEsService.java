package com.esdemo.modules.service;

import com.esdemo.frame.bean.PageBean;
import com.esdemo.modules.bean.EsSearchBean;

import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：Agent业务层
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
public interface AgentEsService {

    /**
     * 根据代理商节点或编号获取代理商信息
     *
     * @param agentKey 代理商节点或编号
     * @return 代理商全部字段信息，用的比较多，就不取单个字段了
     */
    Map<String, Object> queryAgentInfoByAgentNodeOrAgentNo(String agentKey);

    /**
     * 代理商查询
     *
     * @param searchBean 查询条件
     * @return
     */
    PageBean queryAgentInfoForPage(EsSearchBean searchBean);

    /**
     * 统计代理商数量
     *
     * @param searchBean
     * @return
     */
    long summaryAgentCountByTerms(EsSearchBean searchBean);
}
