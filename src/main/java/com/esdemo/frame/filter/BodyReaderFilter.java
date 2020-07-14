package com.esdemo.frame.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title：agentApi2
 * @Description：由于filter在interceptor之前执行，因此通过filter进行实现，解决请求体中的流只能读取一次的问题。 创建filer，在filter中对request对象用包装后的request替换。
 * @Author：zhangly
 * @Date：2019/5/13 14:36
 * @Version：1.0
 */
@WebFilter(filterName = "bodyReaderFilter", urlPatterns = "/*")
@Order(-1)
@Slf4j
public class BodyReaderFilter implements Filter {
    private static final List<String> exceptUrls = new ArrayList<>();
    static {
        // 正则表达式匹配
        exceptUrls.add("/merchantInfo/insertMerchantInfo");
        exceptUrls.add("/merchantInfo/updateMerchantInfo/.*");
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            for (String reg : exceptUrls) {
                if (httpRequest.getServletPath().matches(reg)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
            //只有post请求和非上传文件请求才进行包装
            String methodType = httpRequest.getMethod();
            boolean multipartRequest = httpRequest instanceof MultipartRequest;
            boolean isProblem = httpRequest.getRequestURL().indexOf("problem") > 0;
            if (RequestMethod.POST.toString().equalsIgnoreCase(methodType) && !multipartRequest && !isProblem) {
                log.info("访问{}请求request重新包装......", httpRequest.getRequestURL());
                requestWrapper = new BodyReaderHttpServletRequestWrapper(httpRequest);
            }
        }
        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }

    }

    @Override
    public void destroy() {
    }
}