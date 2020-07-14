package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：交易接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class OrderSwaggerNotes {

    public static final String ACHIEVEMENT_DETAIL = "业绩明细(按月/按日统计下发数据)\n" +
            "- 请求参数\n" +
            "    - pageNo: 当前页数，默认从1开始，必传，位于请求路径中\n" +
            "    - pageSize: 每页条数，默认10条，必传，位于请求路径中\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - queryScope: 查询范围，ALL：全部交易，OFFICAL：直属交易，CHILDREN：下级交易，非必传，默认按全部交易汇总，位于请求body中\n" +
            "    - timeUnit: 查询维度，day：日维度、month：月维度，必传，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - addMerCount: 新增商户\n" +
            "    - activedMerCount: 已激活商户\n" +
            "    - addAgentCount: 新增代理商\n" +
            "    - totalOrderAmount: 总交易量\n" +
            "    - totalOrderCount: 总交易笔数\n" +
            "    - posOrderAmount: pos刷卡交易量\n" +
            "    - posOrderCount: pos刷卡交易笔数\n" +
            "    - yunOrderAmount: 云闪付交易量\n" +
            "    - yunOrderCount: 云闪付交易笔数\n" +
            "    - noCardOrderAmount: 无卡交易量\n" +
            "    - noCardOrderCount: 无卡交易笔数\n\n" +
            "- 例子（按日维度统计）\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"key\": \"2020-04-01\",\n" +
            "            \"value\": {\n" +
            "                \"addAgentCount\": 0,\n" +
            "                \"totalOrderAmount\": 23502.15,\n" +
            "                \"posOrderCount\": 28,\n" +
            "                \"posOrderAmount\": 23493.35,\n" +
            "                \"addMerCount\": 1,\n" +
            "                \"activedMerCount\": 0,\n" +
            "                \"noCardOrderAmount\": 8.80,\n" +
            "                \"noCardOrderCount\": 2,\n" +
            "                \"yunOrderCount\": 0,\n" +
            "                \"totalOrderCount\": 30,\n" +
            "                \"yunOrderAmount\": 0.00\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"2020-04-02\",\n" +
            "            \"value\": {\n" +
            "                \"addAgentCount\": 0,\n" +
            "                \"totalOrderAmount\": 0.00,\n" +
            "                \"posOrderCount\": 0,\n" +
            "                \"posOrderAmount\": 0.00,\n" +
            "                \"addMerCount\": 2,\n" +
            "                \"activedMerCount\": 1,\n" +
            "                \"noCardOrderAmount\": 0.00,\n" +
            "                \"noCardOrderCount\": 0,\n" +
            "                \"yunOrderCount\": 0,\n" +
            "                \"totalOrderCount\": 0,\n" +
            "                \"yunOrderAmount\": 0.00\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}\n" +
            "- 例子（按月维度统计）\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"key\": \"2020-01\",\n" +
            "            \"value\": {\n" +
            "                \"addAgentCount\": 0,\n" +
            "                \"totalOrderAmount\": 152331.69,\n" +
            "                \"posOrderCount\": 89,\n" +
            "                \"posOrderAmount\": 152074.55,\n" +
            "                \"addMerCount\": 32,\n" +
            "                \"activedMerCount\": 5,\n" +
            "                \"noCardOrderAmount\": 257.14,\n" +
            "                \"noCardOrderCount\": 30,\n" +
            "                \"yunOrderCount\": 0,\n" +
            "                \"totalOrderCount\": 119,\n" +
            "                \"yunOrderAmount\": 0.00\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"2020-02\",\n" +
            "            \"value\": {\n" +
            "                \"addAgentCount\": 1,\n" +
            "                \"totalOrderAmount\": 3061.26,\n" +
            "                \"posOrderCount\": 44,\n" +
            "                \"posOrderAmount\": 3053.26,\n" +
            "                \"addMerCount\": 18,\n" +
            "                \"activedMerCount\": 4,\n" +
            "                \"noCardOrderAmount\": 8.00,\n" +
            "                \"noCardOrderCount\": 4,\n" +
            "                \"yunOrderCount\": 0,\n" +
            "                \"totalOrderCount\": 48,\n" +
            "                \"yunOrderAmount\": 0.00\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String DATA_CENSUS = "数据-数据统计\n" +
            "- 请求参数\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - queryScope: 查询范围，ALL：全部交易，OFFICAL：直属交易，CHILDREN：下级交易，非必传，默认按全部交易汇总，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - orderAmount: 近6月交易量\n" +
            "    - orderCount: 近6月交易笔数\n" +
            "    - totalMerCount: 累计商户数量\n" +
            "    - totalAgentCount: 累计代理商数量\n" +
            "    - totalTerminalCount: 机具总数\n" +
            "    - activeTerminalCount: 已激活机具数量\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"totalTerminalCount\": 41976,\n" +
            "        \"orderAmount\": 1323370.28,\n" +
            "        \"activeTerminalCount\": 1292,\n" +
            "        \"totalAgentCount\": 91,\n" +
            "        \"orderCount\": 1281,\n" +
            "        \"totalMerCount\": 1499\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";
}
