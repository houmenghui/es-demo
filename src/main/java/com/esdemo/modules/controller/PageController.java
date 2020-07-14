package com.esdemo.modules.controller;

import com.esdemo.frame.annotation.SignValidate;
import com.esdemo.frame.annotation.OldSwaggerDeveloped;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import com.esdemo.frame.utils.swagger.SwaggerNotes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-06-04 15:17
 */
@SignValidate(needSign = false)
@Slf4j
@RequestMapping("/page")
@Api(description = "跳转页面")
@Controller
public class PageController {

    @OldSwaggerDeveloped
    @ApiOperation(value = "商户预警详情跳转页面", notes = SwaggerNotes.JUMP_MERCHANT_EARLY_WARNING_DETAILS)
    @GetMapping(value = "/jump2MerchantEarlyWarningDetails")
    public String jump2MerchantEarlyWarningDetails(@RequestParam Map<String, String> searchBean, Model model, HttpServletRequest request) {
        String loginToken = WebUtils.getLoginToken(request);
        model.addAttribute("loginToken", loginToken);
        model.addAttribute("isLogin", StringUtils.isNotBlank(WebUtils.getLoginAgentNo(request)));
        model.addAttribute("agentNo", searchBean.get("agentNo"));
        model.addAttribute("queryScope", searchBean.get("queryScope"));
        model.addAttribute("warningId", searchBean.get("warningId"));
        model.addAttribute("contextPath", request.getContextPath());
        return "merchantEarlyWarningDetails";
    }
}
