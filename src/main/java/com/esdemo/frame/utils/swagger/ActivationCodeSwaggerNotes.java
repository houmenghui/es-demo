package com.esdemo.frame.utils.swagger;

/**
 * @Title：agentApi2
 * @Description：设置接口相关的SwaggerNote
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
public final class ActivationCodeSwaggerNotes {

    public static final String LIST_NFC_ACTIVATION_CODE = "NFC激活码列表查询\n" +
            "- 接口地址：/activationCode/listNfcActivationCode/{pageNo}/{pageSize}\n" +
            "- 请求参数\n" +
            "    - pageNo: 当前页，大于等于0，必传，位于请求接口地址中，默认1\n" +
            "    - pageSize: 每页显示条数，大于等于10，必传，位于请求接口地址中，默认20\n" +
            "    - queryType: 查询类型，0：全部、1：未使用，必传，位于请求body中\n" +
            "    - queryRange: 查询范围，0：全部、1：我的，当queryType为0时有效，默认为1，位于请求body中\n" +
            "    - beginId: 起始激活码编号，非必传，位于请求body中\n" +
            "    - endId: 终止激活码编号，非必传，位于请求body中\n" +
            "    - actCodeStatus: 激活码状态，0：已入库、1：已分配、2：已激活，不传或空表示全部，位于请求body中\n" +
            "    - isAddPublic: 是否添加通用码，1：是、2：否，不传或空表示全部，位于请求body中\n" +
            "    - agentNo: 代理商编号，非必传，位于请求body中\n" +
            "    - merchantNo: 商户编号，非必传，位于请求body中\n\n" +
            "- 返回参数\n" +
            "    - code: 返回状态码，200成功\n" +
            "    - message: 错误信息\n" +
            "    - data: 数据集\n" +
            "    - count: 总条数\n" +
            "    - success: 是否成功\n\n" +
            "- 数据集字段说明\n" +
            "    - canRecoveryParentCode: 查询未使用页签时，是否可剔除通用码标识，true or false\n" +
            "    - canRecoveryParentCodeCount: 查询未使用页签时，canRecoveryParentCode为true时，可剔除通用码总数量\n" +
            "    - canRecoveryNfcActivationCount: 查询全部页签时，可回收激活码总数量\n" +
            "    - pageData: 当前页数据\n" +
            "       - id: 激活码id编号\n" +
            "       - uuidCode: 激活码信息\n" +
            "       - unifiedMerchantNo: 激活码激活商户号\n" +
            "       - unifiedMerchantName: 激活码激活商户名称\n" +
            "       - oneAgentNo: 激活码对应一级代理商编号\n" +
            "       - oneAgentName: 激活码对应一级代理商名称\n" +
            "       - agentNo: 激活码所属代理商编号\n" +
            "       - agentName: 激活码所属代理商名称\n" +
            "       - parentId: 激活码所属父级代理商编号\n" +
            "       - agentNode: 激活码所属代理商节点\n" +
            "       - status: 激活码状态，0:入库, 1:已分配, 2:已激活\n" +
            "       - activateTime: 激活时间\n" +
            "       - createTime: 创建时间\n" +
            "       - nfcOrigCode: 通用码信息\n" +
            "       - publicFlag: 是否添加通用码\n\n" +
            "- 返回例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"canRecoveryParentCode\": true,\n" +
            "        \"canRecoveryParentCodeCount\": 41,\n" +
            "        \"pageData\": [\n" +
            "            {\n" +
            "                \"id\": 200000000054,\n" +
            "                \"uuidCode\": \"47d04b45-5eb8-11ea-aa15-00163e1026d9\",\n" +
            "                \"agentNo\": \"1446\",\n" +
            "                \"agentName\": \"前海移联直营\",\n" +
            "                \"parentId\": \"0\",\n" +
            "                \"agentNode\": \"0-1446-\",\n" +
            "                \"status\": \"1\",\n" +
            "                \"createTime\": \"2020-03-05 16:06:56\",\n" +
            "                \"nfcOrigCode\": \"d84189a9-5e91-11ea-aa15-00163e1026d9\",\n" +
            "                \"publicFlag\": \"是\",\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"count\": 41,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String DIVIDE_NFC_ACTIVATION_CODE = "下发激活码\n" +
            "- 接口地址：/activationCode/divideNfcActivationCodeTo/{operateAgentNo}\n" +
            "- 请求参数\n" +
            "    - operateAgentNo: 下发代理商，必传，位于请求接口地址中，默认1\n\n" +
            "    - queryType: 查询类型，1：未使用，下发激活码只有在未使用页签下才有，必传，位于请求body中\n" +
            "    - beginId: 起始激活码编号，非必传，位于请求body中\n" +
            "    - endId: 终止激活码编号，非必传，位于请求body中\n" +
            "    - isAddPublic: 是否添加通用码，1：是、2：否，不传或空表示全部，位于请求body中\n" +
            "    - idArray: 单个选择的激活码id数组，非必传，位于请求body中\n\n" +
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
            "    \"data\": \n" +
            "        {\n" +
            "            \"succCount\": 2\n" +
            "        }\n" +
            "    ,\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String RECOVERY_NFC_ACTIVATION = "回收激活码\n" +
            "- 接口地址：/activationCode/recoveryNfcActivation\n" +
            "- 请求参数\n" +
            "    - queryType: 查询类型，0：全部，回收激活码只有在全部页签下才有，必传，位于请求body中\n" +
            "    - queryRange: 查询范围，0：全部、1：我的，当queryType为0时有效，默认为1，位于请求body中\n" +
            "    - beginId: 起始激活码编号，非必传，位于请求body中\n" +
            "    - endId: 终止激活码编号，非必传，位于请求body中\n" +
            "    - actCodeStatus: 激活码状态，0：已入库、1：已分配、2：已激活，不传或空表示全部，位于请求body中\n" +
            "    - isAddPublic: 是否添加通用码，1：是、2：否，不传或空表示全部，位于请求body中\n" +
            "    - agentNo: 代理商编号，非必传，位于请求body中\n" +
            "    - merchantNo: 商户编号，非必传，位于请求body中\n" +
            "    - idArray: 单个选择的激活码id数组，非必传，位于请求body中\n\n" +
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
            "    \"data\": \n" +
            "        {\n" +
            "            \"succCount\": 2\n" +
            "        }\n" +
            "    ,\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String ASSIGN_PARENTCODE = "分配母码\n" +
            "- 接口地址：/activationCode/assignParentCode\n" +
            "- 请求参数\n" +
            "    - queryType: 查询类型，1：未使用，分配母码只有在未使用页签下才有，必传，位于请求body中\n" +
            "    - beginId: 起始激活码编号，非必传，位于请求body中\n" +
            "    - endId: 终止激活码编号，非必传，位于请求body中\n" +
            "    - isAddPublic: 是否添加通用码，1：是、2：否，不传或空表示全部，位于请求body中\n" +
            "    - idArray: 单个选择的激活码id数组，非必传，位于请求body中\n\n" +
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
            "    \"data\": \n" +
            "        {\n" +
            "            \"succCount\": 2\n" +
            "        }\n" +
            "    ,\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String RECOVERY_PARENTCODE = "回收母码\n" +
            "- 接口地址：/activationCode/recoveryParentCode\n" +
            "- 请求参数\n" +
            "    - queryType: 查询类型，1：未使用，回收母码只有在未使用页签下才有，必传，位于请求body中\n" +
            "    - beginId: 起始激活码编号，非必传，位于请求body中\n" +
            "    - endId: 终止激活码编号，非必传，位于请求body中\n" +
            "    - isAddPublic: 是否添加通用码，1：是、2：否，不传或空表示全部，位于请求body中\n" +
            "    - idArray: 单个选择的激活码id数组，非必传，位于请求body中\n\n" +
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
            "    \"data\": \n" +
            "        {\n" +
            "            \"succCount\": 2\n" +
            "        }\n" +
            "    ,\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String SUMMARY_PARENTCODE = "汇总母码信息\n" +
            "- 接口地址：/activationCode/summaryParentCode\n" +
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
            "    \"data\": \n" +
            "        {\n" +
            "            \"total\": 200,\n" +
            "            \"used\": 100,\n" +
            "            \"noUsed\": 100,\n" +
            "            \"qrCodeUrl\": \"http://repay.s-pos.cn/merchant/checkNFCActivationCode?activationCode=aaaaaaaa\",\n" +
            "            \"source\": \"1\"\n" +
            "        }\n" +
            "    ,\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String GET_ACT_CODE_POSTERALI_IMG_BASE64 = "获取激活码海报图片Base64\n" +
            "- 接口地址：/activationCode/getActCodePosteraliImgBase64\n" +
            "- 请求参数\n" +
            "    - source: 请求来源，必传，0：激活码详情页面、1：母码汇总页面，位于请求body中\n" +
            "    - codeId: 激活码id，当source为0时必传，位于请求body中\n" +
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
            "    \"data\": \"iVBORw0KGgoAAAANSUhEUgAABn4AAAPnCAYAAAABFzLdAACAAElEQVR42uzd\",\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";
}
