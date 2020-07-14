package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.MyUtil;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.TerminalApplyRecord;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.TerminalApplyRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author tgh
 * @description  机具申请
 * @date 2019/5/24
 */
@Slf4j
@Api(description = "机具申请模块")
@RestController
@RequestMapping("/terminalApplyRecord")
public class TerminalApplyRecordController {

    @Resource
    private TerminalApplyRecordService terminalApplyRecordService;

    @OldSwaggerDeveloped
    @ApiOperation(value = "机具申请记录查询",notes = SwaggerNotes.GET_TERMINAL_APPLY_RECORD)
    @PostMapping("/getTerminalApplyRecord/{pageNo}/{pageSize}")
    public ResponseBean getTerminalApplyRecord(@PathVariable Integer pageNo,
                                               @PathVariable Integer pageSize,
                                               @RequestBody String params,
                                               @ApiIgnore @CurrentUser UserInfoBean userInfoBean){
        log.info("机具申请记录查询 请求参数 ===>{}, pageNo:{}, pageSize:{}",params,pageNo,pageSize);
        try {
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            String agentNo = paramsMap.get("user_id");
            if (StringUtils.isBlank(agentNo) || !agentNo.equals(userInfoBean.getAgentNo())){
                return ResponseBean.error("参数有误");
            }
            paramsMap.put("agent_node", userInfoBean.getAgentNode());
            PageHelper.startPage(pageNo, pageSize,false);
            List<Map<String,Object>> list = terminalApplyRecordService.getTerminalApplyRecord(paramsMap);
            if (list.size() < 1){
                return ResponseBean.success(null,"数据为空");
            }
            PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(MyUtil.listToBeans(TerminalApplyRecord.class, list));
            return ResponseBean.success(pageInfo);
        }catch (Exception e){
            log.error("机具申请记录查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "更新机具申请记录",notes = SwaggerNotes.UPDATE_MODIFY_TERMINAL_APPLY_RECORD)
    @PostMapping("/updateModifyTerminalApplyRecord")
    public ResponseBean updateModifyTerminalApplyRecord(@RequestBody String params,
                                                        @ApiIgnore @CurrentUser UserInfoBean userInfoBean){
        log.info("更新机具申请记录 请求参数 ===>{}",params);
        try {
            Map<String, String> paramsMap = GsonUtils.fromJson2Map(params, String.class);
            Map<String, Object> map = terminalApplyRecordService.updateModifyTerminalApplyRecord(paramsMap, userInfoBean);
            return ResponseBean.of((Boolean)map.get("status"),map.get("msg").toString());
        }catch (Exception e){
            log.error("机具申请记录查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "机具申请记录统计",notes = SwaggerNotes.COUNT_TERMINAL_APPLY_RECORD)
    @GetMapping("/countTerminalApplyRecord/{user_id}")
    public ResponseBean countTerminalApplyRecord(@PathVariable String user_id,
                                               @ApiIgnore @CurrentUser UserInfoBean userInfoBean){
        log.info("机具申请记录统计 请求参数 user_id===>{}",user_id);
        try {
            String agentNo = userInfoBean.getAgentNo();
            if (user_id.equals(agentNo)){
                int count = terminalApplyRecordService.countTerminalApplyRecord(userInfoBean.getAgentNode());
                log.info("查询到 {} 条数据 " , count);
                return ResponseBean.success("查询成功",count);
            }
            log.info("所传user_id " + user_id + "不是当前登录代理商编号 ==>" + agentNo);
            return ResponseBean.error("非法操作");
        }catch (Exception e){
            log.error("机具申请记录统计异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "获取所有活动信息",notes = SwaggerNotes.GET_ALL_ACTIVITY_INFO)
    @PostMapping("/getAllActivityInfo")
    public ResponseBean getAllActivityInfo(){
        try {
            return ResponseBean.success(terminalApplyRecordService.getAllActivityInfo());
        }catch (Exception e){
            log.error("获取所有活动信息异常",e);
            return ResponseBean.error("查询异常");
        }
    }
}
