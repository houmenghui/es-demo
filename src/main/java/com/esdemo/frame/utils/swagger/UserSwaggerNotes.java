package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：人员管理接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class UserSwaggerNotes {


    public static final String QUERY_USER_LIST = "人员查询\n" +
            "- 请求参数\n" +
            "    - pageNo: 当前页，大于等于0，必传，位于请求接口地址中\n" +
            "    - pageSize: 每页显示条数，大于等于1，必传，位于请求接口地址中\n" +
            "    - manage: 角色，1：管理员；0：销售员，非必传，位于请求body中\n" +
            "    - status: 状态，1：正常；0：失效，非必传，位于请求body中\n" +
            "    - mobilephone: 手机号，非必传，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - totalCount: 总人数\n" +
            "    - normalCount: 正常人数\n" +
            "    - invalidCount: 失效人数\n" +
            "    - userList: 当前页的数据集合 \n\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"userList\": [\n" +
            "            {\n" +
            "                \"status_zh\": \"正常\",\n" +
            "                \"user_id\": \"1000000000000005851\",\n" +
            "                \"user_name\": \"12514785236\",\n" +
            "                \"mobilephone\": \"12514785236\",\n" +
            "                \"manage_zh\": \"销售员\",\n" +
            "                \"email\": \"12514785236@163.com\",\n" +
            "                \"manage\": \"0\",\n" +
            "                \"status\": \"1\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"status_zh\": \"正常\",\n" +
            "                \"user_id\": \"1000000000000005872\",\n" +
            "                \"user_name\": \"userName2\",\n" +
            "                \"mobilephone\": \"18604049006\",\n" +
            "                \"manage_zh\": \"管理员\",\n" +
            "                \"email\": \"422723652@qq.com\",\n" +
            "                \"manage\": \"1\",\n" +
            "                \"status\": \"1\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"invalidCount\": 0,\n" +
            "        \"normalCount\": 2,\n" +
            "        \"totalCount\": 22\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String SAVE_AGENT_USER_INFO = "新增人员信息\n" +
            "- 请求参数\n" +
            "    - userName: 姓名，必传，位于请求接口地址中\n" +
            "    - mobilephone: 手机号，必传，位于请求接口地址中\n" +
            "    - email: 邮箱，必传，位于请求body中\n" +
            "    - manage: 角色，必传，位于请求body中\n\n";

    public static final String UPDATE_AGENT_USER_INFO = "修改人员信息\n" +
            "- 请求参数\n" +
            "    - userId: 人员编号，必传，位于请求接口地址中\n" +
            "    - userName: 姓名，必传，位于请求接口地址中\n" +
            "    - mobilephone: 手机号，必传，位于请求接口地址中\n" +
            "    - email: 邮箱，必传，位于请求body中\n" +
            "    - manage: 角色，必传，位于请求body中\n" +
            "    - status: 状态，必传，位于请求body中\n\n";
}
