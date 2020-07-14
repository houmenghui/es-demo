package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.KqSwaggerDeveloped;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.SwaggerNoteLmc;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.MachineManageService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lmc
 * @date 2019/5/16 14:51
 */
@Api(description = "机具管理")
@RestController
@RequestMapping("/machinemanage")
public class MachineManageController {
    @Resource
    private MachineManageService machineManageService;


    @KqSwaggerDeveloped
    @ApiOperation(value = "机具筛选查询信息", notes = SwaggerNoteLmc.QUERY_MACHINE_INFO)
    @PostMapping("/getAllByCondition")
    public ResponseBean getAllByCondition(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        ResponseBean responseBean=machineManageService.getAllByCondition(userInfoBean,params);
        return responseBean;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "机具管理-机具下发/回收操作", notes = SwaggerNoteLmc.MANAGE_TERMINAL)
    @PostMapping("/manageTerminal")
    public ResponseBean manageTerminal(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        ResponseBean responseBean=machineManageService.manageTerminal(userInfoBean,params);
        return responseBean;
    }

    @KqSwaggerDeveloped
    @ApiOperation(value = "机具解绑", notes = SwaggerNoteLmc.TERMINAL_RELEASE)
    @PostMapping("/terminalRelease")
    public ResponseBean terminalRelease(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        ResponseBean responseBean=machineManageService.terminalRelease(userInfoBean,params);
        return responseBean;
    }

    /**
     * 查询当前代理商勾选的欢乐返子类型
     */
    @KqSwaggerDeveloped
    @ApiOperation(value = "查询当前代理商勾选的欢乐返子类型", notes = SwaggerNoteLmc.QUERY_ACTIVITY_TYPES)
    @PostMapping("/getActivityTypes")
    public ResponseBean getAgentActivity(@ApiIgnore @CurrentUser UserInfoBean userInfoBean){
        List<Map<String, Object>> list = machineManageService.getActivityTypes(userInfoBean.getAgentNo());
        Map<String, Object> map = new HashMap<>();
        map.put("activity_type_no", "");
        map.put("activity_type_name", "全部");
        list.add(0, map);
        return ResponseBean.success(list);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "机具流动记录列表查询", notes = SwaggerNoteLmc.SN_SEND_AND_REC_INFO)
    @PostMapping("/getSnSendAndRecInfo")
    public ResponseBean getSnSendAndRecInfo(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        //筛选字段
        //oper_type  必填，筛选栏类型 1-入库  2-出库
        //date_start  格式YYYY-MM-DD 00:00:00,选填, 但是date_start和date_end 必须同时填或者不填
        //date_end  格式YYYY-MM-DD 23:59:59,选填，但是date_start和date_end 必须同时填或者不填
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        params_map.put("agent_no", userInfoBean.getAgentNo());
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        if (pageNo == 1) {
            List<Map<String, Object>> list = machineManageService.getSnSendAndRecInfo(params_map);
            int count = 0;
            for (Map<String, Object> map : list) {
                count += Integer.parseInt(map.get("oper_num").toString());
            }
            return ResponseBean.success(list, count);
        } else {
            //设置分页信息，分别是当前页数和每页显示的总记录数
            PageHelper.startPage(pageNo, pageSize, false);
            List<Map<String, Object>> list = machineManageService.getSnSendAndRecInfo(params_map);
            return ResponseBean.success(list);
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "机具流动记录详情查询", notes = SwaggerNoteLmc.SN_SEND_AND_REC_DETAIL)
    @PostMapping("/getSnSendAndRecDetail")
    public ResponseBean getSnSendAndRecDetail(@RequestBody String params) {
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        String id = StringUtils.filterNull(params_map.get("id"));
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        String sn_str = machineManageService.getSnSendAndRecDetail(id);
        String[] sn_array = sn_str.split(",");
        List<String> list = new ArrayList<>();
        int currIdx = (pageNo > 1 ? (pageNo - 1) * pageSize : 0);
        for (int i = 0; i < pageSize && i < sn_array.length - currIdx; i++) {
            String sn = sn_array[currIdx + i];
            list.add(sn);
        }
        return ResponseBean.success(list, sn_array.length);
    }

}
