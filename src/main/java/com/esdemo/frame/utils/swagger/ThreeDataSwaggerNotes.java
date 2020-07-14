package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：三方接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class ThreeDataSwaggerNotes {

    public static final String THREE_DATA_DETAIL = "三方数据明细(按月/按日统计下发数据)\n" +
            "- 请求参数\n" +
            "    - pageNo: 当前页数，默认从1开始，必传，位于请求路径中\n" +
            "    - pageSize: 每页条数，默认10条，必传，位于请求路径中\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - teamId: 查询组织，非必传，为空或不传表示查询全部，位于请求body中\n" +
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

    public static final String THREE_DATA_AGENT_CENSUS = "三方数据代理商汇总\n" +
            "- 请求参数\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - teamId: 查询组织，非必传，为空或不传表示查询全部，位于请求body中\n" +
            "    - timeUnit: 查询维度，day：日维度、month：月维度，必传，位于请求body中\n" +
            "    - timeStr: 查询时间，如，日：2020-04-23、月：2020-04，必传，位于请求body中\n" +
            "    - orderByType: 排序类型，0：代理商注册日期倒序、1：总交易量由低到高、2：总交易量由高到低，为空或不传以0为准，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - key: key的返回稍微要解析一下，代理商编号_代理商名称，前端以_分割分别获取编号和名称\n\n\n" +
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
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"key\": \"1008040_安POSvip一级\",\n" +
            "            \"value\": {\n" +
            "                \"activedMerCount\": 1,\n" +
            "                \"addAgentCount\": 0,\n" +
            "                \"totalOrderAmount\": 8060.35,\n" +
            "                \"posOrderCount\": 10,\n" +
            "                \"posOrderAmount\": 3718.08,\n" +
            "                \"addMerCount\": 1,\n" +
            "                \"noCardOrderAmount\": 0.00,\n" +
            "                \"noCardOrderCount\": 0,\n" +
            "                \"yunOrderCount\": 22,\n" +
            "                \"totalOrderCount\": 32,\n" +
            "                \"yunOrderAmount\": 4342.27\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"1008991_一期优化一级代理商\",\n" +
            "            \"value\": {\n" +
            "                \"activedMerCount\": 2,\n" +
            "                \"addAgentCount\": 3,\n" +
            "                \"totalOrderAmount\": 2588.00,\n" +
            "                \"posOrderCount\": 5,\n" +
            "                \"posOrderAmount\": 2140.00,\n" +
            "                \"addMerCount\": 6,\n" +
            "                \"noCardOrderAmount\": 10.00,\n" +
            "                \"noCardOrderCount\": 1,\n" +
            "                \"yunOrderCount\": 5,\n" +
            "                \"totalOrderCount\": 11,\n" +
            "                \"yunOrderAmount\": 438.00\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"1009067_新数据测试一级代理商\",\n" +
            "            \"value\": {\n" +
            "                \"activedMerCount\": 2,\n" +
            "                \"addAgentCount\": 2,\n" +
            "                \"totalOrderAmount\": 1400.00,\n" +
            "                \"posOrderCount\": 7,\n" +
            "                \"posOrderAmount\": 1300.00,\n" +
            "                \"addMerCount\": 6,\n" +
            "                \"noCardOrderAmount\": 100.00,\n" +
            "                \"noCardOrderCount\": 1,\n" +
            "                \"yunOrderCount\": 0,\n" +
            "                \"totalOrderCount\": 8,\n" +
            "                \"yunOrderAmount\": 0.00\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}\n";

	public static final String THREE_DATA_HOME =  "三方数据汇总\n" +
            "- 请求参数\n" +
            "    - teamId: 查询组织，非必传，为空或不传表示查询全部\n" +
            "    - agentNo: 筛选代理商，非必传，为空或不传表示查询全部\n" +
            "- 返回参数\n" +
            "    - yesterdayTradeSum: 昨日交易量    数据例子0.00\n" +
            "    - totalMerchantNum: 商户总数    数据例子0\n" +
            "    - yesterdayAddMerchantNum: 昨日新增商户  数据例子0\n" +
            "    - totalAgentNum: 全部代理  数据例子0\n" +
            "    - currentMonthTradeSum: 本月交易量  数据例子0.00\n" +
            "    - currentMonthTradeCount: 本月交易笔数  数据例子0\n" +
            "    - currentMonthAddMerchantNum: 本月新增商户  数据例子0\n" +
            "    - currentMonthAddAgentNum: 本月新增代理  数据例子0\n\n" ;

	public static final String THREE_DATA_TREND =  "首页三方数据趋势\n" +
            "- 请求参数\n" +
            "    - teamId: 查询组织，非必传，为空或不传表示查询全部\n" +
            "    - agentNo: 筛选代理商，非必传，为空或不传表示查询全部\n" +
            "- 返回参数\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"transOrderTrend\": {\n" +
            "            \"sevenDayTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"05-22\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-23\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-24\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-25\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-26\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-27\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-28\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"halfYearTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"2018-12\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-01\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-02\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-03\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-04\",\n" +
            "                    \"value\": \"0.00\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-05\",\n" +
            "                    \"value\": \"3816.70\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"newlyMerTrend\": {\n" +
            "            \"sevenDayTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"05-22\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-23\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-24\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-25\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-26\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-27\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-28\",\n" +
            "                    \"value\": \"6\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"halfYearTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"2018-12\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-01\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-02\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-03\",\n" +
            "                    \"value\": \"1\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-04\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-05\",\n" +
            "                    \"value\": \"6\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"newlyAgentTrend\": {\n" +
            "            \"sevenDayTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"05-22\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-23\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-24\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-25\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-26\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-27\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"05-28\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"halfYearTrend\": [\n" +
            "                {\n" +
            "                    \"key\": \"2018-12\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-01\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-02\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-03\",\n" +
            "                    \"value\": \"1\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-04\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"key\": \"2019-05\",\n" +
            "                    \"value\": \"0\",\n" +
            "                    \"description\": \"\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";
}
