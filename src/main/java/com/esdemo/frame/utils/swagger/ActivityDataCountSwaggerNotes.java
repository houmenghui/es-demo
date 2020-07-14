package com.esdemo.frame.utils.swagger;

public final class ActivityDataCountSwaggerNotes {

    public final static String activityDataCountQuery = "数据活动数据汇总\n" +
            "- 接口地址：/activityData/activityDataCountQuery\n" +
            "- 请求参数\n" +
            "   - queryScope 查询类型 ALL全部数据 OFFICAL 直营数据  CHILDREN 下级数据 \n" +
            "   - agentNo 当前选择代理商编号 非必填 \n" +
            "   - startTime 开始日期 非必填 \n  " +
            "   - endTime 结束日期  非必填 \n" +
            "- 返回数据\n" +
            "   - code: 返回状态码，200成功\n" +
            "   - message: 错误信息\n" +
            "   - data: 数据集\n" +
            "   - success: 是否成功\n\n" +
            "- 数据集字段说明\n" +
            "   - activityName 活动名称\n" +
            "   - subType 活动标识\n" +
            "   - activtyMerCount 总商户数\n" +
            "   - subActivity 子活动列表 \n" +
            "       - activityDataName 子活动名称\n" +
            "       - activityData  // 子活动表示 对应 明细筛选字段的参加活动\n" +
            "       - statusType; // 状态类型 // rewardStatus 奖励考核  deductionStatus 扣款考核\n" +
            "       - examine 考核中数据\n" +
            "       - examineName 对应标题文字\n" +
            "       - reachStandard 已达标\n" +
            "       - reachStandardName 对应标题文字\n" +
            "       - notStandard 未达标\n" +
            "       - notStandardName 对应标题文字\n" +
            "       - notBegin 未开始\n" +
            "       - notBeginName 对应标题文字\n" +
            "- 返回数据结构请查看[文档](./doc/activityData/activity_data_count_query.html)";



    public final static String activityDataTypeQuery =  "数据活动数据汇总\n" +
            "- 接口地址：/activityData/activityDataCountQuery\n" +
            "- 请求参数\n" +
            "   -queryScope 查询类型 ALL全部数据 OFFICAL 直营数据  CHILDREN 下级数据 \n" +
            "   -agentNo 当前选择代理商编号 非必填 获取当前登录代理商时可不用填写编号\n" +
            "- 返回数据\n" +
            "   - code: 返回状态码，200成功\n" +
            "   - message: 错误信息\n" +
            "   - data: 数据集\n" +
            "   - success: 是否成功\n\n" +
            "- 数据集字段说明\n" +
            "   - 响应数据为resp 则获取第一个下拉框(此参数固定) 活动类型 resp.get(\"data\").get(\"activitys\")\n" +
            "   - 选择活动类型之后 使用选择的value  获取子类型 下拉框 例：  resp.get(\"data\").get(\"happyBack\")\n" +
            "   - 参加活动字段不联动 固定取key activityData \n" +
            "   - sys_value 上送的值\n" +
            "   - sys_name 显示的值\n" +
            "- 返回数据结构请查看[文档](./doc/activityData/activity_data_type_query.html)";



    public final static String activityDataCountDetailQuery =  "数据活动数据汇总明细\n" +
            "- 接口地址：/activityData/activityDataCountDetailQuery/{pageNo}/{pageSize}\n" +
            "- 请求参数\n" +
            "   - queryScope 查询类型 ALL全部数据 OFFICAL 直营数据  CHILDREN 下级数据 \n" +
            "   - agentNo 当前选择代理商编号 非必填 \n" +
            "   - startTime 开始日期 开始 非必填\n" +
            "   - endTime 结束日期 结束 非必填 \n" +
            "   - merchantNo 商户编号 \n" +
            "   - phone 手机号 非必填\n" +
            "   - subType 活动类型 happyBack 欢乐返  newHappyGive 新欢乐送 \n" +
            "   - activityTypeNo 活动子类型编号 \n" +
            "   - activityData 子活动\n" +
            "   - sortType 排序 desc 降序 asc 升序\n" +
            "   - startActivityTime 截止开始日期  开始\n" +
            "   - endActivityTime 截止结束日期  开始 \n" +
            "   - rewardStatus; // 奖励考核达标状态 0 考核中 1 已达标  2 未达标 \n" +
            "   - deductionStatus;// 扣款考核状态 0 考核中 1 需扣款 2 无需扣款 \n" +
            "   - activityDataStatus;//参与子活动状态 0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款 \n" +
            "- 返回数据结构请查看[文档](./doc/activityData/activity_data_count_detail_query.html)";

}
