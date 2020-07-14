package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：设置接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class ProtocolSwaggerNotes {

    public static final String QUERY_PROTOCOL_VERSION = "下发隐私协议的版本\n" +
            "- 接口地址：/protocol/queryProtocolVersion\n" +
            "- 请求参数\n" +
            "    - appNo: 客户端编号，必传，位于请求头中\n\n" +
            "- 返回参数\n" +
            "    - code: 返回状态码，200成功\n" +
            "    - message: 错误信息\n" +
            "    - data: 数据集\n" +
            "    - count: 总条数\n" +
            "    - success: 是否成功\n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"version\": \"0\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";
}
