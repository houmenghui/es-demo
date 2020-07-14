package com.esdemo.modules.service.impl;

import cn.hutool.core.util.StrUtil;
import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.utils.Constants;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.modules.bean.AgentInfo;
import com.esdemo.modules.bean.EsNpospDataBean;
import com.esdemo.modules.bean.MerchantInfo;
import com.esdemo.modules.dao.AcqMerchantDao;
import com.esdemo.modules.dao.AgentInfoDao;
import com.esdemo.modules.dao.EsDataMigrateDao;
import com.esdemo.modules.dao.MerchantDao;
import com.esdemo.modules.service.EsDataMigrateService;
import com.esdemo.modules.service.MerchantInfoService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.esdemo.frame.enums.EsNpospField.*;
import static com.esdemo.frame.enums.EsNpospJoinType.ORDER;
import static com.esdemo.frame.enums.EsNpospType.MBP;
import static com.esdemo.frame.enums.EsNpospType.MERCHANT;
import static com.esdemo.frame.utils.Constants.NPOSP_ES_INDEX;
import static com.esdemo.frame.utils.Constants.NPOSP_ES_TYPE;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-08-02 16:11
 */
@Async
@Slf4j
@Service
public class EsDataMigrateServiceImpl implements EsDataMigrateService {

    @Resource
    private EsDataMigrateDao esDataMigrateDao;
    @Resource
    private MerchantDao merchantDao;
    @Resource
    private AcqMerchantDao acqMerchantDao;
    @Resource
    private MerchantInfoService merchantInfoService;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private TransportClient client;
    @Resource
    private AgentInfoDao agentInfoDao;

    @DataSourceSwitch(DataSourceType.WRITE)
    @Override
    public void agentMigrate(String migrateAgentNode, String newParentId) {
        log.info("需要将代理商 {} 迁移到 代理商 {} 下", migrateAgentNode, newParentId);
        if (StringUtils.isBlank(migrateAgentNode) || StringUtils.isBlank(newParentId)) {
            return;
        }
        if (!migrateAgentNode.matches("0-(\\d+-)+")) {
            log.info("需要迁移的代理商节点格式不正确 {} ,直接返回", migrateAgentNode);
            return;
        }
        try {
            // 延时10s后才执行,
            // 等待调用方事务提交以及mysql主从同步
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] split = migrateAgentNode.split("-");
        String migrateAgentNo = split[split.length - 1];
        String oldParentId = split[split.length - 2];
        if (StringUtils.equalsIgnoreCase(oldParentId, newParentId)) {
            log.info("需要将代理商 {} 迁移到 代理商 {} 下, 迁移前代理商的父节点与迁移后的新父节点一致,直接返回", migrateAgentNode, newParentId);
            return;
        }
        AgentInfo migrateAgentInfo = agentInfoDao.selectByAgentNo(migrateAgentNo);
        AgentInfo newParentAgentInfo = agentInfoDao.selectByAgentNo(newParentId);
        if (migrateAgentInfo == null || newParentAgentInfo == null) {
            log.info("需要将代理商 {} 迁移到 代理商 {} 下, 找不到对源/目标代理商,直接返回", migrateAgentNode, newParentId);
            return;
        }
        String newAgentNode = String.format("%s%s-", newParentAgentInfo.getAgentNode(), migrateAgentNo);
        log.info("迁移前代理商节点 {}, 迁移后代理商节点 {}", migrateAgentNode, newAgentNode);
        String scriptCode = String.format("ctx._source.agent_node=ctx._source.agent_node.replace('%s','%s')", migrateAgentNode, newAgentNode);
        Script script = new Script(scriptCode);
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        updateByQuery
                .source(NPOSP_ES_INDEX)
                .script(script)
                .size(5000)
                .filter(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termsQuery(TYPE_NAME.getFieldName(), MBP.getTypeName(), ORDER.getTypeName()))
                        .must(QueryBuilders.wildcardQuery(AGENT_NODE.getKey(), String.format("%s*", migrateAgentNode)))
                );
        while (true) {
            BulkByScrollResponse bulkByScrollResponse = updateByQuery.refresh(true).execute().actionGet();
            log.info("需要将代理商 {} 迁移到 代理商 {} 下, 迁移的数据数量 {}", migrateAgentNode, newParentId, bulkByScrollResponse.getUpdated());
            if (bulkByScrollResponse.getUpdated() == 0) {
                return;
            }
        }
    }

    @DataSourceSwitch(DataSourceType.WRITE)
    @Override
    public void changeMerchantProducts(String merchantNo) {
        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
        if (merchantInfo == null) {
            log.info("变更业务产品 商户号{}, 商户不存在,直接返回", merchantNo);
            return;
        }
        try {
            // 延时10s后才执行,
            // 等待调用方事务提交以及mysql主从同步
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String routing = String.format("agent_%s", merchantInfo.getAgentNo());
        synchronized (merchantNo.intern()) {
            log.info("变更业务产品 商户号{}", merchantNo);
            deleteOldMerchantProducts(merchantNo, routing);
            insertNewMbpEsData(merchantNo, routing, getGson());
        }
    }

    private int deleteOldMerchantProducts(String merchantNo, String routing) {
        int count = 0;
        do {
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withIndices(NPOSP_ES_INDEX)
                    .withTypes(NPOSP_ES_TYPE)
                    .withQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MBP.getTypeName()))
                            .must(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo))
                            .must(QueryBuilders.termQuery(ROUTING.getFieldName(), routing))
                    )
                    .withPageable(PageRequest.of(0, 10000))
                    .build();
            List<String> ids = elasticsearchTemplate.queryForIds(searchQuery);
            log.info("变更业务产品, 删除es旧mbp数据, 商户号:{}, 需要删除的数量: {}", merchantNo, ids.size());
            count += ids.size();
            if (CollectionUtils.isEmpty(ids)) {
                return count;
            }
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            ids.stream()
                    .map(item -> {
                        DeleteRequest deleteRequest = new DeleteRequest(NPOSP_ES_INDEX, NPOSP_ES_TYPE, item);
                        deleteRequest.routing(routing);
                        return deleteRequest;
                    }).forEach(bulkRequest::add);
            client.bulk(bulkRequest).actionGet();
        } while (true);
    }

    @DataSourceSwitch(DataSourceType.WRITE)
    @Override
    public void merchantMigrate(String merchantNo, String oldAgentNo) {

        // 参数有误直接返回
        if (StrUtil.isBlank(merchantNo) || StrUtil.isBlank(oldAgentNo)) {
            log.info("参数有误, 直接返回, merchantNo = {}, oldAgentNo={}", merchantNo, oldAgentNo);
            return;
        }
        try {
            // 延时10s后才执行,
            // 等待调用方事务提交以及mysql主从同步
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 如果是同一个商户,这必须处理完前一个才处理
        synchronized (merchantNo.intern()) {
            long startTime = System.currentTimeMillis();
            MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
            // 找不到数据,或者商户迁移代理商编号一样,则不需要处理
            if (merchantInfo == null || StrUtil.equalsIgnoreCase(oldAgentNo, merchantInfo.getAgentNo())) {
                log.info("找不到商户或前后代理商编号一致,直接返回, merchantInfo = {}", merchantInfo);
                return;
            }
            String oldRouting = String.format("agent_%s", oldAgentNo);
            String newRouting = String.format("agent_%s", merchantInfo.getAgentNo());
            log.info("商户号:{},旧路由: {}, 新路由: {}", merchantNo, oldRouting, newRouting);
            // 删除旧数据
            int count = deleteOldEsData(merchantNo, oldAgentNo, oldRouting);
            if (count == 0) {
                log.info("es中可以没有删除的数据,则直接返回,商户编号: {}", merchantNo);
                return;
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            Gson gson = gsonBuilder.create();
            // 插入新的商户数据
            insertNewMerchantEsData(merchantNo, newRouting, gson);
            // 插入新的商户进件项数据
            insertNewMbpEsData(merchantNo, newRouting, gson);
            // 插入新的订单数据
            insertNewOrderEsData(merchantNo, merchantInfo, newRouting, gson);
            long endTime = System.currentTimeMillis();
            log.info("商户号:{},旧路由: {}, 新路由: {}, 迁移数据总耗时 {}", merchantNo, oldRouting, newRouting, (endTime - startTime));
        }
    }

    @DataSourceSwitch(DataSourceType.READ)
    @Override
    public void repairMerchantMigrate(String merchantNo) {
        // 参数有误直接返回
        if (StrUtil.isBlank(merchantNo)) {
            log.info("参数有误, 直接返回, merchantNo = {}", merchantNo);
            return;
        }
        // 如果是同一个商户,这必须处理完前一个才处理
        synchronized (merchantNo.intern()) {
            long startTime = System.currentTimeMillis();
            MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
            // 找不到数据,或者商户迁移代理商编号一样,则不需要处理
            if (merchantInfo == null ) {
                log.info("找不到商户,直接返回, merchantNo = {}", merchantNo);
                return;
            }
            String newRouting = String.format("agent_%s", merchantInfo.getAgentNo());

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            Gson gson = gsonBuilder.create();
            // 插入新的商户数据
            insertNewMerchantEsData(merchantNo, newRouting, gson);
            // 插入新的商户进件项数据
            insertNewMbpEsData(merchantNo, newRouting, gson);
            // 插入新的订单数据
            insertNewOrderEsData(merchantNo, merchantInfo, newRouting, gson);
            long endTime = System.currentTimeMillis();
            log.info("修复商户号:{}, 新路由: {}, 迁移数据总耗时 {}", merchantNo, newRouting, (endTime - startTime));
        }
    }

    //    /**
//     * 根据商户子组织修改进件和交易子组织
//     *
//     * @param merchantNo     需要修改的商户号
//     * @param newEntryTeamId 新的商户子组织
//     */
//    @Override
//    public void updateMbpAndOrderEntryTeamByMer(String merchantNo, String newEntryTeamId) {
//        log.info("根据商户号：{}新子组织：{}同步修改ES进件和订单存量数据的子组织");
//        if (StringUtils.isBlank(merchantNo, newEntryTeamId)) {
//            return;
//        }
//        //校验商户合法性
//        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
//        if (null == merchantInfo) {
//            log.error("商户号{}不存在", merchantNo);
//            return;
//        }
//        //判断接收新子组织与商户主库子组织是否一致
//        String dbMerEntryTeamId = merchantInfoService.getEntryTeamIByMerNo(merchantNo);
//        if (!newEntryTeamId.equals(dbMerEntryTeamId)) {
//            log.error("商户{}数据库主库子组织{}与需要接口接收的自组织{}不一致", merchantNo, dbMerEntryTeamId, newEntryTeamId);
//            return;
//        }
//        Client client = elasticsearchTemplate.getClient();
//        //先修改进件的子组织
//        BoolQueryBuilder builder = QueryBuilders.boolQuery()
//                //查询类型
//                .filter(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MBP.getTypeName()))
//                .filter(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo));
//
//        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
//        updateByQuery.source(Constants.NPOSP_ES_INDEX)
//                //查询要修改的结果集
//                .filter(builder)
//                //修改操作
//                .script(new Script("ctx._source['team_entry_id']='" + newEntryTeamId + "';"));
//        //响应结果集
//        BulkByScrollResponse response = updateByQuery.get();
//        long updatedCount = response.getUpdated();
//        log.info("商户号：{}同步ES进件的子组织：{}影响的行数：{}", merchantNo, newEntryTeamId, updatedCount);
//    }

//    /**
//     * 普通商户绑定特约商户，同步更新ES商户数据
//     *
//     * @param merchantNo
//     * @param acqMerchantNo
//     */
//    @Override
//    public void bindAcqMerchantNoToEs(String merchantNo, String acqMerchantNo) {
//        acqMerchantNo = StringUtils.isBlank(acqMerchantNo) ? null : acqMerchantNo;
//        log.info("商户号：{}绑定特约商户：{}，同步更新ES商户数据", merchantNo, acqMerchantNo);
//        if (StringUtils.isBlank(merchantNo)) {
//            log.error("必要参数不能为空");
//            return;
//        }
//        Script script = new Script("ctx._source['acq_merchant_no']=null;");
//        if (StringUtils.isNotBlank(acqMerchantNo)) {
//            //校验收单特约商户合法性
//            Map<String, Object> acqMerchantMap = acqMerchantDao.queryAcqMerByGeneralMerNo(merchantNo, acqMerchantNo);
//            if (null == acqMerchantMap || acqMerchantMap.isEmpty()) {
//                log.error("商户号{}对应特约商户{}记录不存在", merchantNo, acqMerchantNo);
//                return;
//            }
//            script = new Script("ctx._source['acq_merchant_no']='" + acqMerchantNo + "';");
//        }
//        Client client = elasticsearchTemplate.getClient();
//        //修改商户的特约商户标识
//        BoolQueryBuilder builder = QueryBuilders.boolQuery()
//                //查询类型
//                .filter(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MERCHANT.getTypeName()))
//                .filter(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo));
//
//        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
//        updateByQuery.source(Constants.NPOSP_ES_INDEX)
//                //查询要修改的结果集
//                .filter(builder)
//                //修改操作
//                .script(script);
//        //响应结果集
//        BulkByScrollResponse response = updateByQuery.get();
//        long updatedCount = response.getUpdated();
//        log.info("商户号：{}绑定特约商户：{}，同步更新ES商户数据影响的行数：{}", merchantNo, acqMerchantNo, updatedCount);
//    }

    @Override
    public void activeMerchantBusinessProduct(String merchantNo) {
        try {
            // 延时10s后才执行,
            // 等待调用方事务提交以及mysql主从同步
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("需要同步激活商户进价项,商户号为 {}", merchantNo);
        if (StringUtils.isBlank(merchantNo)) {
            return;
        }
        // 校验商户合法性
        MerchantInfo merchantInfo = merchantDao.queryMerchantInfoByNo(merchantNo);
        if (null == merchantInfo || !StringUtils.equalsIgnoreCase("1", merchantInfo.getHlfActive())) {
            log.error("商户号{}不存在或欢乐返状态未激活", merchantNo);
            return;
        }
        Client client = elasticsearchTemplate.getClient();

        BoolQueryBuilder builder = QueryBuilders.boolQuery()
                // 查询商户进件项
                .filter(QueryBuilders.termQuery(TYPE_NAME.getFieldName(), MBP.getTypeName()))
                // 必须是传入的商户对应的进件项
                .filter(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo))
                // 欢乐返未激活
                .filter(QueryBuilders.termQuery(HLF_ACTIVE.getFieldName(), "0"));

        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        updateByQuery.source(Constants.NPOSP_ES_INDEX)
                //查询要修改的结果集
                .filter(builder)
                // 将欢乐激活状态改为激活
                .script(new Script("ctx._source['" + HLF_ACTIVE.getFieldName() + "']='1';"));
        //响应结果集
        BulkByScrollResponse response = updateByQuery.get();
        long updatedCount = response.getUpdated();
        log.info("商户号：{}同步商户进件欢乐返激活状态, 影响的行数：{}", merchantNo, updatedCount);
    }

    /**
     * 插入新的商户订单数据
     *
     * @param merchantNo   商户编号
     * @param merchantInfo 商户信息
     * @param newRouting   新的路由
     * @param gson         gson
     */
    private void insertNewOrderEsData(String merchantNo, MerchantInfo merchantInfo, String newRouting, Gson gson) {
        log.info("插入新的商户订单数据, 商户号:{},新路由: {}", merchantNo, newRouting);
        String parentId = String.format("merchant_%s", merchantNo);
        int offset = 0;
        int pageSize = 5000;
        for (; true; ) {
            List<EsNpospDataBean> orderEsData = esDataMigrateDao.listOrderInfo(merchantNo, pageSize, offset);
            if (CollectionUtils.isEmpty(orderEsData)) {
                return;
            }
            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
            orderEsData.stream()
                    .map(item -> {
                        item.setTypeName(EsNpospDataBean.TypeName.builder()
                                .name(ORDER.getTypeName())
                                .parent(parentId)
                                .build());
                        item.setAgentNo(merchantInfo.getAgentNo());
                        item.setAgentNode(merchantInfo.getParentNode());
                        return client.prepareIndex(NPOSP_ES_INDEX, NPOSP_ES_TYPE, String.format("order_%s", item.getOrderNo()))
                                .setRouting(newRouting)
                                .setSource(GsonUtils.fromJson2Map(gson.toJson(item), Object.class))
                                .request();
                    }).forEach(bulkRequestBuilder::add);
            BulkResponse bulkItemResponses = bulkRequestBuilder
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL)
                    .execute().actionGet();
            log.info("插入新的商户订单数据,返回错误信息{}", bulkItemResponses.buildFailureMessage());
            // 如果分页查询出来的数据刚好等于pageSize,
            // 说明有可能还有下一页,则继续查询,否则就直接退出
            if (orderEsData.size() == pageSize) {
                offset += pageSize;
            } else {
                return;
            }
        }

    }

    /**
     * 插入新的商户进件项数据
     *
     * @param merchantNo 商户编号
     * @param newRouting 新的路由
     * @param gson       gson
     */
    private void insertNewMbpEsData(String merchantNo, String newRouting, Gson gson) {
        log.info("插入新的商户进件项数据, 商户号:{},新路由: {}", merchantNo, newRouting);
        String merchantEsId = String.format("merchant_%s", merchantNo);
        List<EsNpospDataBean> mbpEsData = esDataMigrateDao.listMbpInfo(merchantNo);
        if (CollectionUtils.isNotEmpty(mbpEsData)) {
            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
            mbpEsData.stream()
                    .map(item -> {
                        item.setTypeName(EsNpospDataBean.TypeName.builder()
                                .name(MBP.getTypeName())
                                .parent(merchantEsId)
                                .build());
                        return client.prepareIndex(NPOSP_ES_INDEX, NPOSP_ES_TYPE, String.format("mbp_%s", item.getMid()))
                                .setRouting(newRouting)
                                .setSource(GsonUtils.fromJson2Map(gson.toJson(item), Object.class))
                                .request();
                    }).forEach(bulkRequestBuilder::add);
            BulkResponse bulkItemResponses = bulkRequestBuilder
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL)
                    .execute()
                    .actionGet();
            log.info("插入新的商户进件项数据,返回错误信息{}", bulkItemResponses.buildFailureMessage());
        }
    }

    /**
     * 插入新的商户数据
     *
     * @param merchantNo 商户编号
     * @param newRouting 新的路由
     * @param gson       gson
     */
    private void insertNewMerchantEsData(String merchantNo, String newRouting, Gson gson) {
        log.info("插入新的商户数据, 商户号:{},新路由: {}", merchantNo, newRouting);
        String merchantEsId = String.format("merchant_%s", merchantNo);
        EsNpospDataBean merchantData = esDataMigrateDao.queryMerchantInfo(merchantNo);
        merchantData.setTypeName(EsNpospDataBean.TypeName.builder()
                .name(MERCHANT.getTypeName())
                .parent(newRouting)
                .build());
        client.prepareIndex(NPOSP_ES_INDEX, NPOSP_ES_TYPE, merchantEsId)
                .setRouting(newRouting)
                .setSource(GsonUtils.fromJson2Map(gson.toJson(merchantData), Object.class))
                .execute();
    }

    /**
     * 删除就的es数据
     * 因为每次查询不得超过1w条数据
     * 因此分批查询后删除
     *
     * @param merchantNo 商户编号
     * @param oldRouting 旧的路由
     */
    private int deleteOldEsData(String merchantNo, String oldAgentNo, String oldRouting) {
        int count = 0;
        do {
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withIndices(NPOSP_ES_INDEX)
                    .withTypes(NPOSP_ES_TYPE)
                    .withQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery(MERCHANT_NO.getFieldName(), merchantNo))
                            .must(QueryBuilders.termQuery(ROUTING.getFieldName(), oldRouting))
                            .must(QueryBuilders.termQuery(AGENT_NO.getFieldName(), oldAgentNo))
                    )
                    .withPageable(PageRequest.of(0, 10000))
                    .build();
            List<String> ids = elasticsearchTemplate.queryForIds(searchQuery);
            log.info("删除es旧的数据, 商户号:{},旧路由: {}, 需要删除的数量: {}", merchantNo, oldRouting, ids.size());
            count += ids.size();
            if (CollectionUtils.isEmpty(ids)) {
                return count;
            }
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            ids.stream()
                    .map(item -> {
                        DeleteRequest deleteRequest = new DeleteRequest(NPOSP_ES_INDEX, NPOSP_ES_TYPE, item);
                        deleteRequest.routing(oldRouting);
                        return deleteRequest;
                    }).forEach(bulkRequest::add);
            client.bulk(bulkRequest).actionGet();
        } while (true);
    }

    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }
}
