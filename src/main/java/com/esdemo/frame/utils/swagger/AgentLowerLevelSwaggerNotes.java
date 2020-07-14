package com.esdemo.frame.utils.swagger;

/**
 * 代理商开设下级文档描述
 */
public final class AgentLowerLevelSwaggerNotes {

    public static final String AGENT_LOWER_LEVEL_DOC = "代理管理代理商分页查询下级\n" +
            "- 请求参数\n" +
            "    - agentNo: 筛选代理编号\n" +
            "    - createDateBegin: 创建开始时间\n" +
            "    - createDateEnd: 创建结束时间\n" +
            "    - lowerStatus: 是否包含下级 1全链条2本级3仅直属下级\n" +
            "- 返回参数\n" +
            "    - id: id\n" +
            "    - agentNo: 代理商编号\n" +
            "    - agentName: 代理商名称\n" +
            "    - agentNode: 代理商节点\n" +
            "    - mobilephone: 手机号\n" +
            "    - status: 状态 正常-1，关闭进件-0，冻结-2\n" +
            "    - createDate: 创建时间\n" +
            "    - count: 总数据条数,第一页的时候该值有效,其他页为0\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [{\n" +
            "        \"id\": 2236,\n" +
            "        \"agentNo\": \"1009076\",\n" +
            "        \"agentName\": \"新数据测试二级代理商\",\n" +
            "        \"agentNode\": \"0-1009067-1009076-\",\n" +
            "        \"mobilephone\": \"17777777469\",\n" +
            "        \"status\": \"1\",\n" +
            "        \"createDate\": \"2020-05-08 11:50:54\"\n" +
            "    }],\n" +
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String AGENT_LOWER_LEVEL_TOBESET_DOC = "分页查询代理商所有待设置下级\n" +
            "- 返回参数\n" +
            "    - id: id\n" +
            "    - agentNo: 代理商编号\n" +
            "    - agentName: 代理商名称\n" +
            "    - agentNode: 代理商节点\n" +
            "    - mobilephone: 手机号\n" +
            "    - status: 状态 正常-1，关闭进件-0，冻结-2\n" +
            "    - createDate: 创建时间\n" +
            "    - toBeSetStatus: 待设置状态 1未完成认证及初始结算价修改 2初始结算价未修改\n" +
            "    - count: 总数据条数,第一页的时候该值有效,其他页为0\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": [{\n" +
            "        \"id\": 2236,\n" +
            "        \"agentNo\": \"1009076\",\n" +
            "        \"agentName\": \"新数据测试二级代理商\",\n" +
            "        \"agentNode\": \"0-1009067-1009076-\",\n" +
            "        \"mobilephone\": \"17777777469\",\n" +
            "        \"status\": \"1\",\n" +
            "        \"createDate\": \"2020-05-08 11:50:54\",\n" +
            "        \"toBeSetStatus\": \"1\"\n" +
            "    }],\n" +
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}";
    public static final String SETAGENTTOBESETIGNORE_DOC = "待设置代理商忽略操作\n" +
            "- 请求参数\n" +
            "    - agentNo: 操作的代理商编号\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"忽略操作成功!\"\n" +
            "    \"success\": true\n" +
            "}";

    public static final String GETAGENTDETAILEDIT_DOC = "代理商详情设置数据下发\n" +
            "- 请求参数\n" +
            "    - agentNo: 选中的代理商编号\n" +
            "- 返回参数\n" +
            "    - id: id\n" +
            "    - agentInfo: 当前代理商详情\n" +
            "    - bpList: 当前代理商的业务产品\n" +
            "    - happyBack:  当前代理商的欢乐返列表\n" +
            "    - newHappyGive:  当前代理商的新欢乐送列表\n" +
            "    - bpListDetail: 详情时,显示的数据\n" +
            "    - happyBackDetail: 详情时,显示的数据\n" +
            "    - newHappyGiveDetail: 详情时,显示的数据\n" +
            "- 例子\n" +
            " {\n" +
            "  \"code\": 200,\n" +
            "  \"message\": \"\",\n" +
            "  \"data\": {\n" +
            "    \"agentInfo\": {\n" +
            "      \"id\": 102, \n" +
            "      \"agentNo\": \"109\", --代理商编号\n" +
            "      \"agentName\": \"test-安收宝二级-xy\", --代理商名称\n" +
            "      \"agentNode\": \"0-108-109-\", --代理商节点\n" +
            "      \"agentLevel\": \"2\", --代理商级别\n" +
            "      \"parentId\": \"108\", --上级代理商ID\n" +
            "      \"oneLevelId\": \"108\", --一级代理商ID\n" +
            "      \"mobilephone\": \"14700000001\", --手机号\n" +
            "      \"status\": \"1\", --状态：正常-1，关闭进件-0，冻结-2\n" +
            "      \"createDate\": \"2019-03-18 09:39:35\", --创建时间\n" +
            "      \"toBeSetStatus\": null, \n" +
            "      \"lowerStatus\": \"1\" --操作代理商是否是当前登入代理商的直属下级 1是 0否\n" +
            "    },\n" +
            "    \"bpList\": [\n" +
            "      {\n" +
            "        \"bpId\": 5, --业务产品ID\n" +
            "        \"agentShowName\": \"安收宝无卡支付\", --业务产品展示名称\n" +
            "        \"teamId\": \"110010\", --组织ID\n" +
            "        \"teamName\": \"安收宝\", --组织名称\n" +
            "        \"allowIndividualApply\": \"1\", --是否队长 1队长0队员\n" +
            "        \"effectiveStatus\": \"1\",\n" +
            "        \"agentNo\": \"109\", --代理商编号\n" +
            "        \"groupNo\": null, --组编号\n" +
            "        \"lowerStatus\": \"1\", --当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选" +
            "        \"agentShare\": [\n" +
            "          {\n" +
            "            \"id\": \"5-3-0-0\", --服务合并ID\n " +
            "            \"cost\": 0.1, --代理商成本\n" +
            "            \"share\": 100, --分润百分比\n" +
            "            \"cashOutStatus\": 1, --是否是提现服务\n" +
            "            \"serviceName\": \"安收宝银联二维码支付提现\" --服务名称\n" +
            "            \"isPriceUpdate\": 0, --代理商底价仅可修改比例 0.否 1.是\n" +
            "            \"parentValue\": {  --上级的数据值\n" +
            "              \"cost\": 1,\n" +
            "              \"share\": 100\n" +
            "             }" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bpListDetail\": [\n" +
            "      {\n" +
            "        \"bpId\": 5, --业务产品ID\n" +
            "        \"agentShowName\": \"安收宝无卡支付\", --业务产品展示名称\n" +
            "        \"teamId\": \"110010\", --组织ID\n" +
            "        \"teamName\": \"安收宝\", --组织名称\n" +
            "        \"allowIndividualApply\": \"1\", --是否队长 1队长0队员\n" +
            "        \"effectiveStatus\": \"1\",\n" +
            "        \"agentNo\": \"108\", --代理商编号\n" +
            "        \"groupNo\": null, --组编号\n" +
            "        \"agentShare\": [\n" +
            "          {\n" +
            "            \"id\": \"5-3-0-0\",\n" +
            "            \"cost\": 0.1,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 1,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付提现\"\n" +
            "            \"isPriceUpdate\": 0, --代理商底价仅可修改比例 0.否 1.是\n" +
            "            \"parentValue\": {  --上级的数据值\n" +
            "              \"cost\": 1,\n" +
            "              \"share\": 100\n" +
            "             }" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"happyBack\": [\n" +
            "      {\n" +
            "        \"id\": 2512,\t\n" +
            "        \"activityTypeNo\": \"00006\", --欢乐返子类型编号\n" +
            "        \"subType\": \"1\", --欢乐返子类型 1欢乐返 2新欢乐送\n" +
            "        \"agentNo\": \"109\",\t--代理商编号\n" +
            "        \"agentNode\": \"0-108-109-\", --代理商节点\n" +
            "        \"cashBackAmount\": 0,\t--返现金额\n" +
            "        \"taxRate\": 0, --税额百分比\n" +
            "        \"createTime\": \"2020-02-28 15:17:51\", --创建时间\n" +
            "        \"fullPrizeAmount\": 0,\t--注册满奖金额\n" +
            "        \"notFullDeductAmount\": 0,\t--注册不满扣金额\n" +
            "        \"oneRewardAmount\": 0, --新欢乐送第1次考核奖励\n" +
            "        \"twoRewardAmount\": 0, --新欢乐送第2次考核奖励\n" +
            "        \"threeRewardAmount\": 0, --新欢乐送第3次考核奖励\n" +
            "        \"fourRewardAmount\": 0,  --新欢乐送第4次考核奖励\n" +
            "        \"deductionAmount\": 0,\t--新欢乐送不达标扣款\n" +
            "        \"rewardRate\": 1, --满奖比例\n" +
            "        \"transAmount\": 88, --交易金额\n" +
            "        \"activityTypeName\": \"欢乐返-循环送98\", --欢乐返子类型名称\n" +
            "        \"teamId\": 110010, --组织ID\n" +
            "        \"teamName\": \"安收宝\",  --组织名称\n" +
            "        \"groupNo\": null, --分组编号\n " +
            "        \"parentValue\": {\n  --上级数据对应" +
            "           \"cashBackAmount\": 0,\n" +
            "           \"taxRate\": 1,\n" +
            "           \"fullPrizeAmount\": 19,\n" +
            "           \"notFullDeductAmount\": 20,\n" +
            "           \"oneRewardAmount\": null,\n" +
            "           \"twoRewardAmount\": null,\n" +
            "           \"threeRewardAmount\": null,\n" +
            "           \"fourRewardAmount\": null,\n" +
            "           \"deductionAmount\": 0,\n" +
            "           \"rewardRate\": 1\n" +
            "        },\n" +
            "        \"lockStatus\": 1 -- 当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选" +
            "      }\n" +
            "    ],\n" +
            "    \"newHappyGive\": [\n" +
            "      {\n" +
            "        \"id\": 2166,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"parentValue\": {\n  --上级数据对应" +
            "           \"cashBackAmount\": 0,\n" +
            "           \"taxRate\": 1,\n" +
            "           \"fullPrizeAmount\": 19,\n" +
            "           \"notFullDeductAmount\": 20,\n" +
            "           \"oneRewardAmount\": null,\n" +
            "           \"twoRewardAmount\": null,\n" +
            "           \"threeRewardAmount\": null,\n" +
            "           \"fourRewardAmount\": null,\n" +
            "           \"deductionAmount\": 0,\n" +
            "           \"rewardRate\": 1\n" +
            "        },\n" +
            "        \"lockStatus\": 1, -- 当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选" +
            "        \"groupNo\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"happyBackDetail\": [\n" +
            "      {\n" +
            "        \"id\": 2511,\n" +
            "        \"activityTypeNo\": \"00006\",\n" +
            "        \"subType\": \"1\",\n" +
            "        \"agentNo\": \"108\",\n" +
            "        \"agentNode\": \"0-108-\",\n" +
            "        \"cashBackAmount\": 80,\n" +
            "        \"taxRate\": 1,\n" +
            "        \"createTime\": \"2020-02-28 15:17:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": null,\n" +
            "        \"twoRewardAmount\": null,\n" +
            "        \"threeRewardAmount\": null,\n" +
            "        \"fourRewardAmount\": null,\n" +
            "        \"deductionAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 88,\n" +
            "        \"activityTypeName\": \"欢乐返-循环送98\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null,\n" +
            "        \"parentValue\": {\n  --上级数据对应" +
            "           \"cashBackAmount\": 0,\n" +
            "           \"taxRate\": 1,\n" +
            "           \"fullPrizeAmount\": 19,\n" +
            "           \"notFullDeductAmount\": 20,\n" +
            "           \"oneRewardAmount\": null,\n" +
            "           \"twoRewardAmount\": null,\n" +
            "           \"threeRewardAmount\": null,\n" +
            "           \"fourRewardAmount\": null,\n" +
            "           \"deductionAmount\": 0,\n" +
            "           \"rewardRate\": 1\n" +
            "        },\n" +
            "        \"lockStatus\": 1 -- 当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选" +
            "        \"fullPrizeSwitch\": 1,  --欢乐返满奖开关，1开启0关闭\n" +
            "        \"notFullDeductSwitch\": 1, --欢乐返不满扣开关开关，1开启0关闭\n" +
            "        \"rewardLevel\": null \n -- 新欢乐送活动考核周期层数0表示没有，1显示第一考核周期,2显示第一，二考核周期,如此类推"+
            "        \"deductionStatus\": null, --新欢乐送 不达标扣款设置 1.显示 0不显示\n" +
            "        \"activityValueSameStatus\": 1, --活动同组数据是否一致，1一致，0不一致\n" +
            "      }\n" +
            "    ],\n" +
            "    \"newHappyGiveDetail\": [\n" +
            "      {\n" +
            "        \"id\": 2165,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"108\",\n" +
            "        \"agentNode\": \"0-108-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 1,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": null,\n" +
            "        \"notFullDeductAmount\": null,\n" +
            "        \"oneRewardAmount\": 10,\n" +
            "        \"twoRewardAmount\": 20,\n" +
            "        \"threeRewardAmount\": 30,\n" +
            "        \"fourRewardAmount\": 40,\n" +
            "        \"deductionAmount\": 1,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null,\n" +
            "        \"parentValue\": {\n  --上级数据对应" +
            "           \"cashBackAmount\": 0,\n" +
            "           \"taxRate\": 1,\n" +
            "           \"fullPrizeAmount\": 19,\n" +
            "           \"notFullDeductAmount\": 20,\n" +
            "           \"oneRewardAmount\": null,\n" +
            "           \"twoRewardAmount\": null,\n" +
            "           \"threeRewardAmount\": null,\n" +
            "           \"fourRewardAmount\": null,\n" +
            "           \"deductionAmount\": 0,\n" +
            "           \"rewardRate\": 1\n" +
            "        },\n" +
            "        \"lockStatus\": 1 -- 当前代理商是否已经勾选该数据 1已经勾选过 0 未勾选" +
            "        \"fullPrizeSwitch\": null,  --欢乐返满奖开关，1开启0关闭\n" +
            "        \"notFullDeductSwitch\": null, --欢乐返不满扣开关开关，1开启0关闭\n" +
            "        \"rewardLevel\": 4 \n -- 新欢乐送活动考核周期层数0表示没有，1显示第一考核周期,2显示第一，二考核周期,如此类推"+
            "        \"deductionStatus\": 1, --新欢乐送 不达标扣款设置 1.显示 0不显示\n" +
            "        \"activityValueSameStatus\": 1, --活动同组数据是否一致，1一致，0不一致\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"count\": 0,\n" +
            "  \"success\": true\n" +
            "}";
    public static final String GETBINDINGSETTLEMENTCARDBEFOREDATA_DOC = "本级绑定结算卡下发数据\n" +
            "- 返回参数\n" +
            "    - agentNo: 代理商编号\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": {\n" +
            "           \"id\": 101,\n" +
            "           \"agentNo\": \"108\", --代理商编号\n" +
            "           \"agentName\": \"test-安收宝-xy\", --代理商名称\n" +
            "           \"agentNode\": \"0-108-\", --代理商节点\n" +
            "           \"agentLevel\": \"1\", --代理商级别\n" +
            "           \"parentId\": \"0\", --上级代理商ID\n" +
            "           \"oneLevelId\": \"108\", --一级代理商ID\n" +
            "           \"accountName\": \"张三\", --开户名\n" +
            "           \"idCardNo\": \"36078198809030022\" --身份证,不打码\n" +
            "    },\n" +
            "    \"count\": 0,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String GETAGENTDETAILADD_DOC = "新增下级代理商下发前置数据\n" +
            "- 请求参数\n" +
            "- 返回参数\n" +
            "    - bpListParent: 上级代理商的业务产品\n" +
            "    - happyBackParent: 上级代理商的欢乐返上级列表 ,满奖满扣的开关都在上级代理List里面\n" +
            "    - newHappyGiveParent: 上级代理商的新欢乐送上级列表 ,新欢乐送活动的考核周期都在上级代理List里面\n" +
            "- 例子\n" +
            " {\n" +
            "  \"code\": 200,\n" +
            "  \"message\": \"\",\n" +
            "  \"data\": {\n" +
            "    \"bpListParent\": [\n" +
            "      {\n" +
            "        \"bpId\": 5, --业务产品ID\n" +
            "        \"agentShowName\": \"安收宝无卡支付\", --业务产品展示名称\n" +
            "        \"teamId\": \"110010\", --组织ID\n" +
            "        \"teamName\": \"安收宝\", --组织名称\n" +
            "        \"allowIndividualApply\": \"1\", --是否队长 1队长0队员\n" +
            "        \"effectiveStatus\": \"1\",\n" +
            "        \"agentNo\": \"108\", --代理商编号\n" +
            "        \"groupNo\": null, --组编号\n" +
            "        \"agentShare\": [\n" +
            "           {\n" +
            "            \"id\": \"6-12-1-0,6-12-2-0\", --服务合并ID\n" +
            "            \"cost\": 0.55, --代理商成本\n" +
            "            \"share\": 100, --分润百分比\n" +
            "            \"cashOutStatus\": 0, --是否是提现服务\n" +
            "            \"serviceName\": \"安收宝MPOS-POS刷卡I 借记卡/贷记卡\" --服务名称\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"happyBackParent\": [\n" +
            "      {\n" +
            "        \"id\": 2512,\t\n" +
            "        \"activityTypeNo\": \"00006\", --欢乐返子类型编号\n" +
            "        \"subType\": \"1\", --欢乐返子类型 1欢乐返 2新欢乐送\n" +
            "        \"agentNo\": \"109\",\t--代理商编号\n" +
            "        \"agentNode\": \"0-108-109-\", --代理商节点\n" +
            "        \"cashBackAmount\": 0,\t--返现金额\n" +
            "        \"taxRate\": 0, --税额百分比\n" +
            "        \"createTime\": \"2020-02-28 15:17:51\", --创建时间\n" +
            "        \"fullPrizeAmount\": 0,\t--注册满奖金额\n" +
            "        \"notFullDeductAmount\": 0,\t--注册不满扣金额\n" +
            "        \"oneRewardAmount\": 0, --新欢乐送第1次考核奖励\n" +
            "        \"twoRewardAmount\": 0, --新欢乐送第2次考核奖励\n" +
            "        \"threeRewardAmount\": 0, --新欢乐送第3次考核奖励\n" +
            "        \"fourRewardAmount\": 0,  --新欢乐送第4次考核奖励\n" +
            "        \"deductionAmount\": 0,\t--新欢乐送不达标扣款\n" +
            "        \"rewardRate\": 1, --满奖比例\n" +
            "        \"transAmount\": 88, --交易金额\n" +
            "        \"activityTypeName\": \"欢乐返-循环送98\", --欢乐返子类型名称\n" +
            "        \"teamId\": 110010, --组织ID\n" +
            "        \"teamName\": \"安收宝\",  --组织名称\n" +
            "        \"groupNo\": null, --分组编号\n" +
            "        \"fullPrizeSwitch\": 1,  --欢乐返满奖开关，1开启0关闭\n" +
            "        \"notFullDeductSwitch\": 1, --欢乐返不满扣开关开关，1开启0关闭\n" +
            "        \"rewardLevel\": null, \n  --新欢乐送活动考核周期层数0表示没有，1显示第一考核周期,2显示第一，二考核周期,如此类推\n" +
            "        \"deductionStatus\": null, --新欢乐送 不达标扣款设置 1.显示 0不显示\n" +
            "        \"activityValueSameStatus\": null, --活动同组数据是否一致，1一致，0不一致\n" +
            "      }\n" +
            "    ],\n" +
            "    \"newHappyGiveParent\": [\n" +
            "      {\n" +
            "        \"id\": 2165,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"108\",\n" +
            "        \"agentNode\": \"0-108-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 1,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": null,\n" +
            "        \"notFullDeductAmount\": null,\n" +
            "        \"oneRewardAmount\": 10,\n" +
            "        \"twoRewardAmount\": 20,\n" +
            "        \"threeRewardAmount\": 30,\n" +
            "        \"fourRewardAmount\": 40,\n" +
            "        \"deductionAmount\": 1,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null,\n " +
            "        \"fullPrizeSwitch\": null,\n" +
            "        \"notFullDeductSwitch\": null,\n" +
            "        \"rewardLevel\": 4 , -- 新欢乐送活动考核周期层数0表示没有，1显示第一考核周期,2显示第一，二考核周期,如此类推\n" +
            "        \"deductionStatus\": 1, --新欢乐送 不达标扣款设置 1.显示 0不显示\n" +
            "        \"activityValueSameStatus\": null, --活动同组数据是否一致，1一致，0不一致\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"count\": 0,\n" +
            "  \"success\": true\n" +
            "}";

    public static final String SETBINDINGSETTLEMENTCARD_DOC = "本级绑定结算卡\n" +
            "- 请求参数\n" +
            "    - accountName: 开户名\n" +
            "    - accountNo: 银行卡号\n" +
            "    - accountPhone: 银行预留手机号\n" +
            "    - accountProvince: 开户行地区:省\n" +
            "    - accountCity: 开户行地区:市\n" +
            "    - accountType: 账号类型 1对公 2对私\n" +
            "    - agentNo: 代理商编号\n" +
            "    - bankName: 开户行全称\n" +
            "    - cnapsNo: 联行行号\n" +
            "    - idCardNo: 身份证号(如果已经有,可以不传)\n" +
            "    - subBank: 支行\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"绑定结算卡成功\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String CHECKAGENTBASE_DOC = "新增下级代理商-代理商基础信息校验\n" +
            "- 请求参数\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "- 参数\n" +
            "{\n" +
            "\"agentInfo\": {\n" +
            "   \"agentName\": \"许亚\", --代理商名称\n" +
            "   \"mobilephone\": \"123456\", --代理商手机号\n" +
            "   \"province\": \"广东\", --省\n" +
            "   \"city\": \"深圳市\", --市\n" +
            "   \"area\": \"宝安区\", --区\n" +
            "   \"address\": \"地址\", --地址\n" +
            "   \"email\": \"2\", --email\n" +
            "   \"saleName\": \"108\" --销售人员\n" +
            "   }\n" +
            "}\n" +
            "- 结果" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"OK\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String CHECKAGENTANDCARD_DOC = "新增下级代理商-结算卡校验\n" +
            "- 请求参数\n" +
            "    - accountName: 开户名\n" +
            "    - accountNo: 身份证号\n" +
            "    - accountProvince: 开户行地区:省\n" +
            "    - accountCity: 开户行地区:市\n" +
            "    - accountType: 账号类型 1对公 2对私\n" +
            "    - bankName: 开户行全称\n" +
            "    - cnapsNo: 联行行号\n" +
            "    - idCardNo: 身份证号\n" +
            "    - subBank: 支行\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "- 参数\n" +
            "{\n" +
            "\t\"accountCard\": {\n" +
            "\t\t\"accountName\": \"许亚\",\n" +
            "\t\t\"accountNo\": \"6222024000067084992\",\n" +
            "\t\t\"accountProvince\": \"广东\",\n" +
            "\t\t\"accountCity\": \"深圳市\",\n" +
            "\t\t\"accountType\": \"2\",\n" +
            "\t\t\"bankName\": \"中国工商银行\",\n" +
            "\t\t\"cnapsNo\": \"305584018221\",\n" +
            "\t\t\"idCardNo\": \"111111111111111111\",\n" +
            "\t\t\"subBank\": \"展示\"\n" +
            "\t}\n" +
            "}\n" +
            "- 结果" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"OK\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}";

    public static final String CHECKBPSERVICEEDIT_DOC = "修改时-校验业务产品服务费率设置\n" +
            "- 请求参数\n" +
            "    - agentNo: 操作代理编号\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "- 参数\n" +
            "{\n" +
            "\"bpList\": [\n" +
            "      {\n" +
            "        \"bpId\": 5,\n" +
            "        \"allowIndividualApply\": \"1\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\": [\n" +
            "          {\n" +
            "            \"id\": \"5-3-0-0\",\n" +
            "            \"cost\": 0.1,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 1,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付提现\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"5-4-0-0\",\n" +
            "            \"cost\": 0.55,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 0,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "\t  {\n" +
            "        \"bpId\": 6,\n" +
            "        \"allowIndividualApply\": \"0\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\":null\n" +
            "      }\n" +
            "\t  ]\n" +
            "}\n" +
            "- 结果\n" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"OK\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}\n" +
            "- 错误结果\n" +
            "{\n" +
            "\t\"code\": 3000,\n" +
            "\t\"message\": \"总的错误提示语\",\n" +
            "\t\"data\": {\n" +
            "\t\t\"errorMap\": {\n" +
            "\t\t\t\"id\": null,\n" +
            "\t\t\t\"msg\": \"总的错误提示语\",\n" +
            "\t\t\t\"code\": \"3000\"\n" +
            "\t\t},\n" +
            "\t\t\"bpErrorMap\": {\n" +
            "\t\t\t\"5-4-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-4-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"5-3-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-3-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t},\n" +
            "\t\"count\": 0,\n" +
            "\t\"success\": false\n" +
            "}";

    public static final String CHECKBPSERVICEADD_DOC = "新增下级代理商-校验业务产品服务费率设置\n" +
            "- 请求参数\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "- 参数\n" +
            "{\n" +
            "\"bpList\": [\n" +
            "      {\n" +
            "        \"bpId\": 5,\n" +
            "        \"allowIndividualApply\": \"1\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\": [\n" +
            "          {\n" +
            "            \"id\": \"5-3-0-0\",\n" +
            "            \"cost\": 0.1,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 1,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付提现\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"5-4-0-0\",\n" +
            "            \"cost\": 0.55,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 0,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "\t  {\n" +
            "        \"bpId\": 6,\n" +
            "        \"allowIndividualApply\": \"0\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\":null\n" +
            "      }\n" +
            "\t  ]\n" +
            "}\n" +
            "- 结果" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"OK\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}\n" +
            "- 错误结果\n" +
            "{\n" +
            "\t\"code\": 3000,\n" +
            "\t\"message\": \"总的错误提示语\",\n" +
            "\t\"data\": {\n" +
            "\t\t\"errorMap\": {\n" +
            "\t\t\t\"id\": null,\n" +
            "\t\t\t\"msg\": \"总的错误提示语\",\n" +
            "\t\t\t\"code\": \"3000\"\n" +
            "\t\t},\n" +
            "\t\t\"bpErrorMap\": {\n" +
            "\t\t\t\"5-4-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-4-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"5-3-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-3-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t},\n" +
            "\t\"count\": 0,\n" +
            "\t\"success\": false\n" +
            "}";


    public static final String ADDAGENTLOWERLEVEL_DOC = "新增下级代理商\n" +
            "- 请求参数\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "- 参数\n" +
            "{\n" +
            "\"agentInfo\": {\n" +
            "   \"agentName\": \"许亚\", --代理商名称\n" +
            "   \"mobilephone\": \"123456\", --代理商手机号\n" +
            "   \"province\": \"广东\", --省\n" +
            "   \"city\": \"深圳市\", --市\n" +
            "   \"area\": \"宝安区\", --区\n" +
            "   \"address\": \"地址\", --地址\n" +
            "   \"email\": \"2\", --email\n" +
            "   \"saleName\": \"108\" --销售人员\n" +
            "   },\n" +
            "\"accountCard\": {\n" +
            "   \"accountName\": \"许亚\",\n" +
            "   \"accountNo\": \"6222024000067084992\",\n" +
            "   \"accountProvince\": \"广东\",\n" +
            "   \"accountCity\": \"深圳市\",\n" +
            "   \"accountType\": \"2\",\n" +
            "   \"bankName\": \"中国工商银行\",\n" +
            "   \"cnapsNo\": \"305584018221\",\n" +
            "   \"idCardNo\": \"111111111111111111\",\n" +
            "   \"subBank\": \"展示\"\n" +
            " },\n" +
            "\"bpList\": [\n" +
            "      {\n" +
            "        \"bpId\": 5,\n" +
            "        \"allowIndividualApply\": \"1\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\": [\n" +
            "          {\n" +
            "            \"id\": \"5-3-0-0\",\n" +
            "            \"cost\": 0.1,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 1,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付提现\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"5-4-0-0\",\n" +
            "            \"cost\": 0.55,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 0,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"bpId\": 6,\n" +
            "        \"allowIndividualApply\": \"0\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\":null\n" +
            "      }\n" +
            "   ],\n" +
            "\"happyBack\": [\n" +
            "      {\n" +
            "        \"id\": 2512,\n" +
            "        \"activityTypeNo\": \"00006\",\n" +
            "        \"subType\": \"1\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2020-02-28 15:17:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 88,\n" +
            "        \"activityTypeName\": \"欢乐返-循环送98\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2472,\n" +
            "        \"activityTypeNo\": \"00007\",\n" +
            "        \"subType\": \"1\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2020-02-28 14:37:03\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0.01,\n" +
            "        \"activityTypeName\": \"欢乐返128大pos\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"newHappyGive\": [\n" +
            "      {\n" +
            "        \"id\": 2166,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2166,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110030,\n" +
            "        \"teamName\": \"安收宝大pos\",\n" +
            "        \"groupNo\": null\n" +
            "      }\n" +
            "    ]" +
            "}" +
            "- 结果" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"OK\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}" +
            "- 校验错误返回结果" +
            "{\n" +
            "\t\"code\": 3000,\n" +
            "\t\"message\": \"总的错误提示语\",\n" +
            "\t\"data\": {\n" +
            "\t\t\"errorMap\": {\n" +
            "\t\t\t\"id\": null,\n" +
            "\t\t\t\"msg\": \"总的错误提示语\",\n" +
            "\t\t\t\"code\": \"3000\"\n" +
            "\t\t},\n" +
            "\t\t\"bpErrorMap\": {\n" +
            "\t\t\t\"5-4-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-4-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"5-3-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-3-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "   \"activityErrorMap\": {\n" +
            "       \"10001\": {\n" +
            "               \t\"cashBack\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型返现提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               },\n" +
            "                   \"fullPrize\": {\n" +
            "                  \"id\": \"10001\",\n" +
            "                      \"msg\": \"活动校验单个子类型满奖提示\",\n" +
            "                      \"code\": \"3\"\n" +
            "               },\n" +
            "               \"notFull\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型不满扣提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               }\n" +
            "       },\n" +
            "       \"10002\": {\n" +
            "               \t\"cashBack\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型返现提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               },\n" +
            "                   \"fullPrize\": {\n" +
            "                  \"id\": \"10001\",\n" +
            "                      \"msg\": \"活动校验单个子类型满奖提示\",\n" +
            "                      \"code\": \"3\"\n" +
            "               },\n" +
            "               \"notFull\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型不满扣提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               }\n" +
            "     }\n" +
            "\t},\n" +
            "\t\"count\": 0,\n" +
            "\t\"success\": false\n" +
            "}";


    public static final String EDITAGENTLOWERLEVEL_DOC = "修改服务费率和活动数据保存\n" +
            "- 请求参数\n" +
            "- 返回参数\n" +
            "    - 通用结果\n" +
            "- 例子\n" +
            "- 参数\n" +
            "{\n" +
            "\"bpList\": [\n" +
            "      {\n" +
            "        \"bpId\": 5,\n" +
            "        \"allowIndividualApply\": \"1\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\": [\n" +
            "          {\n" +
            "            \"id\": \"5-3-0-0\",\n" +
            "            \"cost\": 0.1,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 1,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付提现\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"5-4-0-0\",\n" +
            "            \"cost\": 0.55,\n" +
            "            \"share\": 100,\n" +
            "            \"cashOutStatus\": 0,\n" +
            "            \"serviceName\": \"安收宝银联二维码支付\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"bpId\": 6,\n" +
            "        \"allowIndividualApply\": \"0\",\n" +
            "        \"groupNo\": 1001,\n" +
            "        \"agentShare\":null\n" +
            "      }\n" +
            "   ],\n" +
            "\"happyBack\": [\n" +
            "      {\n" +
            "        \"id\": 2512,\n" +
            "        \"activityTypeNo\": \"00006\",\n" +
            "        \"subType\": \"1\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2020-02-28 15:17:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 88,\n" +
            "        \"activityTypeName\": \"欢乐返-循环送98\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2472,\n" +
            "        \"activityTypeNo\": \"00007\",\n" +
            "        \"subType\": \"1\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2020-02-28 14:37:03\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0.01,\n" +
            "        \"activityTypeName\": \"欢乐返128大pos\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"newHappyGive\": [\n" +
            "      {\n" +
            "        \"id\": 2166,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110010,\n" +
            "        \"teamName\": \"安收宝\",\n" +
            "        \"groupNo\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2166,\n" +
            "        \"activityTypeNo\": \"00060\",\n" +
            "        \"subType\": \"2\",\n" +
            "        \"agentNo\": \"109\",\n" +
            "        \"agentNode\": \"0-108-109-\",\n" +
            "        \"cashBackAmount\": 0,\n" +
            "        \"taxRate\": 0,\n" +
            "        \"createTime\": \"2019-12-13 09:41:51\",\n" +
            "        \"fullPrizeAmount\": 0,\n" +
            "        \"notFullDeductAmount\": 0,\n" +
            "        \"oneRewardAmount\": 0,\n" +
            "        \"twoRewardAmount\": 0,\n" +
            "        \"threeRewardAmount\": 0,\n" +
            "        \"fourRewardAmount\": 0,\n" +
            "        \"deductionAmount\": 0,\n" +
            "        \"merchantRewardAmount\": null,\n" +
            "        \"rewardRate\": 1,\n" +
            "        \"transAmount\": 0,\n" +
            "        \"activityTypeName\": \"君君新欢乐返0元\",\n" +
            "        \"teamId\": 110030,\n" +
            "        \"teamName\": \"安收宝大pos\",\n" +
            "        \"groupNo\": null\n" +
            "      }\n" +
            "    ]" +
            "}" +
            "- 结果" +
            "{\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"\",\n" +
            "    \"data\": \"OK\",\n"+
            "    \"count\": 1,\n" +
            "    \"success\": true\n" +
            "}" +
            "- 错误结果" +
            "{\n" +
            "  \"code\": 400,\n" +
            "  \"message\": \"存在结算价高于本级的服务,请检查是否输入有误\",\n" +
            "  \"data\": {\n" +
            "    \"errorMap\": {\n" +
            "      \"5-4-0-0\": {\n" +
            "        \"id\": \"5-4-0-0\",\n" +
            "        \"msg\": \"上级代理商服务费率设置异常\",\n" +
            "        \"code\": \"3\"\n" +
            "      },\n" +
            "      \"5-3-0-0\": {\n" +
            "        \"id\": \"5-3-0-0\",\n" +
            "        \"msg\": \"上级代理商服务费率设置异常\",\n" +
            "        \"code\": \"3\"\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"count\": 0,\n" +
            "  \"success\": false\n" +
            "}" +
            "- 校验错误返回结果" +
            "{\n" +
            "\t\"code\": 3000,\n" +
            "\t\"message\": \"总的错误提示语\",\n" +
            "\t\"data\": {\n" +
            "\t\t\"errorMap\": {\n" +
            "\t\t\t\"id\": null,\n" +
            "\t\t\t\"msg\": \"总的错误提示语\",\n" +
            "\t\t\t\"code\": \"3000\"\n" +
            "\t\t},\n" +
            "\t\t\"bpErrorMap\": {\n" +
            "\t\t\t\"5-4-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-4-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"5-3-0-0\": {\n" +
            "\t\t\t\t\"id\": \"5-3-0-0\",\n" +
            "\t\t\t\t\"msg\": \"服务单个校验提示语\",\n" +
            "\t\t\t\t\"code\": \"3\"\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "   \"activityErrorMap\": {\n" +
            "       \"10001\": {\n" +
            "               \t\"cashBack\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型返现提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               },\n" +
            "                   \"fullPrize\": {\n" +
                "                  \"id\": \"10001\",\n" +
            "                      \"msg\": \"活动校验单个子类型满奖提示\",\n" +
            "                      \"code\": \"3\"\n" +
            "               },\n" +
            "               \"notFull\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型不满扣提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               }\n" +
            "       },\n" +
            "       \"10002\": {\n" +
            "               \t\"cashBack\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型返现提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               },\n" +
            "                   \"fullPrize\": {\n" +
            "                  \"id\": \"10001\",\n" +
            "                      \"msg\": \"活动校验单个子类型满奖提示\",\n" +
            "                      \"code\": \"3\"\n" +
            "               },\n" +
            "               \"notFull\": {\n" +
            "                       \"id\": \"10001\",\n" +
            "                       \"msg\": \"活动校验单个子类型不满扣提示\",\n" +
            "                       \"code\": \"3\"\n" +
            "               }\n" +
            "     }\n" +
            "\t},\n" +
            "\t\"count\": 0,\n" +
            "\t\"success\": false\n" +
            "}";
}
