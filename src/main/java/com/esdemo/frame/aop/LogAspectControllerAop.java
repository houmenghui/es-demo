package com.esdemo.frame.aop;

import cn.hutool.json.JSONUtil;
import com.esdemo.frame.bean.AppDeviceInfo;
import com.esdemo.frame.filter.BodyReaderHttpServletRequestWrapper;
import com.esdemo.frame.utils.StringUtils;
import com.esdemo.frame.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Title：agentApi2
 * @Description：接口访问日志信息切面
 * @Author：zhangly
 * @Date：2019/5/13 16:18
 * @Version：1.0
 */
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@Component
@Slf4j
public class LogAspectControllerAop {


    @Pointcut("execution(public * com.esdemo.modules.controller.*.*(..))")
    private void controllerAspect() {
    }

    // 请求method前打印内容
    @Before(value = "controllerAspect()")
    public void methodBefore(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        try {
            //请求头公共参数
            AppDeviceInfo appDeviceInfo = WebUtils.getAppDeviceInfo(request);
            //请求param参数
            Map<String, String[]> paramMap = request.getParameterMap();
            String ip = WebUtils.getRealIp(request);

            StringBuilder sb = new StringBuilder();
            sb.append("reqIp"+ip);
            sb.append("\n请求地址：" + request.getRequestURL().toString());
            sb.append("\n请求方式：" + request.getMethod());
            sb.append("\n请求方法：" + joinPoint.getSignature());
            sb.append("\n请求头公共参数：" + JSONUtil.toJsonStr(appDeviceInfo));
            sb.append("\n请求param参数：" + JSONUtil.toJsonStr(paramMap) + "\n");
            //请求body参数
            if (request instanceof BodyReaderHttpServletRequestWrapper) {
                Map<String, String> bodyParamsMap = WebUtils.getReqBodyInfo(request);
                sb.append("请求body参数：" + bodyParamsMap.toString() + "\n");
            }
            log.info(sb.toString());

        } catch (Exception e) {
            log.error("###LogAspectControllerAop.class methodBefore() ### ERROR：{}", e);
        }
    }

    // 在方法执行完结后打印返回内容
    @AfterReturning(returning = "res", pointcut = "controllerAspect()")
    public void methodAfterReturing(Object res) {
        if (res != null) {
            try {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = requestAttributes.getRequest();
                String requestUrl = request.getRequestURL().toString();
                //getActCodePosteraliImgBase64该接口返回图片Base64，字符串太大
                if(StringUtils.isNotBlank(requestUrl) && !requestUrl.contains("getActCodePosteraliImgBase64")){
                    StringBuilder sb = new StringBuilder();
                    sb.append("\n请求地址：" + request.getRequestURL().toString());
                    sb.append("\n请求方法：" + request.getMethod());
                    sb.append("\n响应内容：" + JSONUtil.toJsonStr(res) + "\n");

                    log.info(sb.toString());
                }

            } catch (Exception e) {
                log.error("###LogAspectControllerAop.class methodAfterReturing() ### ERROR：{}", e);
            }
        }
    }
}
