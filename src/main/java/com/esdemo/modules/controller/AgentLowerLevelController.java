package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.swagger.AgentLowerLevelSwaggerNotes;
import com.esdemo.modules.bean.*;
import com.esdemo.modules.bean.Vo.*;
import com.esdemo.modules.service.AgentLowerLevelDataService;
import com.esdemo.modules.service.AgentLowerLevelService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "代理商开设下级接口模块")
@RequestMapping("/agentLowerLevel")
@RestController
@Slf4j
public class AgentLowerLevelController {

    @Resource
    private AgentLowerLevelService agentLowerLevelService;

    @Resource
    private AgentLowerLevelDataService agentLowerLevelDataService;

    @KqSwaggerDeveloped
    @ApiOperation(value = "分页查询代理商所有下级",notes = AgentLowerLevelSwaggerNotes.AGENT_LOWER_LEVEL_DOC)
    @PostMapping ("/getAgentLowerLevelAllList/{pageNo}/{pageSize}")
    public ResponseBean getAgentLowerLevelAllList(@RequestBody(required = false) AgentLowerLevelFilter queryInfo, @PathVariable Integer pageNo,
                                         @PathVariable Integer pageSize, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 10 ? 10 : pageSize;
        long count = 0;
        List<AgentLowerLevelInfoVo> list = null;
        if(queryInfo==null){
            queryInfo=new AgentLowerLevelFilter();
        }
        if(!StringUtils.isNotBlank(queryInfo.getAgentNo())){
            queryInfo.setAgentNo(userInfoBean.getAgentNo());
        }
        //第一页查询总页数，其它页不查询
        if (pageNo == 1) {
            Page page = PageHelper.startPage(pageNo, pageSize);
            list = agentLowerLevelService.getAgentLowerLevelAllList(userInfoBean, queryInfo);
            count = page.getTotal();
        } else {
            PageHelper.startPage(pageNo, pageSize, false);
            list = agentLowerLevelService.getAgentLowerLevelAllList(userInfoBean, queryInfo);
        }
        return ResponseBean.success(list, count);
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "分页查询代理商所有待设置下级",notes = AgentLowerLevelSwaggerNotes.AGENT_LOWER_LEVEL_TOBESET_DOC)
    @PostMapping ("/getAgentLowerLevelToBeSetList/{pageNo}/{pageSize}")
    public ResponseBean getAgentLowerLevelToBeSetList(@PathVariable Integer pageNo, @PathVariable Integer pageSize,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        ResponseBean responseBean=agentLowerLevelService.getAgentLowerLevelToBeSetList(pageNo,pageSize,userInfoBean);
        return responseBean;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "待设置代理商忽略操作",notes = AgentLowerLevelSwaggerNotes.SETAGENTTOBESETIGNORE_DOC)
    @PostMapping ("/setAgentToBeSetIgnore/{agentNo}")
    public ResponseBean setAgentToBeSetIgnore(@PathVariable String agentNo,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        AgentLowerLevelInfoVo info=agentLowerLevelService.getAgentLowerLevelDetail(agentNo);
        if(info==null){
            return ResponseBean.error("该代理商不存在!");
        }else{
            if(!userInfoBean.getAgentNo().equals(info.getParentId())){
                return ResponseBean.error("当前登入代理商没有权限操作该代理商!");
            }
        }
        int num=agentLowerLevelService.setAgentToBeSetIgnore(agentNo);
        if(num>0){
            return ResponseBean.success(null,"忽略操作成功!");
        }else{
            return ResponseBean.error("忽略操作失败!");
        }
    }


    @KqSwaggerDeveloped
    @ApiOperation(value = "代理商详情设置数据下发",notes = AgentLowerLevelSwaggerNotes.GETAGENTDETAILEDIT_DOC)
    @PostMapping ("/getAgentDetailEdit/{agentNo}")
    public ResponseBean getAgentDetailEdit(@PathVariable String agentNo,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        ResponseBean result=agentLowerLevelDataService.getAgentDetailEdit(agentNo,userInfoBean);
        return result;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "本级绑定结算卡下发数据",notes = AgentLowerLevelSwaggerNotes.GETBINDINGSETTLEMENTCARDBEFOREDATA_DOC)
    @PostMapping("/getBindingSettlementCardBeforeData")
    public ResponseBean getBindingSettlementCardBeforeData(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        ResponseBean result=agentLowerLevelService.getBindingSettlementCardBeforeData(userInfoBean);
        return result;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "本级绑定结算卡",notes = AgentLowerLevelSwaggerNotes.SETBINDINGSETTLEMENTCARD_DOC)
    @PostMapping("/setBindingSettlementCard")
    public ResponseBean setBindingSettlementCard(@RequestBody AccountCardUp accountCard,
                                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        ResponseBean result=agentLowerLevelService.setBindingSettlementCard(accountCard,userInfoBean);
        return result;
    }
    @KqSwaggerDeveloped
    @ApiOperation(value = "新增下级代理商-代理商基础信息校验",notes = AgentLowerLevelSwaggerNotes.CHECKAGENTBASE_DOC)
    @PostMapping("/checkAgentBase")
    public ResponseBean checkAgentBase(@RequestBody AgentLowerLevelVo params,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        AgentLowerLevelUp agentInfo=params.getAgentInfo();
        ResponseBean result=agentLowerLevelService.checkAgentBase(agentInfo,userInfoBean);
        return result;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "新增下级代理商-结算卡校验",notes = AgentLowerLevelSwaggerNotes.CHECKAGENTANDCARD_DOC)
    @PostMapping("/checkAgentCard")
    public ResponseBean checkAgentCard( @RequestBody AgentLowerLevelVo params,
                                           @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        AccountCardUp accountCard=params.getAccountCard();
        ResponseBean result=agentLowerLevelService.checkAgentCard(accountCard,userInfoBean);
        return result;
    }


    @KqSwaggerDeveloped
    @ApiOperation(value = "修改时-校验业务产品服务费率设置",notes = AgentLowerLevelSwaggerNotes.CHECKBPSERVICEEDIT_DOC)
    @PostMapping("/checkbpServiceEdit/{agentNo}")
    public ResponseBean checkbpServiceEdit(@RequestBody AgentLowerLevelVo params, @PathVariable String agentNo, @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        List<AgentBpIdInfoVo> bpList=params.getBpList();
        ResponseBean result=agentLowerLevelService.checkbpService(bpList,agentNo,userInfoBean);
        return result;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "新增下级代理商-校验业务产品服务费率设置",notes = AgentLowerLevelSwaggerNotes.CHECKBPSERVICEADD_DOC)
    @PostMapping("/checkbpServiceAdd")
    public ResponseBean checkbpServiceAdd(@RequestBody AgentLowerLevelVo params,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        List<AgentBpIdInfoVo> bpList=params.getBpList();
        ResponseBean result=agentLowerLevelService.checkbpService(bpList,null,userInfoBean);
        return result;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "新增下级代理商下发前置数据",notes = AgentLowerLevelSwaggerNotes.GETAGENTDETAILADD_DOC)
    @PostMapping("/getAgentDetailAdd")
    public ResponseBean getAgentDetailAdd(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        ResponseBean result=agentLowerLevelDataService.getAgentDetailAdd(userInfoBean);
        return result;
    }


    @KqSwaggerDeveloped
    @ApiOperation(value = "新增下级代理商",notes = AgentLowerLevelSwaggerNotes.ADDAGENTLOWERLEVEL_DOC)
    @PostMapping("/addAgentLowerLevel")
    public ResponseBean addAgentLowerLevel(@RequestBody AgentLowerLevelVo params,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        //用来缓存数据带出保存方法
        Map<String, String> agentNoMap=new HashMap<>();
        ResponseBean result=agentLowerLevelService.addAgentLowerLevel(params,userInfoBean,agentNoMap);
        if(result.isSuccess()){
            //如果新增代理商成功，事务提交后调用账户开户
            String agentNo=agentNoMap.get("agentNo");
            agentLowerLevelService.openAgentAccount(agentNo);
        }
        return result;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "修改服务费率和活动数据保存",notes = AgentLowerLevelSwaggerNotes.EDITAGENTLOWERLEVEL_DOC)
    @PostMapping("/editAgentLowerLevel/{agentNo}")
    public ResponseBean editAgentLowerLevel( @PathVariable String agentNo, @RequestBody AgentLowerLevelVo params,@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        ResponseBean result=agentLowerLevelService.editAgentLowerLevel(agentNo,params,userInfoBean);
        return result;
    }
}
