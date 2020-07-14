package com.esdemo.frame.utils.swagger;

import com.esdemo.frame.utils.Constants;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-13 10:52
 */
public final class SwaggerNotes {

    public static final String COMMON_LOGIN = "登陆操作\n" +
            "- 请求参数\n" +
            "    - userName: 手机号或邮箱\n" +
            "    - password: RSA加密后的密码(base64),例子中的密码明文是abc888888\n" +
            "       - 公钥: "+ Constants.LOGIN_PUBLIC_KEY  +"\n" +
            "    - agentOem: 代理商oem值 盛代宝为200010\n" +
            "- 返回参数\n" +
            "    - userId 用户id\n" +
            "    - agentNo 代理商编号\n" +
            "    - manage 是否管理员:1管理员0销售员2.店员\n" +
            "    - loginToken 登陆token\n" +
            "       - **注意** 之后其他的所有接口必须上传参数LOGIN_TOKEN" +
            "       该参数可以放在请求参数(request param)中,或者请求头中,或者cookie中\n" +
            "- 例子\n" +
            "{\n" +
            "\"userName\": \"16675564681\",\n" +
            "\"agentOem\": \"110010\",\n" +
            "\"password\": \"DmYwia3A8fJbgOMlj9Dy74pxesp2oFEqh1LwwfWV9XgcBT+kwVqIaUE+8gTGNWCdyY2/EMchnHixEBhIAjPDPGgQnN/nWnzjbu7shP6gdvRXFm0fOTd8v14tjCbLkTHE5AJK82qpROQ7wcKAjG6yCn+lkVfGj9CcALGEX2a80f4=\"\n" +
            "}\n\n" +
            "如果timestamp为1557993954511, 则签名参数为agentOem=200010&password=fgaS0lu4Wp4Ra1UjOIdqvzlurWVXwVyHGpZItzwABQp20WHKaNc3dEn2JGu3q0qra0d8nxkt5xbNJobcQQg+XMrGV5/sbVbAHIM9nD+4EH816liIVUDaQAFO1epPvy9HPzp+9Gdtkgv4sRJ61tM9gtLJGQtF0Mzn8IfXd4dfJUM=&timestamp=1557993954511&userName=13888888888&key=46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty\n\n" +
            "md5之前签名值a16899c984018a1c3da7a71d9fca87e5,将该值放在公共参数中";

    public static final String LIST_MERCHANT_INFO = "查询商户信息\n" +
            "- 分页信息\n" +
            "   - pageNo: 从0开始\n" +
            "   - pageSize: 分页条数\n" +
            "- 查询参数\n" +
            "   - agentNo (筛选界面选择代理商)登陆代理商下级代理商编号\n" +
            "   - agentNode (筛选界面选择代理商)登陆代理商下级代理商节点\n" +
            "   - theOpenBpId 开通业务产品\n" +
            "   - teamId 所属组织\n" +
            "   - teamEntryId 所属子组织\n" +
            "   - startTransTime 交易开始时间\n" +
            "   - endTransTime 交易结束时间\n" +
            "   - minTransMoney 最小交易金额\n" +
            "   - maxTransMoney 最大交易金额\n" +
            "   - minOrderNum 最小交易笔数\n" +
            "   - maxOrderNum 最大交易笔数\n" +
            "   - province 省份\n" +
            "   - city 城市\n" +
            "   - district 区域\n" +
            "   - startCreateTime 商户开始创建时间\n" +
            "   - endCreateTime 商户结束创建时间\n" +
            "   - merchantStatus 商户状态\n" +
            "   - theUnOpenBpId 未开通的业务产品\n" +
            "   - hlfActive 激活状态 0未激活, 1已激活\n" +
            "   - mobilePhone 手机号\n" +
            "   - merchantName 商户名称\n" +
            "   - merchantNo 商户编号\n" +
            "   - searchKeywork 商户名(模糊), 商户号(精确), 手机号(精确)\n" +
            "   - recommendedSource 推广来源 \n" +
            "   - riskStatus 冻结状态 \n" +
            "   - queryScope 商户类型" +
            "       - ALL 如刷选界面没有选择代理商,则是登陆代理商下的所有商户,否则为选择代理商下的所有商户\n" +
            "       - OFFICAL  如刷选界面没有选择代理商,则是登陆代理商下的直营商户,否则为选择代理商下的直营商户\n" +
            "       - CHILDREN 如刷选界面没有选择代理商,则是登陆代理商下所有下级的商户(不包含直营商户),否则为选择代理商下的所有下级的商户(不包含直营商户)\n" +
            "   - searchType 查询类型,除QUERY外,上面所有的查询条件都无效,后台会根据查询类型自动构造查询条件\n" +
            "       - QUERY 通过筛选界面,选择条件查询\n" +
            "       - ALL 全部商户\n" +
            "       - QUALITY 优质商户       本月交易金额>=x元\n" +
            "       - ACTIVE 活跃商户       近x天交易笔数>=x笔且交易金额>=x元\n" +
            "       - UNCERTIFIED 未认证商户     身份未认证的商户\n" +
            "       - SLEEP 休眠商户       入网>=x天且连续>x天无交易\n" +
            "   - sortType 排序类型\n" +
            "       - DEFAULT_ORDER 默认排序: 按照商户的创建时间倒序排\n" +
            "       - CUR_MONTH_TRANS_ASC 本月交易量从低到高\n" +
            "       - CUR_MONTH_TRANS_DESC 本月交易量从高到底\n" +
            "       - LAST_MONTH_TRANS_ASC 上个月交易量从低到高\n" +
            "       - LAST_MONTH_TRANS_DESC 上个月交易量从高到底\n" +
            "       - ALL_TRANS_ASC 累积交易量从低到高 \n" +
            "       - ALL_TRANS_DESC 累积交易量从高到底\n" +
            "- 返回参数\n" +
            "   - merchantNo 商户编号\n" +
            "   - merchantName 商户名称\n" +
            "   - mobilePhone 手机号\n" +
            "   - transMoney 交易金额(根据不同排序类型得到不同的条件的交易金额)\n" +
            "";

    public static final String GET_MERCHANT_DETAILS = "查询商户详情\n" +
            "- 参数\n" +
            "   - 商户编号\n" +
            "- 返回值\n" +
            "   - isDirectMerchant: 是否为直营商户\n" +
            "   - openAgentUpdateBpSwitch: 是否允许代理商更改业务产品的开关\n" +
            "   - currentTransMoney: 本月交易量\n" +
            "   - currentTransTotal: 本月交易笔数\n" +
            "   - allTransMoney: 累积交易量\n" +
            "   - allTransTotal: 累积交易笔数\n" +
            "   - merchantInfo: 商户基本信息\n" +
            "       - merchantNo: 商户编号\n" +
            "       - merchantName: 商户名(非直营商户,不显示)\n" +
            "       - mobilephone: 手机号\n" +
            "       - agentNo: 所属代理商编号\n" +
            "       - agentName: 所属代理商名称\n" +
            "       - status: 0：商户关闭；1：正常；2 冻结\n" +
            "       - hlfActive: 0：未激活；1：已激活\n" +
            "   - sevenDayDatas: 近七日交易数据\n" +
            "       - key: 日期(yyyy-MM-dd)\n" +
            "       - value: 交易额\n" +
            "       - description: 暂无意义,都是为空字符串\n" +
            "   - halfYearDatas: 近半年交易数据\n" +
            "       - key: 日期(yyyy-MM)\n" +
            "       - value: 交易额\n" +
            "       - description: 暂无意义,都是为空字符串\n" +
            "   - bpDatas: 业务产品信息\n" +
            "       - bpId: 业务产品id\n" +
            "       - bpName: 业务名称\n" +
            "       - bpStatus: 业务产品状态值 1待一审 2待平台审核 3审核失败 4正常 5已转自动审件 0关闭\n" +
            "       - transAmount: 交易金额\n" +
            "   - hardwares: 机具\n" +
            "       - key: 机具sn\n" +
            "       - value: 机具名称\n" +
            "       - description: 机具状态,目前都是'已使用'\n" +
            "- 返回数据结构请查看[文档](./doc/merchant/get_merchant_details.html)";

    public static final String MERCHANT_SUMMARY = "商户汇总" +
            "- 参数 \n" +
            "   - queryScope: 查询返回(必须)\n" +
            "       - ALL: 全部\n" +
            "       - OFFICAL: 直属\n" +
            "       - CHILDREN: 下级\n" +
            "- 返回值:\n" +
            "   - teamList: 按组织类型分类汇总\n" +
            "      - teamEntry: 是否为子组织,true为子组织过滤查询传 teamEntryId, 否则传 teamId\n" +
            "      - typeId: 组织id\n" +
            "      - typeName: 组织名(盛钱包/盛pos)\n" +
            "      - total: 商户总数\n" +
            "      - activeNumber: 商户激活数\n" +
            "      - notActiveNumber: 未激活商户数\n" +
            "      - children: 明细\n" +
            "          - teamEntry: 是否为子组织,true为子组织过滤查询传 teamEntryId, 否则传 teamId\n" +
            "          - typeId: 业务产品id\n" +
            "          - typeName: 业务产品名称\n" +
            "          - total: 商户总数\n" +
            "          - activeNumber: 商户激活数\n" +
            "          - notActiveNumber: 未激活商户数\n" +
            "      - merchantTotal: 商户总数\n";

    public static final String COUNT_BY_DIRECT_AGENT = "按照代理商分类汇总\n" +
            "- 分页信息\n" +
            "   - pageNo: 页码 从1开始\n" +
            "   - pageSize: 每页条数\n" +
            "- 返回值:\n" +
            "   - typeId: 代理商编号\n" +
            "   - agentNode: 代理商节点\n" +
            "   - typeName: 代理商名称\n" +
            "   - total: 商户总数\n" +
            "   - activeNumber: 商户激活数\n" +
            "   - notActiveNumber: 未激活商户数\n";

    public static final String LIST_BUSINESS_PRODUCT = "列出代理商代理的业务产品\n" +
            "- 参数\n" +
            "   - agentNo: 商户查询筛选页面选择的代理商编号(如果不选择,这默认取登陆代理商开通的业务产品)\n" +
            "- 返回值\n" +
            "   - key: 业务产品id\n" +
            "   - value: 业务产品名称\n";

    public static final String GET_AGENT_PRODUCT_LIST = "查询代理商代理的产品\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号\n" +
            "- 返回值\n" +
            "   - agent_no: 代理商编号\n" +
            "   - id: id\n" +
            "   - bp_id: 业务产品编码\n" +
            "   - dp_name: 业务产品名称\n" +
            "   - remark: 业务说明\n";

    public static final String GET_AGENT_SHARE_API_LIST = "查询代理商分润列表\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号\n" +
            "- 返回值\n" +
            "   - agentNo: 代理商编号\n" +
            "   - id: long\n" +
            "   - serviceId: 服务ID\n" +
            "   - serviceName: 服务名称\n" +
            "   - cardType: 银行卡种类\n" +
            "   - holidaysMark: 节假日标志\n" +
            "   - profitType: 分润方式\n" +
            "   - profitTypeZh: 分润方式对应中文\n" +
            "   - serviceType: 服务类型 10000和10001是提现类, 其他是交易类\n" +
            "   - shareProfitPercent: 分润比例" +
            "   - shareSet: " +
            "   - costRate: 成本";

    public static final String GET_AGENT_RATE_LIST = "查询代理商费率列表\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号\n" +
            "- 返回值\n" +
            "   - agentNo: 代理商编号\n" +
            "   - id: long\n" +
            "   - serviceId: 服务ID\n" +
            "   - serviceName: 服务名称\n" +
            "   - cardType: 银行卡种类\n" +
            "   - holidaysMark: 节假日标志\n" +
            "   - merRate: 费率表达式\n" +
            "   - ratetype: 费率类型\n";

    public static final String GET_AGENT_QUOTA_LIST = "查询代理商的限额列表\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号\n" +
            "- 返回值\n" +
            "   - agentNo: 代理商编号\n" +
            "   - id: long\n" +
            "   - serviceId: 服务ID\n" +
            "   - serviceName: 服务名称\n" +
            "   - cardType: 银行卡种类\n" +
            "   - holidaysMark: 节假日标志\n" +
            "   - singleDayAmount: 单日最大交易额\n" +
            "   - singleMinAmount: 单笔最小交易额\n" +
            "   - singleCountAmount: 单笔最大交易额\n" +
            "   - singleDaycardAmount: 单日单卡最大交易额\n" +
            "   - singleDaycardCount: 单日单卡最大交易笔数\n";

    public static final String QUERY_AGENT_INFO_BYNO = "查询代理商基本信息\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号\n" +
            "- 返回值\n" +
            "   - 基本信息\n" +
            "   - id: id\n" +
            "   - agentNo: 代理商编号\n" +
            "   - agentName: 代理商名称\n" +
            "   - linkName: 联系人\n" +
            "   - email: 邮箱\n" +
            "   - mobilephone: 手机号\n" +
            "   - address: 地址\n" +
            "   - agentArea: 代理区域\n" +
            "   - status: 代理商状态 1：正常，0：关闭进件，2：冻结\n" +
            "   - 结算信息\n" +
            "   - accountType: 账户类型  1：对公，2：对私\n" +
            "   - accountName: 开户名\n" +
            "   - accountNo: 开户账户\n" +
            "   - bankName: 开户行全称\n" +
            "   - cnapsNo: 联行行号\n" +
            "   - 隐含信息\n" +
            "   - agentNode: 代理商节点\n" +
            "   - agentLevel: 代理商级别 1: 一级，2：二级\n" +
            "   - parentId: 上级代理商ID\n" +
            "   - parentName: 上级代理商名称\n" +
            "   - oneLevelId: 一级代理商ID\n" +
            "   - isOem: 是否OEM\n" +
            "   - teamId: \n" +
            "   - agentType: 代理商类型（直营代理商或OEM代理商：0为直营代理商，1为OEM代理商）\n";

    public static final String QUERY_AGENT_INFO_LIST = "条件查询\n" +
            "- 参数\n" +
            "   - agentName: 查询的代理商名称或编号\n" +
            "   - startDate: 创建日期开始日期\n" +
            "   - endDate: 创建日期结束日期\n" +
            "   - hasChild: ALL 全部,是;OFFICAL 直属;CHILDREN 直属外的下级;OTHERALL 是,当前登录代理商不包含自己,否则包含;SELF 否,查自己;必传\n" +
            "   - pageNo: int 必传\n" +
            "   - pageSize: int 必传\n" +
            "   - mobilephone: 手机号\n" +
            "- 返回值\n" +
            "   - count: 总计 pageNo=1该值才有效\n" +
            "   - id: id\n" +
            "   - agent_no: 代理商编号\n" +
            "   - agent_name: 代理商名称\n" +
            "   - link_name: 联系人\n" +
            "   - phone: 电话\n" +
            "   - mobilephone: 手机号\n" +
            "   - status: 代理商状态 1：正常，0：关闭进件，2：冻结\n" +
            "   - isDirectChild: 是否为直接下级 \"true\" : 是; \"false\": 否\n";

    public static final String GET_TERMINAL_APPLY_RECORD = "机具申请记录查询\n" +
            "- 参数\n" +
            "   - user_id: 调用查询的用户ID，代理商传代理商编号,必传\n" +
            "   - pageNo: 当前页\n" +
            "   - pageSize: 每页数量\n" +
            "   - record_status: 机具申请记录的状态(DCL:待处理，YCL:已处理)\n" +
            "- 返回值\n" +
            "   - pageNo: 当前页\n" +
            "   - pageSize: 每页数量\n" +
            "   - id: 记录ID\n" +
            "   - merchant_no: 商户号\n" +
            "   - status : 状态 0:待直属处理  1:已处理  2:待一级处理\n" +
            "   - product_type: 机具类型\n" +
            "   - create_time: 申请时间\n" +
            "   - address: 商户地址\n" +
            "   - mobilephone: 手机号\n" +
            "   - remark: 备注\n" +
            "   - update_time: 最后处理时间\n" +
            "   - merchant_name: 商户名称\n" +
            "   - need_operation: 是否需要处理(Y需要/N不需要)\n" +
            "   - agent_no_one: 一级代理商编号\n" +
            "   - agent_name_one: 一级代理商名称\n" +
            "   - agent_no_parent: 所属代理商编号\n" +
            "   - agent_name_parent: 所属代理商名称\n" +
            "   - bp_name: 机具类型对应的中文\n" +
            "   - sn: 处理时填写的机具SN号\n" +
            "   - count: 总记录数\n";

    public static final String UPDATE_MODIFY_TERMINAL_APPLY_RECORD = "更新机具申请记录\n" +
            "- 参数\n" +
            "   - user_id: 查询的用户ID，代理商传代理商编号,必传\n" +
            "   - record_id: 被修改的机具申请记录ID,必传\n" +
            "   - record_status: 被修改的机具申请记录的新状态,必传\n" +
            "   - record_remark: 被修改的机具申请记录的说明\n" +
            "   - ter_sn: 机具SN号,必传\n" +
            "   - merchant_no: 商户号,必传\n";

    public static final String COUNT_TERMINAL_APPLY_RECORD = "机具申请记录统计\n" +
            "- 参数\n" +
            "   - user_id: 调用查询的用户ID，代理商传代理商编号,必传\n" +
            "- 返回值\n" +
            "   - count: 条数\n";

    public static final String GET_ACQ_MER_MCC = "收单商户进件大类小类\n" +
            "- 参数\n" +
            "   - 无 \n" +
            "- 返回值\n" +
            "   - acqMerMccMapList \n" +
            "       - key : \n" +
            "       - value : \n" +
            "           - acqMerMccList : \n" +
            "               - key : \n" +
            "               - value : \n" +
            "               - specialIndustry : 特定行业需要显示的名称。默认为空字符串\"\" \n";

    public static final String SELECT_SURVEY_ORDER_DETAIL = "调单详情\n" +
            "- 参数\n" +
            "     - order_no: 调单编号\n" +
            "- 返回\n" +
            "     - surveyOrderDetail 调单详情\n" +
            "       - order_no: 调单编号,必传\n" +
            "       - trans_order_no: 订单编号\n" +
            "       - order_type_code: 调单,订单类型\n" +
            "       - order_service_code: 业务类型\n" +
            "       - acq_reference_no: 系统参考号\n" +
            "       - reply_status: 回复状态\n" +
            "       - deal_status: 处理状态\n" +
            "       - urge_num: 催单次数\n" +
            "       - merchant_no: 商户编号\n" +
            "       - trans_account_no: 交易卡号\n" +
            "       - trans_amount: 交易金额\n" +
            "       - amount: 结算金额\n" +
            "       - agent_node: 所属代理商节点\n" +
            "       - pay_method: 交易方式\n" +
            "       - trans_status: 交易状态\n" +
            "       - create_time: 发起时间\n" +
            "       - reply_end_time: 截止回复时间\n" +
            "       - deal_remark: 备注说明\n" +
            "       - order_type_code_value: 调单,订单类型\n" +
            "       - order_service_code_value: 业务类型\n" +
            "       - reply_status_value: 回复状态\n" +
            "       - deal_status_value: 处理状态\n" +
            "       - pay_method_value: 交易方式\n" +
            "       - trans_status_value: 交易状态\n" +
            "       - agent_name: 所属代理商名称\n" +
            "       - picsList: 图片\n" +
            "           - name: 名称\n" +
            "           - aliyun_url: 路径\n" +
            "     - replyDetail: 回复详情\n" +
            "       - reply_result: 回复结果\n" +
            "       - mer_name: 商户姓名\n" +
            "       - card_person_name: 持卡人姓名\n" +
            "       - card_person_mobile: 持卡人电话\n" +
            "       - real_name: 真实商户名称\n" +
            "       - province_city: 归属省市 province + city\n" +
            "       - trans_address: 真实交易地址\n" +
            "       - mer_mobile: 商户电话\n" +
            "       - reply_remark: 回复说明\n" +
            "       - create_time: 回复时间,创建时间(提交,修改回复时不用传)\n" +
            "       - picsList: 图片\n" +
            "           - name: 名称\n" +
            "           - aliyun_url: 路径\n" +
            "       - order_type_code_value: 调单,订单类型\n" +
            "       - order_service_code_value: 业务类型\n" +
            "       - reply_status_value: 回复状态\n" +
            "       - deal_status_value: 处理状态\n" +
            "       - pay_method_value: 交易方式\n" +
            "       - trans_status_value: 交易状态\n" +
            "       - reply_result_value: 回复结果\n" +
            "       - reply_files_name: 附件\n" +
            "       - reply_record_count: 是否有一条以上回复记录:1 >1条; 0 <= 1条\n" +
            "     - flag: true显示修改\n" +
            "     - hasReplyDetail: ios需要,回复详情为空返回false";

    public static final String SELECT_REPLY_RECORD = "调单回复记录\n" +
            "- 参数\n" +
            "   - order_no: 调单编号 ,必传\n" +
            "- 返回值\n" +
            "   - reply_result: 回复结果\n" +
            "   - mer_name: 商户姓名\n" +
            "   - card_person_name: 持卡人姓名\n" +
            "   - card_person_mobile: 持卡人电话\n" +
            "   - real_name: 真实商户名称\n" +
            "   - province_city: 归属省市 province + city\n" +
            "   - trans_address: 真实交易地址\n" +
            "   - mer_mobile: 商户电话\n" +
            "   - reply_remark: 回复说明\n" +
            "   - create_time: 回复时间,创建时间(提交,修改回复时不用传)\n" +
            "   - picsList: 图片\n" +
            "   - order_type_code_value: 调单,订单类型\n" +
            "   - order_service_code_value: 业务类型\n" +
            "   - reply_status_value: 回复状态\n" +
            "   - deal_status_value: 处理状态\n" +
            "   - pay_method_value: 交易方式\n" +
            "   - trans_status_value: 交易状态\n" +
            "   - reply_result_value: 回复结果\n" +
            "   - reply_files_name: 附件\n" +
            "   - reply_record_count: 是否有一条以上回复记录:1 >1条; 0 <= 1条\n";

    public static final String INSERT_OR_UPDATE_REPLY = "调单回复提交,修改\n" +
            "- 参数\n" +
            "   - params: 封装图片以外的参数\n" +
            "       - order_no: 调单号,必传\n" +
            "       - agent_node: 代理商节点,必传\n" +
            "       - agent_no: 当前登录代理商,必传\n" +
            "       - continueCommit: 是否继续提交,第一次点提交传false,确定提交再调一次接口传true,必传\n" +
            "       - replyDetail: \n" +
            "           - reply_result: 回复结果\n" +
            "           - mer_name: 商户姓名\n" +
            "           - card_person_name: 持卡人姓名\n" +
            "           - card_person_mobile: 持卡人电话\n" +
            "           - real_name: 真实商户名称\n" +
            "           - province_city: 归属省市 province + city\n" +
            "           - trans_address: 真实交易地址\n" +
            "           - mer_mobile: 商户电话\n" +
            "           - reply_remark: 回复说明\n" +
            "           - create_time: 回复时间,创建时间(提交,修改回复时不用传)\n" +
            "           - picsList: 图片\n" +
            "               - name: 名称\n" +
            "               - aliyun_url: 阿里云地址\n" +
            "           - order_type_code_value: 调单,订单类型\n" +
            "           - order_service_code_value: 业务类型\n" +
            "           - reply_status_value: 回复状态\n" +
            "           - deal_status_value: 处理状态\n" +
            "           - pay_method_value: 交易方式\n" +
            "           - trans_status_value: 交易状态\n" +
            "           - reply_result_value: 回复结果\n" +
            "           - reply_files_name: 附件\n" +
            "           - reply_record_count: 是否有一条以上回复记录:1 大于1条; 0 小于等于1条\n" +
            "   - fileList: 文件信息\n" +
            "       - fileName: 文件名称(包含后缀)\n" +
            "       - file: 文件内容\n" +
            "- 返回值\n" +
            "   - continueCommit: 如果继续提交返回true,确定提交时作为参数\n" +
            "   - status: 成功与否\n" +
            "   - msg: 成功或失败信息\n";

    public static final String SELECT_SURVEY_ORDER_BY_CONDITIONS = "调单管理条件查询\n" +
            "- 参数\n" +
            "   - pageNo: 当前页,从1开始\n" +
            "   - pageSize: 每页数量\n" +
            "   - agent_name: 代理商名称\n" +
            "   - merchant_no: 商户编号\n" +
            "   - order_no: 交易订单号,trans_order_no,如:SK075255281034407602\n" +
            "   - acq_reference_no: 系统参考号\n" +
            "   - trans_account_no: 交易卡号\n" +
            "   - order_type_code: 调单类型\n" +
            "   - order_service_code: 业务类型\n" +
            "   - reply_status: 回复状态\n" +
            "   - deal_status: 处理状态\n" +
            "   - flag: 是否包含下级 1:包含,0:不包含\n" +
            "   - pay_method: 交易方式\n" +
            "   - trans_status: 交易状态\n" +
            "   - create_time_start: 发起日期\n" +
            "   - create_time_end: 发起日期\n" +
            "   - reply_end_time_start: 回复截止时间\n" +
            "   - reply_end_time_end: 回复截止时间\n" +
            "   - agent_node: 查询条件节点\n" +
            "   - agent_no: 当前登录代理商编号,必填\n" +
            "- 返回值\n" +
            "   - order_no: 调单号\n" +
            "   - order_type_code: 调单,订单类型\n" +
            "   - acq_reference_no: 系统参考号\n" +
            "   - reply_status: 回复状态\n" +
            "   - deal_status: 处理状态\n" +
            "   - merchant_no: 商户编号\n" +
            "   - trans_account_no: 交易卡号\n" +
            "   - trans_status: 交易状态\n" +
            "   - create_time: 发起时间\n" +
            "   - reply_end_time: 截止回复时间\n" +
            "   - own_status: 操作商户权限 1:一级代理商可以查,修改;2:所属代理商可查可改;0:只能查\n" +
            "   - agent_node: 当前商户对应代理商节点\n" +
            "   - trans_order_no: 交易订单号\n" +
            "   - count: 条数\n";

    public static final String BASE_INFO_CHECK = "校验三码(开户名,身份证,结算卡号)认证\n" +
            "- 参数\n" +
            "   - account_name: 银行卡开户名,必传\n" +
            "   - account_no: 银行卡账号,必传\n" +
            "   - id_card_no: 身份证,必传\n" +
            "   - mobilephone: 手机号,必传\n" +
            "- 返回值\n" +
            "   - 验证状态\n";

    public static final String SELECT_AGENT_SHARE = "修改分润记录查询\n" +
            "- 参数\n" +
            "   - shareId: Long 分润规则id,必传,对应分润接口下发的id\n" +
            "- 返回值\n" +
            "   - shareId: 分润规则id\n" +
            "   - costHistory: 修改前代理商成本\n" +
            "   - cost: 修改后代理商成本\n" +
            "   - shareProfitPercentHistory: 修改前分润比例\n" +
            "   - shareProfitPercent: 修改后分润比例\n" +
            "   - efficientDate: 生效日期\n" +
            "   - effectiveStatus: 是否生效:0-未生效,1-已生效\n" +
            "   - updateDate: 修改日期\n";

    public static final String UPDATE_AGENT_SHARE = "修改分润\n" +
            "- 参数\n" +
            "   - entityId: 当前登录代理商编号,必传\n" +
            "   - agentNo: 被修改分润的代理商编号,必传\n" +
            "   - shareId: 分润规则id,必传\n" +
            "   - efficientDate: 生效日期,必传\n" +
            "   - shareProfitPercent: 代理商固定分润百分比,页面上分润比例字段,必传\n" +
            "   - cost: 代理商成本扣率,代理商成本每笔固定值,页面上代理商成本字段,必传\n" +
            "   - profitType: 分润方式:1-每笔固定收益额；2-每笔固定收益率；3-每笔固定收益率+保底封顶；4-每笔固定收益金额+固定收益率5-商户签约费率与代理商成本费率差额百分比分润；6-商户签约费率与代理商成本费率差额按交易量阶梯百分比分润）（二级及往下的代理商只有前4种）\n" +
            "- 返回值\n" +
            "   - 状态信息\n";

    public static final String GET_MPAGE_INFO_RPC = "商户进件列表查询\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号,筛选条件,没有就传当前登录代理商的\n" +
            "   - include_son : \n" +
            "       - 1.包含所有 : 当前查询代理商整个链条的商户\n" +
            "       - 2.不包含:  当前查询代理商的直属商户\n" +
            "       - 3.仅直属下级: 当前查询代理商的直属代理商的直属商户\n" +
            "       - 传空字符为包含所有\n" +
            "   - merchant_name: 筛选商户条件，支持编号，名称，手机号\n" +
            "   - merchant_status: 状态 1待一审  2待平台审核  3审核失败  4正常  全部传空\n" +
            "   - create_start_date: 创建日期开始日期 列如：2020-06-10 格式\n" +
            "   - create_end_date: 创建日期结束日期 列如：2020-06-10 格式 \n" +
            "   - bp_id: 业务产品\n" +
            "   - pos_type: 机具设备类型\n" +
            "   - pageNo: 必传,从1开始\n" +
            "   - pageSize: 必传\n" +
            "- 返回值\n" +
            "   - 第一页时,返回查询结果总数\n" +
            "   - merchant_no: 商户编号\n" +
            "   - merchant_name: 商户名称\n" +
            "   - mobilephone: 手机号\n" +
            "   - status:          状态 1待一审  2待平台审核  3审核失败  4正常 5已转自动审件 0关闭\n" +
            "   - status_zh: 状态对应中文 1待一审  2待平台审核  3审核失败  4正常 5已转自动审件 0关闭\n" +
            "   - bp_name: 业务产品名称\n" +
            "   - create_time: 创建时间\n" +
            "   - examination_opinions : 审核意见\n" +
            "   - bp_id : 业务产品\n" +
            "   - sn: \n" +
            "   - {\n" +
            "       \"code\": 200,\n" +
            "       \"message\": \"\",\n" +
            "       \"data\": {\n" +
            "         \"list\": [\n" +
            "           {\n" +
            "             \"status_zh\": \"正常\",\n" +
            "             \"merchant_no\": \"258121000032067\",\n" +
            "             \"bp_name\": \"POS刷卡6（0.67%+5）\",\n" +
            "             \"create_time\": \"2019-06-25 10:00:58\",\n" +
            "             \"bp_id\": 256,\n" +
            "             \"mobilephone\": \"18800001114\",\n" +
            "             \"merchant_name\": \"金测试四\",\n" +
            "             \"id\": 205000496,\n" +
            "             \"sn\": \"D103082038003260\",\n" +
            "             \"status\": \"4\"\n" +
            "           },\n" +
            "           {\n" +
            "             \"status_zh\": \"正常\",\n" +
            "             \"merchant_no\": \"258131000032066\",\n" +
            "             \"bp_name\": \"无卡支付\",\n" +
            "             \"create_time\": \"2019-06-13 11:53:35\",\n" +
            "             \"bp_id\": 174,\n" +
            "             \"mobilephone\": \"18800001113\",\n" +
            "             \"merchant_name\": \"金三次了\",\n" +
            "             \"id\": 205000498,\n" +
            "             \"sn\": \"1000000000022641\",\n" +
            "             \"status\": \"4\"\n" +
            "           }\n" +
            "         ]\n" +
            "       },\n" +
            "       \"count\": -1,\n" +
            "       \"success\": true\n" +
            "   - }";

    public static final String GET_BANK_AND_CNAP = "获取支行信息\n" +
            "- 参数\n" +
            "   - city_name: 城市名，如深圳市传深圳\n" +
            "   - account_no: 银行卡账号\n" +
            "- 返回值\n" +
            "   - cnaps_no: 银联号\n" +
            "   - bank_name: 开户行地区\n";

    public static final String SELECT_POSTER = "海报下发\n" +
            "- 参数\n" +
            "- 返回值\n" +
            "   - fileName: 文件名\n" +
            "   - url: 阿里云地址\n" +
            "          {\n" +
            "            \"code\": 200,\n" +
            "            \"message\": \"\",\n" +
            "            \"data\": [\n" +
            "              {\n" +
            "                \"fileName\": \"http://agent-attch.oss.aliyuncs.com/abcd_1536904194594_18170.jpg?OSSAccessKeyId=Ck76WULSZApw3ZFv&Signature=orJGxYaUEjytX%2FcEaEaXKDWPupc%3D&Expires=5617437735744000\",\n" +
            "                \"url\": \"http://agent-attch.oss.aliyuncs.com/abcd_1536904194594_18170.jpg?OSSAccessKeyId=Ck76WULSZApw3ZFv&Signature=orJGxYaUEjytX%2FcEaEaXKDWPupc%3D&Expires=5617437735744000\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"fileName\": \"http://agent-attch.oss.aliyuncs.com/abcd_1536904194770_72572.jpg?OSSAccessKeyId=Ck76WULSZApw3ZFv&Signature=LAQoungRUMw66rfKwTk%2FKiXRkP8%3D&Expires=5617437736046400\",\n" +
            "                \"url\": \"http://agent-attch.oss.aliyuncs.com/abcd_1536904194770_72572.jpg?OSSAccessKeyId=Ck76WULSZApw3ZFv&Signature=LAQoungRUMw66rfKwTk%2FKiXRkP8%3D&Expires=5617437736046400\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"count\": 0,\n" +
            "            \"success\": true\n" +
            "          }";

    public static final String INSERT_AGENT_EXPAND = "拓展代理商\n" +
            "- 参数\n" +
            "   - parentId: 上级代理商编号,必传\n" +
            "   - userId: 上级代理商userId,必传\n" +
            "   - agentName: 代理商名称,必传\n" +
            "   - linkName: 联系人,必传\n" +
            "   - province: 省,必传\n" +
            "   - city: 市,必传\n" +
            "   - area: 区,必传\n" +
            "   - address: 详细地址,必传\n" +
            "   - mobilephone: 手机号,必传\n" +
            "   - smsCode: 短信验证码,必传\n" +
            "   - safePassword: 登录密码,必传\n" +
            "   - sign: md5(parentId+key)必传\n" +
            "   - saleName: 业务代表,即上级代理商的销售人员\n" +
            "- 返回值\n" +
            "   - 状态信息\n";

    public static final String GET_HARD_PRODUCT = "机具种类查询\n" +
            "- 参数\n" +
            "   - agent_no: 当前代理商编号,必传\n" +
            "- 返回值\n" +
            "   - hp_id: 硬件产品ID版本号\n" +
            "   - type_name: 种类名称\n" +
            "- 例子:\n" +
            "- {\n" +
            "      \"code\": 200,\n" +
            "      \"message\": \"\",\n" +
            "      \"data\": [\n" +
            "          {\n" +
            "            \"type_name\": \"全部\",\n" +
            "            \"hp_id\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"oem_mark\": \"0\",\n" +
            "            \"pos_type\": \"101\",\n" +
            "            \"type_name\": \"超级刷III\",\n" +
            "            \"secret_type\": 0,\n" +
            "            \"facturer_code\": \"NEWLAND\",\n" +
            "            \"version_nu\": \"III\",\n" +
            "            \"hp_id\": 1,\n" +
            "            \"manufacturer\": \"新大陆\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"oem_mark\": \"0\",\n" +
            "            \"pos_type\": \"101\",\n" +
            "            \"type_name\": \"超级刷II\",\n" +
            "            \"secret_type\": 0,\n" +
            "            \"facturer_code\": \"NEWLAND\",\n" +
            "            \"version_nu\": \"II\",\n" +
            "            \"model\": \"4\",\n" +
            "            \"hp_id\": 2,\n" +
            "            \"manufacturer\": \"新大陆\"\n" +
            "          }\n" +
            "      ],\n" +
            "      \"count\": 0,\n" +
            "      \"success\": true\n" +
            "}";

    public static final String GET_BP_ID = "查询业务产品列表\n" +
            "- 参数\n" +
            "   - agent_no: 当前代理商编号,必传\n" +
            "- 返回值\n" +
            "   - bp_id : 业务产品ID;\n" +
            "   - bp_name: 业务产品姓名;\n" +
            "   - bp_type: 业务产品类型;\n" +
            "   - remark: 业务产品简介;\n" +
            "   - allowIndividualApply:  允许单独申请，1：是，0：否'\n" +
            "   - groupNo: 产品组号\n" +
            "- 例子:\n" +
            "- {\n" +
            "      \"code\": 200,\n" +
            "      \"message\": \"\",\n" +
            "      \"data\": [\n" +
            "           {\n" +
            "             \"bp_id\": \"174\",\n" +
            "             \"bp_name\": \"无卡支付\",\n" +
            "             \"bp_type\": \"1\",\n" +
            "             \"remark\": null,\n" +
            "             \"allowIndividualApply\": null,\n" +
            "             \"groupNo\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"bp_id\": \"250\",\n" +
            "             \"bp_name\": \"POS刷卡（1%+2）\",\n" +
            "             \"bp_type\": \"1\",\n" +
            "             \"remark\": \"不买不买\",\n" +
            "             \"allowIndividualApply\": null,\n" +
            "             \"groupNo\": null\n" +
            "           }\n" +
            "      ],\n" +
            "      \"count\": 0,\n" +
            "      \"success\": true\n" +
            "}";

    public static final String GET_AGENT_BUSINESS = "获取代理商业务产品\n" +
            "- 参数\n" +
            "   - agent_no: 当前代理商编号,必传\n" +
            "- 返回值\n" +
            "   - bp_id : 业务产品ID;\n" +
            "   - bp_name: 业务产品名称;\n" +
            "- 例子:\n" +
            "- {\n" +
            "      \"code\": 200,\n" +
            "      \"message\": \"\",\n" +
            "      \"data\": [\n" +
            "          {\n" +
            "            \"bp_name\": \"NFC收款业务产品\",\n" +
            "            \"bp_id\": 296\n" +
            "          },\n" +
            "          {\n" +
            "            \"bp_name\": \"POS刷卡（1%+2）\",\n" +
            "            \"bp_id\": 250\n" +
            "          },\n" +
            "          {\n" +
            "            \"bp_name\": \"无卡支付\",\n" +
            "            \"bp_id\": 174\n" +
            "          }\n" +
            "      ],\n" +
            "      \"count\": 0,\n" +
            "      \"success\": true\n" +
            "}";

    public static final String GET_ALL_ACTIVITY_INFO = "获取所有活动信息\n" +
            "- 参数\n" +
            "   - 参数可以全部传空值\n" +
            "- 返回值,活动信息\n" +
            "   - activity_name : 活动名称\n" +
            "   - activity_value: 活动名称对应的值\n" +
            "   - activity_status: 活动状态\n" +
            "   - activity_remark: 备注\n" +
            "- 例子:\n" +
            "- {\n" +
            "      \"code\": 200,\n" +
            "      \"message\": \"\",\n" +
            "      \"data\": [\n" +
            "          {\n" +
            "            \"activity_name\": \"循环送\",\n" +
            "            \"activity_value\": \"1\",\n" +
            "            \"activity_remark\": \"机具活动类型\",\n" +
            "            \"activity_status\": 1\n" +
            "          },\n" +
            "          {\n" +
            "            \"activity_name\": \"移公社\",\n" +
            "            \"activity_value\": \"2\",\n" +
            "            \"activity_remark\": \"机具活动类型\",\n" +
            "            \"activity_status\": 1\n" +
            "          },\n" +
            "          {\n" +
            "            \"activity_name\": \"返现\",\n" +
            "            \"activity_value\": \"3\",\n" +
            "            \"activity_remark\": \"机具活动类型\",\n" +
            "            \"activity_status\": 1\n" +
            "          }\n" +
            "      ],\n" +
            "      \"count\": 0,\n" +
            "      \"success\": true\n" +
            "}";

    public static final String GET_AGENT_TEAMS = "获取代理商所属产品\n" +
            "- 参数\n" +
            "   - agent_no: 当前代理商编号,必传\n" +
            "- 返回值\n" +
            "   - team_id : 组织id;\n" +
            "   - team_name: 组织名称;\n" +
            "   - team_entry: 子组织信息\n" +
            "       - team_entry_id: 子组织id\n" +
            "       - team_entry_name: 子组织名称\n" +
            "- 例子:\n" +
            "{\n" +
            "  \"code\": 200,\n" +
            "  \"message\": \"\",\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"team_id\": \"\",\n" +
            "      \"team_name\": \"全部\",\n" +
            "      \"team_entry\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"team_id\": 100010,\n" +
            "      \"team_name\": \"直营组织\",\n" +
            "      \"team_entry\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"team_id\": 100070,\n" +
            "      \"team_name\": \"盛POS\",\n" +
            "      \"team_entry\": [\n" +
            "        {\n" +
            "          \"team_entry_id\": \"\",\n" +
            "          \"team_entry_name\": \"全部\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"team_entry_id\": \"100070-001\",\n" +
            "          \"team_entry_name\": \"盛POS\",\n" +
            "          \"team_id\": 100070\n" +
            "        },\n" +
            "        {\n" +
            "          \"team_entry_id\": \"100070-002\",\n" +
            "          \"team_entry_name\": \"超级盛POS\",\n" +
            "          \"team_id\": 100070\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"team_id\": 200010,\n" +
            "      \"team_name\": \"盛钱包\",\n" +
            "      \"team_entry\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"team_id\": 600010,\n" +
            "      \"team_name\": \"超级还\",\n" +
            "      \"team_entry\": []\n" +
            "    }\n" +
            "  ],\n" +
            "  \"count\": 0,\n" +
            "  \"success\": true\n" +
            "}";

    public static final String INSERT_AGENT = "新增下级代理商\n" +
            "- 参数\n" +
            "   - agentInfo: 代理商信息\n" +
            "       - agentNo: 代理商ID\n" +
            "       - agentName: 代理商名称\n" +
            "       - linkName: 联系人\n" +
            "       - email: 邮箱\n" +
            "       - mobilephone: 手机号\n" +
            "       - province: 省\n" +
            "       - city: 市\n" +
            "       - area: 区\n" +
            "       - address: 详细地址\n" +
            "       - accountType: 结算信息  1：对公，2：对私\n" +
            "       - accountName: 开户名\n" +
            "       - accountNo: 开户账户\n" +
            "       - bankName: 开户行全称\n" +
            "       - cnapsNo: 联行行号\n" +
            "       - accountProvince: 开户行地区：省\n" +
            "       - accountCity: 开户行地区：市\n" +
            "       - subBank: 支行\n" +
            "       - saleName: 销售名称\n" +
            "       - agentArea: 代理区域\n" +
            "       - teamId: 组织id\n" +
            "       - oneAgentNo: 一级代理商编号\n" +
            "   - bpIdList: 勾选的业务产品id\n" +
            "       - bpId: 勾选的业务产品id\n" +
            "   - productsAgentList: 业务产品,可以不传,后台没用 \n" +
            "       - bp_id: 业务产品ID \n" +
            "       - cost_income:  \n" +
            "       - share_income:  \n" +
            "       - profit_type:  \n" +
            "   - shareDataList:  \n" +
            "       - cardType: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡\n" +
            "       - holidaysMark: 节假日标志:0-不限，1-只工作日，2-只节假日\n" +
            "       - profitType: 分润类型, 固定传 5\n" +
            "       - serviceId: 服务id\n" +
            "       - shareProfitPercent:代理商固定分润百分比 固定传100\n" +
            "       - costRateType:	代理商成本费率类型:1-每笔固定金额(serviceType = 10000 or serviceType=10001)，2-扣率，\n" +
            "       - cost: 固定金额或固定比率\n" +
            "       - serviceTypeName: 服务类型名称 bpName + '-' + serviceTypeName\n" +
            "   - happyBackDataList: 欢乐返活动列表 \n" +
            "       - activityTypeNo: 欢乐返子类型编号\n" +
            "       - activityTypeName: 欢乐返子类型名称\n" +
            "       - activityCode: 欢乐返类型\n" +
            "       - transAmount: 交易金额\n" +
            "       - cashBackAmount: 下发返现金额 改为  首次注册返现金额\n" +
            "       - taxRate:	固定返现百分比  改为  首次注册返现比例\n" +
            "       - repeatRegisterAmount: 重复注册返现金额\n" +
            "       - repeatRegisterRatio: 重复注册返现比例\n" +
            "       - fullPrizeAmount: 首次注册满奖金额\n" +
            "       - notFullDeductAmount: 首次注册不满扣金额\n" +
            "       - repeatFullPrizeAmount: 重复注册满奖金额\n" +
            "       - repeatNotFullDeductAmount: 重复注册不满扣金额\n" +
            "- 返回值\n" +
            "   - 状态信息\n";

    public static final String GET_AGENT_SERVICES = "获取分润服务信息\n" +
            "- 参数\n" +
            "   - agentNo: 代理商编号,必传\n" +
            "   - bpIds: 勾选的业务产品id集合,必传\n" +
            "       - bpId: 业务产品id\n" +
            "- 返回值\n" +
            "   - serviceId : 服务id\n" +
            "   - serviceName: 服务名称\n" +
            "   - holidaysMark: 节假日标志:1-只工作日，2-只节假日，0-不限\n" +
            "   - cardType: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡\n" +
            "   - bpId: 业务产品id\n" +
            "   - bpName: 业务产品名称\n" +
            "   - serviceType: 服务类型\n" +
            "   - serviceType2: 主服务类型, 如果该服务是提现服务,则关联出主服务类型\n" +
            "   - serviceTypeName: 服务种类名称\n" +
            "- 例子:\n" +
            "   - {\n" +
            "       \"code\": 200,\n" +
            "       \"message\": \"\",\n" +
            "       \"data\": [\n" +
            "         {\n" +
            "           \"id\": 78600,\n" +
            "           \"serviceId\": 424,\n" +
            "           \"serviceName\": \"快捷支付\",\n" +
            "           \"holidaysMark\": \"0\",\n" +
            "           \"cardType\": \"0\",\n" +
            "           \"quotaLevel\": null,\n" +
            "           \"agentNo\": \"0\",\n" +
            "           \"rateType\": \"2\",\n" +
            "           \"singleNumAmount\": 0.3,\n" +
            "           \"rate\": 0.65,\n" +
            "           \"capping\": null,\n" +
            "           \"safeLine\": null,\n" +
            "           \"isGlobal\": 1,\n" +
            "           \"checkStatus\": \"0\",\n" +
            "           \"lockStatus\": \"0\",\n" +
            "           \"ladder1Rate\": null,\n" +
            "           \"ladder1Max\": null,\n" +
            "           \"ladder2Rate\": null,\n" +
            "           \"ladder2Max\": null,\n" +
            "           \"ladder3Rate\": null,\n" +
            "           \"ladder3Max\": null,\n" +
            "           \"ladder4Rate\": null,\n" +
            "           \"ladder4Max\": null,\n" +
            "           \"merRate\": null,\n" +
            "           \"oneMerRate\": null,\n" +
            "           \"fixedRate\": 0,\n" +
            "           \"bpId\": \"174\",\n" +
            "           \"bpName\": \"无卡支付\",\n" +
            "           \"allowIndividualApply\": 1,\n" +
            "           \"serviceType\": 10004,\n" +
            "           \"serviceType2\": null\n" +
            "         },\n" +
            "         {\n" +
            "           \"id\": 78601,\n" +
            "           \"serviceId\": 423,\n" +
            "           \"serviceName\": \"快捷支付关联提现\",\n" +
            "           \"holidaysMark\": \"0\",\n" +
            "           \"cardType\": \"0\",\n" +
            "           \"quotaLevel\": null,\n" +
            "           \"agentNo\": \"0\",\n" +
            "           \"rateType\": \"1\",\n" +
            "           \"singleNumAmount\": 0.1,\n" +
            "           \"rate\": null,\n" +
            "           \"capping\": null,\n" +
            "           \"safeLine\": null,\n" +
            "           \"isGlobal\": 1,\n" +
            "           \"checkStatus\": \"0\",\n" +
            "           \"lockStatus\": \"0\",\n" +
            "           \"ladder1Rate\": null,\n" +
            "           \"ladder1Max\": null,\n" +
            "           \"ladder2Rate\": null,\n" +
            "           \"ladder2Max\": null,\n" +
            "           \"ladder3Rate\": null,\n" +
            "           \"ladder3Max\": null,\n" +
            "           \"ladder4Rate\": null,\n" +
            "           \"ladder4Max\": null,\n" +
            "           \"merRate\": null,\n" +
            "           \"oneMerRate\": null,\n" +
            "           \"fixedRate\": 0,\n" +
            "           \"bpId\": \"174\",\n" +
            "           \"bpName\": \"无卡支付\",\n" +
            "           \"allowIndividualApply\": 1,\n" +
            "           \"serviceType\": 10001,\n" +
            "           \"serviceType2\": 10004\n" +
            "         }\n" +
            "       ],\n" +
            "       \"count\": 0,\n" +
            "       \"success\": true\n" +
            "   - }";

    public static final String SELECT_HAPPY_BACK = "新增代理商,查询欢乐返活动\n" +
            "- 参数\n" +
            "   - agentNo: 代理商编号,必传\n" +
            "- 返回值\n" +
            "   - activityTypeNo: 欢乐返子类型编号\n" +
            "   - activityTypeName: 欢乐返子类型名称\n" +
            "   - activityCode: 欢乐返类型\n" +
            "   - transAmount: 交易金额\n" +
            "   - cashBackAmount: 下发返现金额\n" +
            "   - taxRate:	税额百分比\n" +
            "   - repeatRegisterAmount: 重复返现金额\n" +
            "   - repeatRegisterRatio: 重复注册返现比例\n" +
            "   - fullPrizeLevelFlag 新增下级代理商时满奖开关,为true 支持配置,即显示,否则为false" +
            "   - notFullDeductLevelFlag 新增下级代理商时不满扣开关,为true 支持配置,即显示,否则为false" +
            "   - 例子:\n" +
            "       {\n" +
            "         \"code\": 200,\n" +
            "         \"message\": \"\",\n" +
            "         \"data\": [\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00002\",\n" +
            "             \"activityTypeName\": \"欢乐返-循环送98\",\n" +
            "             \"activityCode\": \"欢乐返-循环送\",\n" +
            "             \"transAmount\": \"88.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00003\",\n" +
            "             \"activityTypeName\": \"欢乐返-98\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"100.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00005\",\n" +
            "             \"activityTypeName\": \"欢乐返-99\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"120.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00009\",\n" +
            "             \"activityTypeName\": \"欢乐返1-120\",\n" +
            "             \"activityCode\": \"欢乐返-循环送\",\n" +
            "             \"transAmount\": \"88.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00029\",\n" +
            "             \"activityTypeName\": \"欢乐返*168\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"68.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00031\",\n" +
            "             \"activityTypeName\": \"欢乐返98(pcx)\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"98.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00042\",\n" +
            "             \"activityTypeName\": \"欢乐返-0\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"0.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00044\",\n" +
            "             \"activityTypeName\": \"大POS欢乐返\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"100.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00051\",\n" +
            "             \"activityTypeName\": \"欢乐返101\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"101.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           },\n" +
            "           {\n" +
            "             \"activityTypeNo\": \"00062\",\n" +
            "             \"activityTypeName\": \"欢乐返0冻结(测试)\",\n" +
            "             \"activityCode\": \"欢乐返\",\n" +
            "             \"transAmount\": \"0.00\",\n" +
            "             \"cashBackAmount\": null,\n" +
            "             \"taxRate\": null,\n" +
            "             \"repeatRegisterAmount\": null,\n" +
            "             \"repeatRegisterRatio\": null\n" +
            "           }\n" +
            "         ],\n" +
            "         \"count\": 0,\n" +
            "         \"success\": true\n" +
            "       }";

    public static final String GET_ACQ_MERINFO_DETAILS = "收单商户进件详情\n" +
            "- 参数\n" +
            "   - acq_into_no: 进件编号\n" +
            "   - agent_no: 当前登录代理商编号\n" +
            "- 返回值\n" +
            "       - specialIndustry: 特定行业名称，为空字符串则对应的图片列表不显示，否则显示对应的名称，取file_type=46的图片\n" +
            "   - acqMerInfo: 收单商户\n" +
            "       - id: \n" +
            "       - merchant_type: 进件类型:1个体收单商户，2-企业收单商户\n" +
            "       - merchant_name: 商户名称\n" +
            "       - legal_person: 法人姓名\n" +
            "       - legal_person_id: 法人身份证号\n" +
            "       - id_valid_start: 身份证有效期开始时间\n" +
            "       - id_valid_end: 身份证有效期结束时间\n" +
            "       - province: 经营地址(省)\n" +
            "       - city: 经营地址（市）\n" +
            "       - district: 经营地址（区）\n" +
            "       - address: 详细地址\n" +
            "       - one_scope: 一级经营范围\n" +
            "       - two_scope: 二级经营范围\n" +
            "       - charter_name: 营业执照名称\n" +
            "       - charter_no: 营业执照编号\n" +
            "       - charter_valid_start: 营业执照有效开始时间\n" +
            "       - charter_valid_end: 营业执照有效期结束时间\n" +
            "       - account_type: 账户类型 1 对私 2对公\n" +
            "       - bank_no: 银行卡号\n" +
            "       - account_name: 开户名\n" +
            "       - account_bank: 开户银行\n" +
            "       - account_province: 开户地区（省）\n" +
            "       - account_city: 开户地区（市）\n" +
            "       - account_district: 开户地区（区）\n" +
            "       - bank_branch: 支行\n" +
            "       - line_number: 联行号\n" +
            "       - acq_into_no: 进件编号\n" +
            "       - into_source: 进件来源\n" +
            "       - audit_status: 审核状态 1.正常 2.审核通过 3 审核不通过\n" +
            "       - audit_time: 审核时间\n" +
            "       - create_time: 进件时间\n" +
            "       - agent_no: 所属代理商\n" +
            "       - one_agent_no: 所属一级代理商\n" +
            "       - examination_opinions: 审核意见\n" +
            "       - mcc: mcc码\n" +
            "       - one_scope_name: 一级经营范围名称\n" +
            "       - two_scope_name: 二级经营范围名称\n" +
            "       - update_time: 修改时间\n" +
            "   - acqMerFileInfoList: 银联号\n" +
            "       - id: id\n" +
            "       - create_time: 创建时间\n" +
            "       - file_type: 文件类型\n" +
            "       - file_url: 文件地址\n" +
            "       - status: 文件状态 1 正常 2失效\n" +
            "       - audit_status 审核状态 1.待审核 2.审核通过 3 审核不通过\n" +
            "       - acq_into_no: 进件编号\n";

    public static final String GET_ACQ_MERINFO_LIST = "收单商户进件列表查询\n" +
            "- 参数\n" +
            "   - agent_no: 所属代理商\n" +
            "   - audit_status: 审核状态\n" +
            "   - merchant_name: 商户名称\n" +
            "   - create_starttime: 进件起始日期\n" +
            "   - create_endtime: 进件终止日期\n" +
            "   - pageNo\n" +
            "   - pageSize\n" +
            "- 返回值\n" +
            "   - acqMerInfoList: \n" +
            "       - id: \n" +
            "       - merchant_type: 进件类型:1个体收单商户，2-企业收单商户\n" +
            "       - merchant_name: 商户名称\n" +
            "       - legal_person: 法人姓名\n" +
            "       - legal_person_id: 法人身份证号\n" +
            "       - id_valid_start: 身份证有效期开始时间\n" +
            "       - id_valid_end: 身份证有效期结束时间\n" +
            "       - province: 经营地址(省)\n" +
            "       - city: 经营地址（市）\n" +
            "       - district: 经营地址（区）\n" +
            "       - address: 详细地址\n" +
            "       - one_scope: 一级经营范围\n" +
            "       - two_scope: 二级经营范围\n" +
            "       - charter_name: 营业执照名称\n" +
            "       - charter_no: 营业执照编号\n" +
            "       - charter_valid_start: 营业执照有效开始时间\n" +
            "       - charter_valid_end: 营业执照有效期结束时间\n" +
            "       - account_type: 账户类型 1 对私 2对公\n" +
            "       - bank_no: 银行卡号\n" +
            "       - account_name: 开户名\n" +
            "       - account_bank: 开户银行\n" +
            "       - account_province: 开户地区（省）\n" +
            "       - account_city: 开户地区（市）\n" +
            "       - account_district: 开户地区（区）\n" +
            "       - bank_branch: 支行\n" +
            "       - line_number: 联行号\n" +
            "       - acq_into_no: 进件编号\n" +
            "       - into_source: 进件来源\n" +
            "       - audit_status: 审核状态 1.正常 2.审核通过 3 审核不通过\n" +
            "       - audit_time: 审核时间\n" +
            "       - create_time: 进件时间\n" +
            "       - agent_no: 所属代理商\n" +
            "       - one_agent_no: 所属一级代理商\n" +
            "       - examination_opinions: 审核意见\n" +
            "       - mcc: mcc码\n" +
            "       - one_scope_name: 一级经营范围名称\n" +
            "       - two_scope_name: 二级经营范围名称\n" +
            "       - update_time: 修改时间\n" +
            "   - total: 总记录数\n";

    public static final String QUERY_MER_ITEM_DETAILS = "商户进件详情\n" +
            "- 参数\n" +
            "   - agent_node: 当前登录的代理商节点,必传\n" +
            "   - merchant_no: 商户编号,必传\n" +
            "   - bp_id: 业务产品,必传\n" +
            "   - one_agent_no: 一级代理商编号,必传\n" +
            "- 返回值\n" +
            "   - merServiceRateList: 服务对应的费率信息\n" +
            "       - capping: 封顶\n" +
            "       - card_type: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡(传)\n" +
            "       - disabled_date: 失效时间\n" +
            "       - efficient_date: 生效时间\n" +
            "       - holidays_mark: 节假日标志:1-只工作日，2-只节假日，0-不限(传)\n" +
            "       - id: id\n" +
            "       - merchant_no: 商户ID\n" +
            "       - rate: 扣率\n" +
            "       - rate_type: 费率类型:1-每笔固定金额，2-扣率，3-扣率带保底封顶，4-扣率+固定金额,5-单笔阶梯 扣率(传)\n" +
            "       - safe_line: 保底\n" +
            "       - service_id: 服务ID(传)\n" +
            "       - single_num_amount: 每笔固定值\n" +
            "       - ladder1_rate: 阶梯区间1费率\n" +
            "       - ladder1_max: 阶梯区间1上限\n" +
            "       - ladder2_rate: 阶梯区间2费率\n" +
            "       - ladder2_max: 阶梯区间2上限\n" +
            "       - ladder3_rate: 阶梯区间3费率\n" +
            "       - ladder3_max: 阶梯区间3上限\n" +
            "       - ladder4_rate: 阶梯区间4费率\n" +
            "       - ladder4_max: 阶梯区间4上限\n" +
            "       - service_name: 服务名称\n" +
            "   - merServiceQuotaList: 服务对应的限额信息\n" +
            "       - card_type: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡(传)\n" +
            "       - disabled_date: 失效时间\n" +
            "       - efficient_date: 生效时间\n" +
            "       - holidays_mark: 节假日标志:1-只工作日，2-只节假日，0-不限(传)\n" +
            "       - id: id\n" +
            "       - merchant_no: 商户ID\n" +
            "       - service_id: 服务ID(传)\n" +
            "       - single_count_amount: 单笔最大交易额(传)\n" +
            "       - single_daycard_amount: 单日单卡最大交易额(传)\n" +
            "       - single_daycard_count: 单日单卡最大交易笔数(传)\n" +
            "       - single_day_amount: 单日最大交易额(传)\n" +
            "       - useable: \n" +
            "       - fixed_mark: \n" +
            "       - service_name: 服务名称\n" +
            "       - single_min_amount: 单笔最小交易额(传)\n" +
            "   - merInfo: 商户信息\n" +
            "       - address: 经营地址:详细地址\n" +
            "       - agentNo: 代理商ID\n" +
            "       - businessType: 经营范围-商户类别：餐娱类；批发类；民生类；一般类；房车类；其他；(传,假设传参为1代表民生类)\n" +
            "       - city: 经营地址（市）\n" +
            "       - createTime: 创建时间\n" +
            "       - creator: 创建人\n" +
            "       - email: Email\n" +
            "       - examinationOpinions: 审核意见\n" +
            "       - id: id\n" +
            "       - idCardNo: 法人身份证号(传)\n" +
            "       - industryType: 行业类型(传MCC)\n" +
            "       - lastUpdate_time: 最后更新时间\n" +
            "       - lawyer: 法人姓名(传)\n" +
            "       - mender: 修改人\n" +
            "       - merchantNo: 商户ID\n" +
            "       - merchantName: 商户名称(传)\n" +
            "       - merchantType: 商户类型:1-个人，2-个体商户，3-企业商户\n" +
            "       - mobilephone: 手机号(传)\n" +
            "       - operator: 业务人员\n" +
            "       - parentNode: 上级代理商节点\n" +
            "       - posType: 设备类型 1移联商宝,2传统POS,3移小宝,4移联商通,5超级刷\n" +
            "       - province: 经营地址（省）\n" +
            "       - remark: 备注(传)\n" +
            "       - saleName: 销售人员（谁拓展的商户）\n" +
            "       - status: 状态\n" +
            "   - photoRequireItemList: 结算信息资料\n" +
            "       - id: \n" +
            "       - merchant_no: 商户ID\n" +
            "       - mri_id: 进件要求项ID\n" +
            "       - content: 附件名称包含后缀\n" +
            "       - status: 状态：0待审核；1通过；2审核失败\n" +
            "       - check_status: \n" +
            "   - prayerRequireItemList: 结算信息资料\n" +
            "       - id: \n" +
            "       - merchant_no: 商户ID\n" +
            "       - mri_id: 进件要求项ID\n" +
            "       - content: 附件名称包含后缀\n" +
            "       - status: 状态：0待审核；1通过；2审核失败\n" +
            "       - check_status: \n" +
            "   - status: 该商户最终审核状态\n" +
            "  - 例子: \n" +
            "       - {\n" +
            "           \"code\": 200,\n" +
            "           \"message\": \"\",\n" +
            "           \"data\": {\n" +
            "             \"prayerRequireItemList\": [\n" +
            "               {\n" +
            "                 \"id\": \"62811\",\n" +
            "                 \"merchant_no\": \"258121000032121\",\n" +
            "                 \"mri_id\": \"1\",\n" +
            "                 \"content\": \"对私\",\n" +
            "                 \"status\": \"1\",\n" +
            "                 \"check_status\": null,\n" +
            "                 \"merchantNo\": null,\n" +
            "                 \"mriId\": null,\n" +
            "                 \"last_update_time\": \"2019-07-02 18:56:35\"\n" +
            "               },\n" +
            "               {\n" +
            "                 \"id\": \"62817\",\n" +
            "                 \"merchant_no\": \"258121000032121\",\n" +
            "                 \"mri_id\": \"7\",\n" +
            "                 \"content\": \"北京-北京市-朝阳区-同方信息\",\n" +
            "                 \"status\": \"1\",\n" +
            "                 \"check_status\": null,\n" +
            "                 \"merchantNo\": null,\n" +
            "                 \"mriId\": null,\n" +
            "                 \"last_update_time\": \"2019-07-02 18:56:35\"\n" +
            "               }\n" +
            "             ],\n" +
            "             \"msg\": \"查询商户进件详情成功\",\n" +
            "             \"photoRequireItemList\": [\n" +
            "               {\n" +
            "                 \"id\": \"62807\",\n" +
            "                 \"merchant_no\": \"258121000032121\",\n" +
            "                 \"mri_id\": \"10\",\n" +
            "                 \"content\": \"http://agent-attch.oss.aliyuncs.com/1472814340523.png?OSSAccessKeyId=Ck76WULSZApw3ZFv&Signature=8KW4zMQQYv2dbraLil2iZCAYpl8%3D&Expires=5625585549460800\",\n" +
            "                 \"status\": \"1\",\n" +
            "                 \"check_status\": null,\n" +
            "                 \"merchantNo\": null,\n" +
            "                 \"mriId\": null,\n" +
            "                 \"last_update_time\": \"2019-07-02 18:56:35\"\n" +
            "               }\n" +
            "             ],\n" +
            "             \"merInfo\": {\n" +
            "               \"id\": 683512,\n" +
            "               \"merchantNo\": \"258121000032121\",\n" +
            "               \"merchantName\": \"烧肉二期\",\n" +
            "               \"merchantType\": \"个人\",\n" +
            "               \"lawyer\": \"吴建辉\",\n" +
            "               \"businessType\": \"餐娱类\",\n" +
            "               \"industryType\": \"餐馆\",\n" +
            "               \"idCardNo\": \"44138119840510155X\",\n" +
            "               \"province\": \"北京\",\n" +
            "               \"city\": \"北京市\",\n" +
            "               \"district\": \"朝阳区\",\n" +
            "               \"address\": \"北京北京市朝阳区同方信息\",\n" +
            "               \"mobilephone\": \"17536936915\",\n" +
            "               \"email\": null,\n" +
            "               \"operator\": null,\n" +
            "               \"agentNo\": \"1446\",\n" +
            "               \"agentName\": null,\n" +
            "               \"createTime\": \"2019-07-03 18:56:35\",\n" +
            "               \"status\": \"1\",\n" +
            "               \"statusZh\": null,\n" +
            "               \"parentNode\": \"0-1446-\",\n" +
            "               \"saleName\": \"1446\",\n" +
            "               \"creator\": null,\n" +
            "               \"mender\": null,\n" +
            "               \"lastUpdateTime\": \"2019-07-04 09:40:58\",\n" +
            "               \"remark\": null,\n" +
            "               \"oneAgentNo\": \"1446\",\n" +
            "               \"teamId\": \"100070\",\n" +
            "               \"merAccount\": 1,\n" +
            "               \"registerSource\": \"1\",\n" +
            "               \"riskStatus\": \"1\",\n" +
            "               \"examinationOpinions\": null,\n" +
            "               \"posType\": null\n" +
            "             },\n" +
            "             \"merServiceRateList\": [\n" +
            "               {\n" +
            "                 \"capping\": null,\n" +
            "                 \"card_type\": \"只信用卡\",\n" +
            "                 \"disabled_date\": null,\n" +
            "                 \"efficient_date\": null,\n" +
            "                 \"holidays_mark\": \"不限\",\n" +
            "                 \"id\": null,\n" +
            "                 \"merchant_no\": null,\n" +
            "                 \"rate\": \"2.00\",\n" +
            "                 \"rate_type\": \"1\",\n" +
            "                 \"safe_line\": null,\n" +
            "                 \"service_id\": \"1162\",\n" +
            "                 \"single_num_amount\": \"2.00\",\n" +
            "                 \"ladder1_rate\": null,\n" +
            "                 \"ladder1_max\": null,\n" +
            "                 \"ladder2_rate\": null,\n" +
            "                 \"ladder2_max\": null,\n" +
            "                 \"ladder3_rate\": null,\n" +
            "                 \"ladder3_max\": null,\n" +
            "                 \"ladder4_rate\": null,\n" +
            "                 \"ladder4_max\": null,\n" +
            "                 \"service_name\": \"盛POS快捷提现\",\n" +
            "                 \"check_status\": null\n" +
            "               }\n" +
            "             ],\n" +
            "             \"merServiceQuotaList\": [\n" +
            "               {\n" +
            "                 \"card_type\": \"不限\",\n" +
            "                 \"disabled_date\": null,\n" +
            "                 \"efficient_date\": null,\n" +
            "                 \"holidays_mark\": \"不限\",\n" +
            "                 \"id\": null,\n" +
            "                 \"merchant_no\": null,\n" +
            "                 \"service_id\": \"1162\",\n" +
            "                 \"single_count_amount\": \"10000.00\",\n" +
            "                 \"single_daycard_amount\": \"1000000.00\",\n" +
            "                 \"single_daycard_count\": \"99\",\n" +
            "                 \"single_day_amount\": \"10000.00\",\n" +
            "                 \"useable\": null,\n" +
            "                 \"fixed_mark\": null,\n" +
            "                 \"service_name\": \"盛POS快捷提现\",\n" +
            "                 \"single_min_amount\": \"0.01\",\n" +
            "                 \"check_status\": null,\n" +
            "                 \"singleDayAmount\": null,\n" +
            "                 \"singleCountAmount\": null,\n" +
            "                 \"singleDaycardAmount\": null,\n" +
            "                 \"singleDaycardCount\": null,\n" +
            "                 \"singleMinAmount\": null,\n" +
            "                 \"cardType\": null,\n" +
            "                 \"serviceId\": null,\n" +
            "                 \"holidaysMark\": null\n" +
            "               }\n" +
            "             ],\n" +
            "             \"status\": true\n" +
            "           },\n" +
            "           \"count\": 0,\n" +
            "           \"success\": true\n" +
            "       - }";

    public static final String GET_SERVICE_INFO_BY_PARAMS = "根据业务产品查出服务,根据服务查出业务服率和限额\n" +
            "- 参数\n" +
            "   - agent_no: 代理商编号,必传\n" +
            "   - bp_id: 业务产品id,必传\n" +
            "   - one_agent_no: 一级代理商编号,必传\n" +
            "   - mobilephone: 手机号,必传\n" +
            "- 返回值\n" +
            "   - serviceInfo: 服务\n" +
            "       - serviceId: 服务ID\n" +
            "       - serviceName: 服务名称\n" +
            "       - serviceType: 服务类型:1-POS刷卡，2-扫码支付，3-快捷支付，4-账户提现\n" +
            "       - hardwareIs: 是否与硬件相关:1-是，0-否\n" +
            "       - bankCard: 可用银行卡集合:1-信用卡，2-银行卡，0-不限\n" +
            "       - exclusive: 可否单独申请:1-可，0-否\n" +
            "       - business: 业务归属\n" +
            "       - saleStarttime: 可销售起始日期\n" +
            "       - saleEndtime: 可销售终止日期\n" +
            "       - useStarttime: 可使用起始日期\n" +
            "       - useEndtime: 可使用终止日期\n" +
            "       - proxy: 可否代理:1-可，0-否\n" +
            "       - getcashId: 提现服务ID\n" +
            "       - rateCard: 费率是否区分银行卡种类:1-是，0-否\n" +
            "       - rateHolidays: 费率是否区分节假日:1-是，0-否\n" +
            "       - quotaHolidays: 限额是否区分节假日:1-是，0-否\n" +
            "       - quotaCard: 限额是否区分银行卡种类:1-是，0-否\n" +
            "       - oemId: OEM ID\n" +
            "       - remark: 备注\n" +
            "       - tFlag: T0T1标志：0-不涉及，1-T0，2-T1\n" +
            "       - cashSubject: 仅服务类型为账户提现，存储科目，账号\n" +
            "       - fixedRate: 费率固定标志:1-固定，0-不固定\n" +
            "       - fixedQuota: 额度固定标志:1-固定，0-不固定\n" +
            "       - serviceStatus: 服务状态：1开启，0关闭\n" +
            "       - tradStart: 交易开始时间\n" +
            "       - tradEnd: 交易截至时间\n" +
            "   - serviceQuota: 服务限额信息\n" +
            "       - agentNo: 代理商ID\n" +
            "       - cardType: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡\n" +
            "       - checkStatus: 审核状态\n" +
            "       - holidaysMark: 节假日标志:1-只工作日，2-只节假日，0-不限\n" +
            "       - id: id\n" +
            "       - lockStatus: 锁定状态：1锁定；0未锁定\n" +
            "       - quotaLevel: 限额等级:0-全局、1-某个代理商下的商户\n" +
            "       - serviceId: 服务ID\n" +
            "       - serviceName: 服务名称\n" +
            "       - serviceManageQuotacol:\n" +
            "       - serviceType: 服务类型:1-刷卡，2-提现，3-网络支付，4-快捷支付\n" +
            "       - singleCountAmount: 单笔最大交易额\n" +
            "       - singleDaycardAmount: 单日单卡最大交易额\n" +
            "       - singleDaycardCount: 单日单卡最大交易笔数\n" +
            "       - singleDayAmount: 单日最大交易额\n" +
            "       - fixedQuota: 服务限额是否固定\n" +
            "       - singleMinAmount: 单笔最小交易额\n" +
            "   - serviceRate: 服务对应的费率信息\n" +
            "       - agentNo: 代理商ID\n" +
            "       - capping: 封顶\n" +
            "       - cardType: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡\n" +
            "       - checkStatus: 审核状态\n" +
            "       - fixedMark: 固定标志:1-固定，0-不固定商户费率高于管控费率即可\n" +
            "       - holidaysMark: 节假日标志:1-只工作日，2-只节假日，0-不限\n" +
            "       - id: id\n" +
            "       - lockStatus: 锁定状态\n" +
            "       - quotaLevel: 限额等级:0-全局、1-某个代理商下的商户\n" +
            "       - rate: 扣率\n" +
            "       - rateType: 费率类型:1每笔固定金额，2扣率，3扣率+保底封顶\n" +
            "       - safeLine: 保底\n" +
            "       - serviceId: 服务ID\n" +
            "       - serviceName: 服务名称\n" +
            "       - serviceType: 服务类型:1-刷卡，2-提现，3-网络支付，4-快捷支付\n" +
            "       - singleNumAmount: 每笔固定值\n" +
            "       - ladder1Rate: 阶梯区间1费率\n" +
            "       - ladder1Max: 阶梯区间1上限\n" +
            "       - ladder2Rate: 阶梯区间2费率\n" +
            "       - ladder2Max: 阶梯区间2上限\n" +
            "       - ladder3Rate: 阶梯区间3费率\n" +
            "       - ladder3Max: 阶梯区间3上限\n" +
            "       - ladder4Rate: 阶梯区间4费率\n" +
            "       - ladder4Max: 阶梯区间4上限\n" +
            "       - fixedRate: 服务费率是否固定\n" +
            "   - addRequireItem: 业务产品对应的进件项信息\n" +
            "       - dataAll: 资料是否记录进件项内容：1-是，2-否\n" +
            "       - example: 示例\n" +
            "       - exampleType: 示例类型:1-图片，2-文件，3-文字\n" +
            "       - photoAddress: 图片的地址\n" +
            "       - itemId: 进件要求项ID\n" +
            "       - itemName: 要求项名称\n" +
            "       - photo: 图片来源：1 只允许拍照，2 拍照和相册\n" +
            "       - remark: 备注\n" +
            "       - checkStatus: 是否需要审核：1-是，2-否\n" +
            "       - checkMsg: 审核错误提示\n" +
            "   - merType: MCCLIST\n" +
            "       - sysValue: sysValue\n" +
            "       - sysName: sysName\n" +
            "       - merMccType: merMccType\n" +
            "           - sysValue: sysValue\n" +
            "           - sysName: sysName\n";

    public static final String GET_MER_PRODUCT_LIST = "查询商户可以适用的业务产品\n" +
            "- 参数\n" +
            "   - agent_no: 登录代理商编号,必传\n" +
            "   - bp_type: 类型:1-个人，2-个体商户，3-企业商户,必传\n" +
            "   - sn: sn,必传\n" +
            "- 返回值\n" +
            "   - total: 总条数\n" +
            "   - bp_id: 业务产品ID\n" +
            "   - bp_name: 名称\n" +
            "   - sale_starttime: 可销售起始日期\n" +
            "   - sale_endtime: 可销售截止日期\n" +
            "   - proxy: 可否代理:1-可，0-否\n" +
            "   - bp_type: 类型:1-个人，2-个体商户，3-企业商户\n" +
            "   - is_oem: 是否OEM:1-是，0-否\n" +
            "   - team_id: 组织ID\n" +
            "   - own_bp_id: 关联自营业务产品ID\n" +
            "   - two_code: 二维码\n" +
            "   - remark: 说明\n" +
            "   - bp_img: 宣传图片\n" +
            "   - not_check: 证件资料完整时无需人工审核\n" +
            "   - total: 总条数\n";

    public static final String ADD_ACQ_MERINFO = "收单商户进件\n" +
            "- 参数\n" +
            "   - params: 封装图片以外的其他所有参数\n" +
            "       - agent_no: 代理商编号(传)\n" +
            "       - user_id: 登录用户ID(传)必传\n" +
            "       - mobilephone: 登录用户手机号(传),必传\n" +
            "       - acqMerInfo: 收单商户进件信息\n" +
            "           - id: \n" +
            "           - merchant_type: 进件类型:1个体收单商户，2-企业收单商户\n" +
            "           - merchant_name: 商户名称\n" +
            "           - legal_person: 法人姓名\n" +
            "           - legal_person_id: 法人身份证号\n" +
            "           - id_valid_start: 身份证有效期开始时间\n" +
            "           - id_valid_end: 身份证有效期结束时间\n" +
            "           - province: 经营地址(省)\n" +
            "           - city: 经营地址（市）\n" +
            "           - district: 经营地址（区）\n" +
            "           - address: 详细地址\n" +
            "           - one_scope: 一级经营范围\n" +
            "           - two_scope: 二级经营范围\n" +
            "           - charter_name: 营业执照名称\n" +
            "           - charter_no: 营业执照编号\n" +
            "           - charter_valid_start: 营业执照有效开始时间\n" +
            "           - charter_valid_end: 营业执照有效期结束时间\n" +
            "           - account_type: 账户类型 1 对私 2对公\n" +
            "           - bank_no: 银行卡号\n" +
            "           - account_name: 开户名\n" +
            "           - account_bank: 开户银行\n" +
            "           - account_province: 开户地区（省）\n" +
            "           - account_city: 开户地区（市）\n" +
            "           - account_district: 开户地区（区）\n" +
            "           - bank_branch: 支行\n" +
            "           - line_number: 联行号\n" +
            "           - acq_into_no: 进件编号\n" +
            "           - into_source: 进件来源\n" +
            "           - audit_status: 审核状态 1.正常 2.审核通过 3 审核不通过\n" +
            "           - audit_time: 审核时间\n" +
            "           - create_time: 进件时间\n" +
            "           - agent_no: 所属代理商\n" +
            "           - oneAgent_no: 所属一级代理商\n" +
            "           - examination_opinions: 审核意见\n" +
            "           - mcc: mcc码\n" +
            "           - one_scope_name: 一级经营范围名称\n" +
            "           - two_scope_name: 二级经营范围名称\n" +
            "           - update_time: 修改时间\n" +
            "   - uploadAcqMerFileList: 收单商户进件附件\n" +
            "       - file_name: 文件名称(包含后缀)(传)需拼接上文件类型 例:img_9.jpg,9为file_type 文件类型; \n" +
            "       - file: 文件内容(传)\n" +
            "- 返回值\n" +
            "   - 状态信息\n";

    public static final String INSERT_MERCHANT_INFO = "我要进件,保存数据\n" +
            "- 参数\n" +
            "    - params: 图片以外其他所有参数\n" +
            "       - agentNo: 代理商编号(传)\n" +
            "       - userId: 登录用户ID(传)\n" +
            "       - merchantNo: 商户编号(完善商户信息时,此参数必传)\n" +
            "       - sns: 机具SN号(传:格式123,1234)\n" +
            "       - oneAgentNo: 一级代理商编号\n" +
            "       - bpId: 业务产品ID\n" +
            "       - teamId: 组织ID\n" +
            "       - merInfo: 商户基本信息\n" +
            "           - address: 经营地址:详细地址\n" +
            "           - agentNo: 代理商ID\n" +
            "           - businessType: 经营范围-商户类别：餐娱类；批发类；民生类；一般类；房车类；其他；(传,假设传参为1代表民生类)\n" +
            "           - province: 经营地址（省）\n" +
            "           - city: 经营地址（市）\n" +
            "           - createTime: 创建时间\n" +
            "           - creator: 创建人\n" +
            "           - email: Email\n" +
            "           - examinationOpinions: 审核意见\n" +
            "           - id: id\n" +
            "           - idCardNo: 法人身份证号(传)\n" +
            "           - industryType: 行业类型(传MCC)\n" +
            "           - industryMcc: 行业类型(MCC值如1101)\n" +
            "           - lastUpdateTime: 最后更新时间\n" +
            "           - lawyer: 法人姓名(传)\n" +
            "           - mender: 修改人\n" +
            "           - merchantNo: 商户ID\n" +
            "           - merchantName: 商户名称(传)\n" +
            "           - merchantType: 商户类型:1-个人，2-个体商户，3-企业商户\n" +
            "           - mobilephone: 手机号(传)\n" +
            "           - operator: 业务人员\n" +
            "           - parentNode: 上级代理商节点,必传\n" +
            "           - posType: 设备类型 1移联商宝,2传统POS,3移小宝,4移联商通,5超级刷\n" +
            "           - remark: 备注(传)\n" +
            "           - saleName: 销售人员（谁拓展的商户）\n" +
            "           - status: 状态\n" +
            "       - merBusPro: 商户业务产品，默认单次只能选一个业务产品\n" +
            "           - bpId: 业务产品ID(必传)\n" +
            "           - createTime: 申请时间\n" +
            "           - id: id\n" +
            "           - merchantNo: 商户ID\n" +
            "           - saleName: 所属销售(传)\n" +
            "           - status: 状态(传,传O为待审核)\n" +
            "       - merCardInfo: 商户结算信息\n" +
            "           - accountName: 开户名(传)\n" +
            "           - accountNo: 开户账号(传)\n" +
            "           - accountType: 账户类型:1-对公,2-对私(传)\n" +
            "           - bankName: 开户行全称(传)\n" +
            "           - cardType: 类型:借记卡、贷记卡(传)\n" +
            "           - cnapsNo: 联行行号(传)\n" +
            "           - createTime: 创建时间\n" +
            "           - defQuickPay: 是否默认快捷支付:1-是,2-否\n" +
            "           - defSettleCard: 是否默认结算卡:1-是,2-否\n" +
            "           - id: id\n" +
            "           - merchantNo: 商户ID\n" +
            "           - quickPay: 是否快捷支付:1-是,2-否\n" +
            "           - status: 状态\n" +
            "       - merServiceRate: 商户每一个服务对应的费率值\n" +
            "           - capping: 封顶\n" +
            "           - cardType: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡(传)\n" +
            "           - disabledDate: 失效时间\n" +
            "           - efficientDate: 生效时间\n" +
            "           - holidaysMark: 节假日标志:1-只工作日，2-只节假日，0-不限(传)\n" +
            "           - id: id\n" +
            "           - merchantNo: 商户ID\n" +
            "           - rate: 扣率\n" +
            "           - rateType: 费率类型:1-每笔固定金额，2-扣率，3-扣率带保底封顶，4-扣率+固定金额,5-单笔阶梯 扣率(传)\n" +
            "           - safeLine: 保底\n" +
            "           - serviceId: 服务ID(传)\n" +
            "           - singleNumAmount: 每笔固定值\n" +
            "           - ladder1Rate: 阶梯区间1费率\n" +
            "           - ladder1Max: 阶梯区间1上限\n" +
            "           - ladder2Rate: 阶梯区间2费率\n" +
            "           - ladder2Max: 阶梯区间2上限\n" +
            "           - ladder3Rate: 阶梯区间3费率\n" +
            "           - ladder3Max: 阶梯区间3上限\n" +
            "           - ladder4Rate: 阶梯区间4费率\n" +
            "           - ladder4Max: 阶梯区间4上限\n" +
            "           - serviceName: 服务名称\n" +
            "       - merServiceQuota: 商户每一个服务对应的商户限额\n" +
            "           - cardType: 银行卡种类:0-不限，1-只信用卡，2-只储蓄卡(传)\n" +
            "           - disabledDate: 失效时间\n" +
            "           - efficientDate: 生效时间\n" +
            "           - holidaysMark: 节假日标志:1-只工作日，2-只节假日，0-不限(传)\n" +
            "           - id: id\n" +
            "           - merchantNo: 商户ID\n" +
            "           - serviceId: 服务ID(传)\n" +
            "           - singleCountAmount: 单笔最大交易额(传)\n" +
            "           - singleDaycardAmount: 单日单卡最大交易额(传)\n" +
            "           - singleDaycardCount: 单日单卡最大交易笔数(传)\n" +
            "           - singleDayAmount: 单日最大交易额(传)\n" +
            "           - useable: \n" +
            "           - fixedMark: \n" +
            "           - serviceName: 服务名称\n" +
            "           - singleMinAmount: 单笔最小交易额(传)\n" +
            "       - merRequireItem: 结算信息资料项	\n" +
            "           - id: \n" +
            "           - merchantNo: 商户ID\n" +
            "           - mriId: 进件要求项ID\n" +
            "           - content: 附件名称包含后缀\n" +
            "           - status: 状态：0待审核；1通过；2审核失败\n" +
            "           - checkStatus: \n" +
            "           - subBank: 支行名称\n" +
            "           - : 支行名称\n" +
            "       - serviceInfo:  服务信息\n" +
            "           - serviceId: 服务ID\n" +
            "           - serviceName: 服务名称\n" +
            "           - serviceType: 服务类型:1-POS刷卡，2-扫码支付，3-快捷支付，4-账户提现\n" +
            "           - hardwareIs: 是否与硬件相关:1-是，0-否\n" +
            "           - bankCard: 可用银行卡集合:1-信用卡，2-银行卡，0-不限\n" +
            "           - exclusive: 可否单独申请:1-可，0-否\n" +
            "           - business: 业务归属\n" +
            "           - saleStarttime: 可销售起始日期\n" +
            "           - saleEndtime: 可销售终止日期\n" +
            "           - useStarttime: 可使用起始日期\n" +
            "           - useEndtime: 可使用终止日期\n" +
            "           - proxy: 可否代理:1-可，0-否\n" +
            "           - getcashId: 提现服务ID\n" +
            "           - rateCard: 费率是否区分银行卡种类:1-是，0-否\n" +
            "           - rateHolidays: 费率是否区分节假日:1-是，0-否\n" +
            "           - quotaHolidays: 限额是否区分节假日:1-是，0-否\n" +
            "           - quotaCard: 限额是否区分银行卡种类:1-是，0-否\n" +
            "           - oemId: OEM ID\n" +
            "           - remark: 备注\n" +
            "           - tFlag: T0T1标志：0-不涉及，1-T0，2-T1\n" +
            "           - cashSubject: 仅服务类型为账户提现，存储科目，账号\n" +
            "           - fixedRate: 费率固定标志:1-固定，0-不固定\n" +
            "           - fixedQuota: 额度固定标志:1-固定，0-不固定\n" +
            "           - serviceStatus: 服务状态：1开启，0关闭\n" +
            "           - tradStart: 交易开始时间\n" +
            "           - tradEnd: 交易截至时间\n" +
            "    - merFile: 商户附件信息\n" +
            "        - fileName: 文件名称(包含后缀)(传)\n" +
            "        - file: 文件内容(传)\n" +
            "- 返回值\n" +
            "       -状态信息\n";

    public static final String UPDATE_MERCHANT_INFO = "我要进件,保存数据\n" +
            "- 参数\n" +
            "    - params: 图片以外其他所有参数\n" +
            "       - merRequireItem: 结算信息资料项	\n" +
            "           - id: \n" +
            "           - merchantNo: 商户ID\n" +
            "           - mriId: 进件要求项ID\n" +
            "           - content: 附件名称包含后缀\n" +
            "           - status: 状态：0待审核；1通过；2审核失败\n" +
            "           - checkStatus: \n" +
            "           - subBank: 支行名称\n" +
            "           - : 支行名称\n" +
            "    - merFile: 商户附件信息\n" +
            "        - fileName: 文件名称(包含后缀)(传)\n" +
            "        - file: 文件内容(传)\n" +
            "- 返回值\n" +
            "       -状态信息\n";

    public static final String LIST_CAN_REPLACEBPINFO = "查询可替换的业务产品信息\n" +
            "- 返回值\n" +
            "   - mbpId: 进件id\n" +
            "   - bpId: 原业务产品id\n" +
            "   - bpName: 原业务产品名称\n" +
            "   - canReplaceBpList: 可替换的业务产品\n" +
            "       - bpId: 可替换的业务产品id\n" +
            "       - bpName: 可替换的业务产品名称\n";

    public static final String REPLACE_BUSINESS_PRODUCT = "替换业务产品\n" +
            "   - merchantNo: 要操作的商户\n" +
            "   - bpId: 原业务产品id\n" +
            "   - newBpId: 替换后的业务产品id\n";

    public static final String LIST_AGENT_INFO = "查询登陆代理商下的信息\n" +
            "- pageNo: 页码(从0开始), pageNo: 每页条数" +
            "- 参数\n" +
            "   - keyword: 代理商名字(模糊)/代理商编号(精确)/手机号(精确)\n" +
            "   - agentType: \n" +
            "       - '1': 直接下级代理商(不含本级), \n" +
            "       - '2': 查询所有链条下的代理商(含本级) \n" +
            "       - 其他: 查询所有链条下的代理商(含本级)\n" +
            "       - '3': 直属下级代理商(含本级), \n" +
            "       - '4': 链条下所有代理商(不含本级),\n";

    public static final String QUERY_MERCHANT_AND_AGENT_DATA = "参数\n" +
            "- queryScope: 查询返回\n" +
            "   - ALL: 全部\n" +
            "   - OFFICAL: 直属\n" +
            "   - CHILDREN: 下级\n" +
            "- agentNo: 查询代理商编号\n" +
            "";
    public static final String QUERY_MERCHANT_EARLY_WARNING = "" +
            "- 参数\n" +
            "   - queryScope: 查询返回\n" +
            "      - ALL: 全部\n" +
            "      - OFFICAL: 直属\n" +
            "      - CHILDREN: 下级\n" +
            "   - agentNo: 查询代理商编号\n" +
            "- 返回值:\n" +
            "   - id: 预警id(查询详情的时候需要回传)\n" +
            "   - warningType 预警类型\n" +
            "   - warningTitle 显示标题\n" +
            "   - warningImg 显示图标" +
            "   - warningUrl 跳转连接\n" +
            "   - waringCount 商户预警数量\n" +
            "";

    public static final String GET_MERCHANT_EARLY_WARNING_DETAILS = "" +
            "- 参数\n" +
            "   - queryScope: 查询返回\n" +
            "      - ALL: 全部\n" +
            "      - OFFICAL: 直属\n" +
            "      - CHILDREN: 下级\n" +
            "   - agentNo: 查询代理商编号\n" +
            "   - warningId: 预警id" +
            "- 返回值\n" +
            "   - 待定:有可能直接返回html页面\n";

    public static final String JUMP_MERCHANT_EARLY_WARNING_DETAILS = "跳转到商户预警详情页面,请求参数是request param" +
            "- 参数\n" +
            "   - queryScope: 查询返回\n" +
            "      - ALL: 全部\n" +
            "      - OFFICAL: 直属\n" +
            "      - CHILDREN: 下级\n" +
            "   - agentNo: 查询代理商编号\n" +
            "   - warningId: 预警id\n";

    public static final String WILDCARD_MER_LIST = "根据关键字模糊匹配商户列表，供前端下拉框选择" +
            "- 参数\n" +
            "   - merchantKey: 关键字（模糊匹配商户号和商户名称），非必传，位于请求body中\n\n";

	public static final String THREE_DATA_GET_CHILDREN_AGENT =  
			"- 参数\n" +
            "   - keyword: 查询关键值(搜索代理商名称/编号/手机号码)\n" +
            "   - pageNo: 1\n" + 
            "   - pageSize: 10\n" + 
            "- 返回值\n" +
            "   - agentName (代理商名称)\n" +
            "   - agentNo (代理商编号)\n";

	public static final String THREE_DATA_COLLECT_QUERY = 
			"- 参数\n" +
            "   - agentNo: 查询代理商编号\n" + 
            "   - teamId: 组织ID\n" + 
            "- 返回值\n" +
            "   - transSum: 累计交易量\n" +
            "   - merchantSum: 商户总数\n" +
            "   - terminalSum: 机具总数\n" +
            "   - activatedMerchantSum: 商户已激活总数\n" +
            "   - lastUpdateTime: 最后更新时间\n" 
            ;

	public static final String THREE_DATA_TEAM_SELECT_LIST =
			"- 参数\n" +
			"- 返回值\n" +
			"   - teamId: 组织ID\n" +
	        "   - teamName: 组织名称\n";

	public static final String GET_FLOOR9_TOKEN =
            "- 参数\n" +
            "   - agentNo: 登录代理商编号\n" +
            "- 返回值\n" +
            "   - code: 状态（调用成功返回1，失败返回0)\n" +
            "   - msg: 消息（当调用失败时，请在该字段返回失败原因）\n" +
            "   - token: 请求成功则返回token" ;

    public static final String CJT_PARAMS =
            "- 参数\n" +
            "   - clientFlag: 用来区分IOS和安卓,安卓传 android,IOS为iOS\n" +
            "- 返回值\n" +
            "   - shareUrl: \n" +
            "   - imgUrl: 安卓图片地址\n" +
            "   - saveUrl: \n";

	public static final String THREE_DATA_DETAIL =
			"- 参数\n" +
            "   - agentNo: 查询代理商编号\n" + 
            "   - teamId: 组织ID\n" + 
            "   - yearMonth: 年月 格式:201908\n" + 
            "- 返回值\n" +
            "   - transSum: 累计交易量\n" +
            "   - merchantSum: 商户总数\n" +
            "   - terminalSum: 机具总数\n" +
            "   - activatedMerchantSum: 商户已激活总数\n" +
            "   - lastUpdateTime: 统计时间\n" ;

    public static final String QUERY_MERCHANT_PARAMS =
            "返回数据结构请查看[文档](./doc/merchant/query_merchant_params.html)";

}
