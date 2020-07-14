package com.esdemo.frame.config;

import com.esdemo.frame.interceptor.BeforeLoginInterceptor;
import com.esdemo.frame.interceptor.ClientSignValidateInterceptor;
import com.esdemo.frame.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Created by Sandwich on 2018-08-13
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;      // 登陆校验
    @Resource
    private BeforeLoginInterceptor beforeLoginInterceptor; // 跨域访问
    @Resource
    private ClientSignValidateInterceptor signValidateInterceptor; // 接口签名


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 所有请求登陆之前拦截器: 用于配置跨域请求等
        InterceptorRegistration beforeLoginRegistry = registry.addInterceptor(beforeLoginInterceptor);
        beforeLoginRegistry.addPathPatterns("/**");

        // 不需要登陆校验的地址
        String[] excludePaths = {
                // 错误页面
                "/error",
                // 排除资源请求
                "*.css", "*.js", "*.png", "/dist/**",
                // 排除swagger相关路径
                "/swagger-ui.html", "/configuration/*", "/swagger-resources/**", "/v2/api-docs", "/webjars/**",
        };
        // 签名拦截请求
        InterceptorRegistration signRegistry = registry.addInterceptor(signValidateInterceptor);
        signRegistry.addPathPatterns("/**");
        signRegistry.excludePathPatterns(excludePaths);

        // 登陆拦截请求
        InterceptorRegistration authorizeRegistry = registry.addInterceptor(loginInterceptor);
        authorizeRegistry.addPathPatterns("/**");
        authorizeRegistry.excludePathPatterns(excludePaths);

    }
}
