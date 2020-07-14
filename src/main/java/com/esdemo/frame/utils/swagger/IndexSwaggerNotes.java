package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：首页接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class IndexSwaggerNotes {

    public static final String LOAD_CURR_DAY_DATA = "首页->今日业绩\n" +
            "- 返回参数\n" +
            "    - dayIncome: 今日收入\n" +
            "    - dayOrderAmount: 今日交易量\n" +
            "    - dayOrderCount: 今日交易笔数\n" +
            "    - dayAddMerCount: 今日新增商户\n" +
            "    - dayAddMerOrderAmount: 今日新增商户交易量\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"dayAddMerCount\": 1,\n" +
            "        \"dayOrderAmount\": 0.00,\n" +
            "        \"dayIncome\": \"\",\n" +
            "        \"dayOrderCount\": 0,\n" +
            "        \"dayAddMerOrderAmount\": 0.00\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String LOAD_CURR_MONTH_DATA = "首页->本月业绩\n" +
            "- 请求参数\n" +
            "    - queryScope: 查询范围，ALL：全部数据，OFFICAL：直属数据，CHILDREN：下级数据，非必传，默认按全部数据汇总，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - monthProfitIncome: 本月分润收入\n" +
            "    - monthActivityIncome: 本月活动补贴收入\n" +
            "    - monthOrderCount: 本月交易笔数\n" +
            "    - monthOrderAmount: 本月交易量\n" +
            "    - monthAddMerCount: 本月新增商户数\n" +
            "    - monthAddMerOrderAmount: 本月新增商户交易量\n\n" +
            "    - explain: 本月业绩底部说明文本\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"monthOrderAmount\": 31362.40,\n" +
            "        \"monthProfitIncome\": \"\",\n" +
            "        \"monthAddMerOrderAmount\": 0.00,\n" +
            "        \"monthOrderCount\": 45,\n" +
            "        \"monthAddMerCount\": 12,\n" +
            "        \"monthActivityIncome\": \"\"\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String EXPLAIN_OF_MARK = "问号说明\n" +
            "- 请求参数\n" +
            "    - markType: 问号类型，必传，位于请求body中\n" +
            "       - INDEX_CURR_DAY_MARK: 今日业绩问号说明\n" +
            "       - SIX_MONTH_ORDER_MARK: 数据-近6月交易量问号说明\n\n" +
            "- 返回参数\n" +
            "    - context: 文本内容\n" +
            "    - buttonText: 按钮文字\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"buttonText\": \"我知道了\",\n" +
            "        \"context\": \"<ul><li>1. 收入包含本级所有的收入，包含已下发和未下发的金额</li><li>2. 今日业绩中的其它数据均包含本级及所有下级的数据</li></ul>\"\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";


    public static final String GET_TODO_STATUS_DOC = "首页->首页获取待办事项显示状态\n" +
            "- 返回参数\n" +
            "    - safephone: 是否设置过安全手机0否1是\n" +
            "    - safePassword: 是否设置过安全密码0否1是\n" +
            "    - accountNo: 是否绑定过结算卡0否1是\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"safephone\": 1,\n" +
            "        \"safePassword\": 1,\n" +
            "        \"accountNo\": 0\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";
}
