package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.utils.*;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.SurveyOrderInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tgh
 * @description 调单管理
 * @date 2019/5/16
 */
@Api(description = "调单管理模块")
@RestController
@Slf4j
@RequestMapping("/surveyOrderInfo")
public class SurveyOrderInfoController {

    @Resource
    private SurveyOrderInfoService surveyOrderInfoService;

    @OldSwaggerDeveloped
    @ApiOperation("地址下拉列表")
    @GetMapping("/selectAdrees")
    public ResponseBean selectAdrees() {
        try {
            Map<String, Object> map = surveyOrderInfoService.selectAddrees();
            if (map == null) {
                map = new HashMap<>();
                map.put("all", "all");
            }
            return ResponseBean.success(map);
        }catch (Exception e){
            log.error("地址下拉列表查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "条件查询下拉列表",notes = "条件查询下拉列表,参数sysKey")
    @GetMapping("/selectBySysKey/{sysKey}")
    public ResponseBean selectBySysKey(@PathVariable String sysKey) {
        log.info("条件查询下拉列表 请求参数==> {}",sysKey);
        try {
            List<Map<String,Object>> list = null;
            if ("ORDER_DEAL_STATUS".equals(sysKey)) {
                list = surveyOrderInfoService.orderDealStatusList();
            }else{
                list = surveyOrderInfoService.selectBySysKey(sysKey);
            }
            log.info("查询key = " + sysKey + " 的数据字典共===" + list.size() + "===条数据==");
            return ResponseBean.success(list);
        }catch (Exception e){
            log.error("条件查询下拉列表查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "调单管理条件查询",notes = SwaggerNotes.SELECT_SURVEY_ORDER_BY_CONDITIONS)
    @PostMapping("/selectSurveyOrderByConditions/{pageNo}/{pageSize}")
    public ResponseBean selectSurveyOrderByConditions(@RequestBody String params,
                                                      @PathVariable int pageNo,
                                                      @PathVariable int pageSize,
                                                      @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("调单管理条件查询 请求参数==> {},pageNo:{},pageSize:{}",params,pageNo,pageSize);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params,Object.class);
            if (paramsMap == null || paramsMap.get("agent_no") == null){
                return ResponseBean.error("参数有误");
            }
            String agentNo = paramsMap.get("agent_no").toString();
            String entityId = userInfoBean.getAgentNo();
            if (!entityId.equals(agentNo)){
                log.info("所传登录代理商编号 {} 跟当前登录代理商编号 {} 不同",agentNo,entityId);
                return ResponseBean.error("无权操作");
            }
            paramsMap.put("entityNode",userInfoBean.getAgentNode());
            PageHelper.startPage(pageNo, pageSize,false);
            PageInfo<Map<String, Object>> pageInfo =
                    new PageInfo<>(surveyOrderInfoService.selectSurveyOrderByConditions(paramsMap,userInfoBean));
            return ResponseBean.success(pageInfo);
        }catch (Exception e){
            log.error("查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "调单详情",notes = SwaggerNotes.SELECT_SURVEY_ORDER_DETAIL)
    @GetMapping("/selectSurveyOrderDetail/{order_no}")
    public ResponseBean selectSurveyOrderDetail(@PathVariable String order_no,
                                                @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("调单详情查询 请求参数==> {}",order_no);
        try {
            return ResponseBean.success(surveyOrderInfoService.selectSurveyOrderDetail(order_no,userInfoBean));
        }catch (Exception e){
            log.error("调单详情查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "回复记录",notes = SwaggerNotes.SELECT_REPLY_RECORD)
    @GetMapping("/selectReplyRecord/{pageNo}/{pageSize}/{order_no}")
    public ResponseBean selectReplyRecord(@PathVariable String order_no,
                                          @PathVariable int pageNo,
                                          @PathVariable int pageSize) {
        log.info("回复记录查询 请求参数==> order_no:{},pageNo:{},pageSize:{}",order_no,pageNo,pageSize);
        try {
            if (StringUtils.isBlank(order_no)){
                return ResponseBean.error("参数有误","调单编号不能为空");
            }
            PageHelper.startPage(pageNo, pageSize,false);
            List<Map<String, Object>> list = surveyOrderInfoService.selectReplyRecord(order_no);
            if (list.size() == 0){
                return ResponseBean.success(null,"数据为空");
            }
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
            return ResponseBean.success(pageInfo);
        }catch (Exception e){
            log.error("回复记录查询异常",e);
            return ResponseBean.error("查询异常");
        }
    }

    @SignValidate(needSign = false)
    @OldSwaggerDeveloped
    @ApiOperation(value = "调单回复提交,修改",notes = SwaggerNotes.INSERT_OR_UPDATE_REPLY)
    @PostMapping("/insertOrUpdateReply")
    public ResponseBean insertOrUpdateReply(@RequestParam("params") String params,
                                            HttpServletRequest request,
                                            @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("调单回复提交,修改 请求参数==> {}",params);
        try {
            Map<String, Object> paramsMap = GsonUtils.fromJson2Map(params,Object.class);
            if (paramsMap == null || paramsMap.get("order_no") == null || paramsMap.get("continueCommit") == null
                    || paramsMap.get("agent_node") == null || paramsMap.get("agent_no") == null){
                log.info("参数====> {} ",paramsMap);
                return ResponseBean.error("参数有误");
            }
            //图片上传
            paramsMap.put("reply_files_name", uploadFile(request));
            return ResponseBean.success(surveyOrderInfoService.insertOrUpdateReply(paramsMap,userInfoBean));
        }catch (Exception e){
            log.error("调单回复提交,修改异常",e);
            return ResponseBean.error("操作异常");
        }
    }

    private String uploadFile(HttpServletRequest request) throws IOException {
        String attachment = "";
        if(request instanceof MultipartRequest){
            MultipartRequest qq = (MultipartRequest)request;
            Map<String, MultipartFile> maps = qq.getFileMap();
            MultiValueMap<String, MultipartFile> multiFileMap = qq.getMultiFileMap();
            List<MultipartFile> fileList = multiFileMap.get("file");
            if (fileList != null && fileList.size() > 0){//安卓
                for (MultipartFile file : fileList) {
                    InputStream inputStream = file.getInputStream();
                    String fileName = file.getOriginalFilename();
                    attachment = returnFileName(attachment, inputStream, fileName);
                }
            }else{
                for (String key : maps.keySet()) {
                    MultipartFile multipartFile = maps.get(key);
                    InputStream inputStream = multipartFile.getInputStream();
                    String fileName = multipartFile.getOriginalFilename();
                    attachment = returnFileName(attachment, inputStream, fileName);
                }
            }
        }
        return attachment;
    }

    /**
     * 调单图片文件名保存拼接
     * @param attachment
     * @param inputStream
     * @param fileName
     * @return
     * @throws IOException
     */
    private String returnFileName(String attachment, InputStream inputStream, String fileName) throws IOException {
        String suffix = fileName.substring(fileName.lastIndexOf("."),fileName.length());
        if (".bmp,.jpg,.png,.jpeg".contains(suffix)) {//图片
            log.info("==提交回复用户上传了图片文件===");
            if (fileName.contains("_")) {
                fileName = fileName.substring(fileName.lastIndexOf("_") + 1,fileName.lastIndexOf("."));
            }
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileNameDate = dateFormat.format( now );
            Date date = new Date();
            Random random = new Random();
            String num = "";
            for(int i = 0; i < 6; i++){
                num += random.nextInt(4);
            }
            fileName = fileName + "_" + fileNameDate + num + suffix;
            ALiYunOssUtil.saveFile(Constants.ALIYUN_OSS_ATTCH_TUCKET,fileName, inputStream);
            log.info(ALiYunOssUtil.genUrl("agent-attch",fileName,new Date(date.getTime()+100000)));
            if ("".endsWith(attachment)) {
                attachment = attachment + fileName;
            } else {
                attachment = attachment + "," + fileName;
            }
        }else{//如果不是图片
            attachment = attachment + "," + fileName;
        }
        //如果 attachment 最后一个是逗号 ,去掉
        if (",".equals(attachment.substring(attachment.length()-1))) {
            attachment = attachment.substring(0, attachment.length()-1);
        }
        if (",".equals(attachment.substring(0,1))) {
            attachment = attachment.substring(1, attachment.length());
        }
        return attachment;
    }

    @OldSwaggerDeveloped
    @ApiOperation(value = "提交回复",notes = "提交回复,参数 order_no 调单号,必传,当前登录代理商编号 agent_no,必传")
    @GetMapping("/updateReplyStatus/{order_no}/{agent_no}")
    public ResponseBean updateReplyStatus(@PathVariable String order_no,
                                          @PathVariable String agent_no,
                                          @ApiIgnore @CurrentUser UserInfoBean userInfoBean) {
        log.info("提交回复 请求参数==> order_no:{},agent_no:{}",order_no,agent_no);
        try {
            if (StringUtils.isBlank(order_no) || StringUtils.isBlank(agent_no) || !agent_no.equals(userInfoBean.getAgentNo())){
                return ResponseBean.error("参数有误");
            }
            return ResponseBean.success(surveyOrderInfoService.updateReplyStatus(userInfoBean.getAgentNo(),order_no,userInfoBean));
        }catch (Exception e){
            log.error("提交回复异常",e);
            return ResponseBean.error("提交异常");
        }
    }
}