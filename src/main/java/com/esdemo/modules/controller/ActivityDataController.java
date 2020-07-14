package com.esdemo.modules.controller;


import com.esdemo.frame.annotation.CacheData;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.swagger.ActivityDataCountSwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.service.ActivityDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequestMapping("/activityData")
@Api(description = "活动数据模块")
@RestController
public class ActivityDataController  {
    @Autowired
    private ActivityDataService activityDataService;


    @ApiOperation(value = "数据模块活动数据汇总", notes = ActivityDataCountSwaggerNotes.activityDataCountQuery,httpMethod = "POST")
    @PostMapping("/activityDataCountQuery")
    @KqSwaggerDeveloped
    @CacheData()
    public ResponseBean activityDataCountQuery(@ApiIgnore @CurrentUser UserInfoBean userInfoBean ,@RequestBody ActivityAndDataQueryBean activityAndDataQueryBean) {
        activityAndDataQueryBean.setUserInfoBean(userInfoBean);
        ResponseBean result = activityDataService.activityDataCountQuery(activityAndDataQueryBean);
        return  result;
    }

    @RequestMapping("/activityDataTypeQuery")
    @ApiOperation(value = "数据模块活动详情下拉框数据", notes = ActivityDataCountSwaggerNotes.activityDataTypeQuery,httpMethod = "POST")
    @KqSwaggerDeveloped
    @CacheData()
    public ResponseBean activityDataTypeQuery(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,@RequestBody ActivityAndDataQueryBean activityAndDataQueryBean){
        activityAndDataQueryBean.setUserInfoBean(userInfoBean);
        ResponseBean responseBean = activityDataService.activityDataTypeQuery(activityAndDataQueryBean);
        return responseBean;
    }

    @ApiOperation(value = "数据模块活动数据商户列表查询", notes = ActivityDataCountSwaggerNotes.activityDataCountDetailQuery,httpMethod = "POST")
    @PostMapping("/activityDataCountDetailQuery/{pageNo}/{pageSize}")
    @KqSwaggerDeveloped
    @CacheData()
    public ResponseBean activityDataCountDetailQuery(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
            @PathVariable(required = false) int pageNo,
            @PathVariable(required = false) int pageSize,@RequestBody ActivityAndDataQueryBean activityAndDataQueryBean) {
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;
        activityAndDataQueryBean.setUserInfoBean(userInfoBean);
        activityAndDataQueryBean.setCountOrDetail("detail");
        activityAndDataQueryBean.setPageNo(pageNo);
        activityAndDataQueryBean.setPageSize(pageSize);
        ResponseBean result = activityDataService.activityDataCountDetailQuery(activityAndDataQueryBean);
        return  result;
    }
}
