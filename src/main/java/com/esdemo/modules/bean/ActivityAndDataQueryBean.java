package com.esdemo.modules.bean;

import com.esdemo.frame.enums.ActivityDataEnum;
import com.esdemo.frame.enums.ActivityEnum;
import com.esdemo.frame.enums.QueryScope;
import lombok.Data;

@Data
public class ActivityAndDataQueryBean {

    private UserInfoBean userInfoBean; //当前登录代理商
    private QueryScope queryScope = QueryScope.ALL; //查询类型
    private String agentNo;                 // 查询代理商编号
    private String agentNode;               // 查询代理商编号
    private String startTime; //开始时间
    private String endTime; // 结束时间
    private String merchantNo; //商户编号
    private String merchantName; // 商户名称
    private String phone; //手机号
    private ActivityEnum subType;  // 活动类型  1 欢乐返  2 新欢乐送
    private String activityTypeNo; // 活动子类型 编号
    private ActivityDataEnum activityData;  //参与活动
    private String activityDataStatus;  //参与子活动状态 0 未开始  1 考核中  2 已达标    3 未达标  4  需扣款 , 5  无需扣款
    private String sortType; // 按照活动截止日期排序  传  desc 降序 asc 升序
    private String startActivityTime; //截止开始日期
    private String endActivityTime; // 截止结束日期
    private String countOrDetail;//count 统计 detail 详情
    private String rewardStatus; // 奖励考核达标状态 0 考核中 1 已达标  2 未达标
    private String deductionStatus;// 扣款考核状态 0 考核中 1 需扣款 2 无需扣款
    private int pageSize = 10;
    private int pageNo= 1;
}
