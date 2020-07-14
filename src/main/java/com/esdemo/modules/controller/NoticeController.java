package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.LoginValid;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.ALiYunOssUtil;
import com.esdemo.frame.utils.GsonUtils;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.swagger.SwaggerNoteLmc;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.NoticeService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lmc
 * @date 2019/5/21 11:26
 */
@LoginValid(needLogin = false)
@SignValidate(needSign = false)
@Api(description = "公告查询")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询首页头条公告, is_profit=true 来标志收益消息" ,notes = SwaggerNoteLmc.GET_HOME_MSG)
    @PostMapping("/getHomeMsg")
    public ResponseBean getHomeMsg(@ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        String agent_no = userInfoBean.getAgentNo();
        Map<String, Object> oem_map = noticeService.getOemInfo(agent_no);
        String oem_type = StringUtils.filterNull(oem_map.get("oem_type"));
        //为空查询所有的
        if("".equals(oem_type)){
            oem_type = "0";
        }
        String one_level_id = StringUtils.filterNull(oem_map.get("one_level_id"));
        //是否是一级代理商
        String one_level_id_flag = "false";
        if (one_level_id.equals(agent_no)){
            one_level_id_flag = "true";
        }
        List<Map<String, Object>> list = noticeService.getHomeMsg(one_level_id_flag, oem_type);
        for(Map<String,Object> map : list){
            String title_img = StringUtils.filterNull(map.get("title_img"));
            title_img = ALiYunOssUtil.genUrl("agent-attch", title_img, new Date(new Date().getTime()+100000));
            map.put("title_img", title_img);
            String message_img = StringUtils.filterNull(map.get("message_img"));
            message_img = ALiYunOssUtil.genUrl("agent-attch", message_img, new Date(new Date().getTime()+100000));
            map.put("message_img", message_img);
            //加入是否是收益标志
            map.put("is_profit", false);
        }
        Map<String,Object> profit_map = new HashMap<>();
        profit_map.put("is_profit", true);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        Date m = c.getTime();

        String start_time = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(m);
        String end_time = new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(m);
        String profit = noticeService.getYesterdayIncome(agent_no, start_time, end_time); //
        profit_map.put("title", "昨日收入"+profit+"元");
        list.add(profit_map);
        return ResponseBean.success(list);
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "查询公告列表，点击该列表的公告详情直接提取需要的内容去显示就可以了,isPopup=1表示查询弹窗公告" ,notes = SwaggerNoteLmc.GET_MSG_LIST)
    @PostMapping("/getMsgList")
    public ResponseBean getMsgList(@ApiIgnore @CurrentUser UserInfoBean userInfoBean, @RequestBody String params) {
        String agent_no = userInfoBean.getAgentNo();
        Map<String, Object> params_map = GsonUtils.fromJson2Map(params, Object.class);
        int pageNo = Integer.parseInt(StringUtils.filterNull(params_map.get("pageNo")));
        int pageSize = Integer.parseInt(StringUtils.filterNull(params_map.get("pageSize")));
        int isPopup = Integer.parseInt(StringUtils.filterNull(params_map.get("isPopup")));
        Map<String, Object> oem_map = noticeService.getOemInfo(agent_no);
        String oem_type = StringUtils.filterNull(oem_map.get("oem_type"));
        //为空查询所有的
        if("".equals(oem_type)){
            oem_type = "0";
        }
        String one_level_id = StringUtils.filterNull(oem_map.get("one_level_id"));
        //是否是一级代理商
        String one_level_id_flag = "false";
        if (one_level_id.equals(agent_no)){
            one_level_id_flag = "true";
        }

        List<Map<String, Object>> list = null;
        if(isPopup ==1) {
            //设置分页信息，分别是当前页数和每页显示的总记录数
            PageHelper.startPage(pageNo, pageSize, false);
            list = noticeService.getPopupMsg(one_level_id_flag, oem_type);
        }else{
            //设置分页信息，分别是当前页数和每页显示的总记录数
            PageHelper.startPage(pageNo, pageSize, false);
            list = noticeService.getMsgList(one_level_id_flag, oem_type);
            //更新公告阅读时间
            noticeService.updatelastTime(userInfoBean.getUserId());
        }
        for(Map<String,Object> map : list){
            String title_img = StringUtils.filterNull(map.get("title_img"));
            title_img = ALiYunOssUtil.genUrl("agent-attch", title_img, new Date(new Date().getTime()+100000));
            map.put("title_img", title_img);
            String message_img = StringUtils.filterNull(map.get("message_img"));
            message_img = ALiYunOssUtil.genUrl("agent-attch", message_img, new Date(new Date().getTime()+100000));
            map.put("message_img", message_img);
        }
        return ResponseBean.success(list);
    }

//    @OldSwaggerDeveloped
//    @ApiOperation(value = "查询公告详情，针对首页滚动消息的nt_id进行查询")
//    @PostMapping("/getMsgDetail/{nt_id}")
//    public ResponseBean getMsgDetail(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,@PathVariable String nt_id) {
//        Map<String, Object> msg_map = noticeService.getMsgDetail(nt_id);
//        String title_img = StringUtils.filterNull(msg_map.get("title_img"));
//        title_img = ALiYunOssUtil.genUrl("agent-attch", title_img, new Date(64063065600000L));
//        msg_map.put("title_img", title_img);
//        String message_img = StringUtils.filterNull(msg_map.get("message_img"));
//        message_img = ALiYunOssUtil.genUrl("agent-attch", message_img, new Date(64063065600000L));
//        msg_map.put("message_img", message_img);
//        return ResponseBean.success(msg_map);
//    }

}
