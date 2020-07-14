package com.esdemo.modules.service.impl;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.bean.PageBean;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.es.EsLog;
import com.esdemo.modules.bean.EsSearchBean;
import com.esdemo.modules.service.AgentEsService;
import com.esdemo.modules.utils.EsSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.esdemo.frame.enums.EsNpospField.*;
import static com.esdemo.frame.enums.EsNpospJoinType.AGENT;

/**
 * @Title：agentApi2
 * @Description：Agent业务层实现
 * @Author：zhangly
 * @Date：2019/5/13 11:36
 * @Version：1.0
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class AgentEsServiceImpl implements AgentEsService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 根据代理商节点或编号获取代理商信息
     *
     * @param agentKey 代理商节点或编号
     * @return 代理商全部字段信息，用的比较多，就不取单个字段了
     */
    @Override
    public Map<String, Object> queryAgentInfoByAgentNodeOrAgentNo(String agentKey) {
        if (StringUtils.isBlank(agentKey)) {
            return null;
        }
        BoolQueryBuilder builder = QueryBuilders.boolQuery()
                .should(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), AGENT.getTypeName()))
                        .filter(QueryBuilders.termQuery(AGENT_NODE.getFieldName() + ".key", agentKey)))
                .should(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), AGENT.getTypeName()))
                        .filter(QueryBuilders.termQuery(AGENT_NO.getFieldName(), agentKey)));

        log.info("根据代理商节点或编号查询{}对应的代理商信息DSL语句为：{}", agentKey, builder.toString());
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(Constants.NPOSP_ES_INDEX)
                .withQuery(builder)
                .build();

        List<Map<String, Object>> mapLis = elasticsearchTemplate.query(searchQuery, response -> {
            return Arrays.stream(response.getHits().getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
        });

        return CollectionUtils.isEmpty(mapLis) ? null : mapLis.get(0);

    }

    /**
     * 代理商查询
     *
     * @param searchBean 查询条件
     * @return
     */
    @Override
    public PageBean queryAgentInfoForPage(EsSearchBean searchBean) {

        searchBean.setTypeName(AGENT.getTypeName());
        log.info("代理商查询【参数：{}】开始", JSONUtil.toJsonStr(searchBean));
        NativeSearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean).build();
        EsLog.info("代理商查询", searchQuery);

        return elasticsearchTemplate.query(searchQuery, response -> {
            List<Map<String, Object>> pageContent = Arrays.stream(response.getHits().getHits()).map(item -> {
                Map<String, Object> sourceAsMap = item.getSourceAsMap();
                return sourceAsMap;
            }).collect(Collectors.toList());
            Long totalCount = response.getHits().getTotalHits();
            PageRequest pageRequest = searchBean.getPageRequest();
            PageBean page = new PageBean(pageRequest.getPageNumber(), pageRequest.getPageSize(), totalCount, pageContent);
            return page;
        });
    }

    /**
     * 统计代理商数量
     *
     * @param searchBean
     * @return
     */
    @Override
    @CacheData
    public long summaryAgentCountByTerms(EsSearchBean searchBean) {
        SearchQuery searchQuery = EsSearchUtils.getEsSearchQueryBuilder(searchBean).build();
        EsLog.info("统计代理商数量", searchQuery);
        return elasticsearchTemplate.count(searchQuery);
    }
}
