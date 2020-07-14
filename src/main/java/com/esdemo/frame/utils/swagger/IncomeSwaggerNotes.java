package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：收入接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class IncomeSwaggerNotes {

    public static final String INCOME_DETAIL = "收入明细(按月/按日统计下发数据)\n" +
            "- 请求参数\n" +
            "    - pageNo: 当前页数，默认从1开始，必传，位于请求路径中\n" +
            "    - pageSize: 每页条数，默认10条，必传，位于请求路径中\n" +
            "    - agentNo: 按代理商查询，非必传，默认当前登录代理商，位于请求body中\n" +
            "    - timeUnit: 查询维度，day：日维度、month：月维度，必传，位于请求body中\n" +
            "- 返回参数\n" +
            "    - totalIncome: 总收入\n" +
            "    - profitIncome: 分润收入\n" +
            "    - hpbIncome: 活动补贴\n" +
            "    - subIncome: 下发金额\n" +
            "    - subProfitIncome: 分润收入（下发）\n" +
            "    - subHpbIncome: 活动补贴（下发）\n" +
            "    - leftIncome: 剩余金额\n" +
            "    - leftProfitIncome: 分润收入（剩余）\n" +
            "    - leftHpbIncome: 活动补贴（剩余）\n\n" +
            "- 例子（按日维度统计）\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"key\": \"2020-04-01\",\n" +
            "            \"value\": {\n" +
            "                \"totalIncome\": 0,\n" +
            "                \"profitIncome\": 0,\n" +
            "                \"hpbIncome\": 0,\n" +
            "                \"subIncome\": 0,\n" +
            "                \"subProfitIncome\": 0,\n" +
            "                \"subHpbIncome\": 0,\n" +
            "                \"leftIncome\": 0,\n" +
            "                \"leftProfitIncome\": 0,\n" +
            "                \"leftHpbIncome\": 0\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"2020-04-02\",\n" +
            "            \"value\": {\n" +
            "                \"totalIncome\": 0,\n" +
            "                \"profitIncome\": 0,\n" +
            "                \"hpbIncome\": 0,\n" +
            "                \"subIncome\": 0,\n" +
            "                \"subProfitIncome\": 0,\n" +
            "                \"subHpbIncome\": 0,\n" +
            "                \"leftIncome\": 0,\n" +
            "                \"leftProfitIncome\": 0,\n" +
            "                \"leftHpbIncome\": 0\n" +
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
            "                \"totalIncome\": 366.00,\n" +
            "                \"profitIncome\": 180.00,\n" +
            "                \"hpbIncome\": 186.00,\n" +
            "                \"subIncome\": 90.14,\n" +
            "                \"subProfitIncome\": 4.38,\n" +
            "                \"subHpbIncome\": 85.76,\n" +
            "                \"leftIncome\": 275.86,\n" +
            "                \"leftProfitIncome\": 175.62,\n" +
            "                \"leftHpbIncome\": 100.24\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"2020-02\",\n" +
            "            \"value\": {\n" +
            "                \"totalIncome\": 837.29,\n" +
            "                \"profitIncome\": 489.29,\n" +
            "                \"hpbIncome\": 348.00,\n" +
            "                \"subIncome\": 483.60,\n" +
            "                \"subProfitIncome\": 249.49,\n" +
            "                \"subHpbIncome\": 234.11,\n" +
            "                \"leftIncome\": 353.69,\n" +
            "                \"leftProfitIncome\": 239.80,\n" +
            "                \"leftHpbIncome\": 113.89\n" +
            "            },\n" +
            "            \"description\": null\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String MY_INCOME_CENSUS = "数据-我的收入\n" +
            "- 返回参数\n" +
            "    - totalProfitIncome: 分润收入\n" +
            "    - totalHpbIncome: 活动补贴\n" +
            "    - totalIncome: 累计收入\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"totalIncome\": 180002663.36,\n" +
            "        \"totalProfitIncome\": 180001949.36,\n" +
            "        \"totalHpbIncome\": 714.00\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";
}
