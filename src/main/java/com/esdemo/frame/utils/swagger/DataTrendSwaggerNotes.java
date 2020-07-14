package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：趋势相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class DataTrendSwaggerNotes {

    public static final String VIEW_INCOME_TREND = "收入趋势\n" +
            "- 请求参数\n" +
            "    - select_type: 查询类型（字符串），1：近7日，2：近半年，必传，位于请求body中\n\n" +
            "- 例子（近7日）\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"X\": \"04-15\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"04-16\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"04-17\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"04-18\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"04-19\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"04-20\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"04-21\",\n" +
            "            \"Y\": 0.0\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}" +
            "- 例子（近半年）\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"X\": \"2019-10\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"2019-11\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"2019-12\",\n" +
            "            \"Y\": 0.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"2020-01\",\n" +
            "            \"Y\": 366.00\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"2020-02\",\n" +
            "            \"Y\": 837.29\n" +
            "        },\n" +
            "        {\n" +
            "            \"X\": \"2020-03\",\n" +
            "            \"Y\": 554.07\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String VIEW_DATA_TREND = "数据-新增商户趋势、交易量趋势、新增代理商趋势\n" +
            "- 请求参数\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - queryScope: 查询范围，ALL：全部，OFFICAL：直属，CHILDREN：下级，非必传，默认按全部汇总，位于请求body中\n\n" +
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
