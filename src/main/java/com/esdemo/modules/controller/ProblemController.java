package com.esdemo.modules.controller;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.annotation.CurrentUser;
import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.bean.ResponseBean;
import com.esdemo.frame.enums.ComplainterEnum;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.swagger.SettingSwaggerNotes;
import com.esdemo.modules.bean.UserInfoBean;
import com.esdemo.modules.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Slf4j
@RequestMapping("/problem")
@Api(description = "问题建议模块")
@RestController
@SignValidate(needSign = false)
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @ApiOperation(value = "问题建议-获取问题类型", notes = SettingSwaggerNotes.GET_PROBLEM_TYPE_LIST)
    @PostMapping("/getProblemTypeList")
    @OldSwaggerDeveloped
    public ResponseBean getProblemTypeList() {

        try {
            Map<String, Object> res = new HashMap<>();

            List<Map<String, Object>> problemTypeList = problemService.getAllProblemType();

            res.put("problemTypeList", problemTypeList);
            res.put("complainterList", ComplainterEnum.getAllComplainterToMap());
            return ResponseBean.success(res);

        } catch (Exception e) {
            log.error("问题建议-获取问题类型异常{}", e);
            return ResponseBean.error("问题建议-获取问题类型失败，请稍候再试");
        }
    }

    @ApiOperation(value = "问题建议-提交意见反馈", notes = SettingSwaggerNotes.SUBMIT_FEEDBACK)
    @PostMapping("/submitFeedback")
    @OldSwaggerDeveloped
    public ResponseBean submitFeedback(@ApiIgnore @CurrentUser UserInfoBean userInfoBean,
                                       HttpServletRequest request) {

        try {
            String userId = userInfoBean.getUserId();
            String problemType = request.getParameter("problemType");
            String complainterType = request.getParameter("complainterType");
            String content = request.getParameter("content");
            String mobileNo = request.getParameter("mobileNo");

            //获取appNo
            AppDeviceInfo appDeviceInfo = WebUtils.getAppDeviceInfo(request);
            log.info("获取获取设备公共参数：{}", JSONUtil.toJsonStr(appDeviceInfo));
            if(null == appDeviceInfo){
                return ResponseBean.error("请求不合法");
            }
            String appNo = appDeviceInfo.getAppNo();
            if(StringUtils.isBlank(appNo)){
                return ResponseBean.error("请求不合法");
            }

            if (StringUtils.isBlank(userId, problemType, content, mobileNo)) {
                return ResponseBean.error("必要参数不能有空");
            }
            userId = userId.replaceAll("\"", "");
            problemType = problemType.replaceAll("\"", "");
            content = content.replaceAll("\"", "");
            mobileNo = mobileNo.replaceAll("\"", "");

            if (StringUtils.isNotBlank(complainterType)) {
                complainterType = complainterType.replaceAll("\"", "");
            }

            Map bodyParams = new HashMap<>();
            bodyParams.put("userId", userId);
            bodyParams.put("problemType", problemType);
            bodyParams.put("complainterType", complainterType);
            bodyParams.put("content", content);
            bodyParams.put("mobileNo", mobileNo);
            bodyParams.put("userType", "1");
            bodyParams.put("appNo", appNo);
            //获取上传文件的文件名称
            Map<String, Object> uploadFileRes = WebUtils.uplodFiles(request);
            if (!CollectionUtils.isEmpty(uploadFileRes)) {
                boolean uploadStatus = (boolean) uploadFileRes.get("status");
                if (uploadStatus) {
                    List<String> fileNameList = (List<String>) uploadFileRes.get("str");
                    if (!CollectionUtils.isEmpty(fileNameList)) {
                        String attachment = String.join(",", fileNameList);
                        bodyParams.put("attachment", attachment);
                    }
                }
            }
            //新增问题记录
            int count = problemService.insertProblemInfo(bodyParams);
            if (count < 1) {
                return ResponseBean.error("提交问题反馈信息失败");
            }
            return ResponseBean.success();

        } catch (Exception e) {
            log.error("问题建议-提交意见反馈异常{}", e);
            return ResponseBean.error("提交问题反馈信息失败，请稍候再试");
        }
    }
}