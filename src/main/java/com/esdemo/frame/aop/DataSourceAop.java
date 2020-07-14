package com.esdemo.frame.aop;

import com.esdemo.frame.annotation.DataSourceSwitch;
import com.esdemo.frame.db.DataSourceContextHolder;
import com.esdemo.frame.db.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@Component
@Slf4j
public class DataSourceAop implements PriorityOrdered {

    @Pointcut("@annotation(com.esdemo.frame.annotation.DataSourceSwitch)")
    public void dbReadWriteMethod() {
    }


    @Before("dbReadWriteMethod() && @annotation(dataSourceSwitch)")
    public void beforeWrite(JoinPoint point, DataSourceSwitch dataSourceSwitch) {
        //设置数据库为写数据
        DataSourceType dataSourceType = dataSourceSwitch.value();
        DataSourceContextHolder.config(dataSourceType);
        //调试代码，可注释
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        log.info("dataSource切换到：{} 开始执行: {}.{}() 方法", dataSourceType.getName(), className, methodName);
    }

    /**
     * 清楚数据源一方面为了避免内存泄漏，更重要的是避免对后续在本线程上执行的操作产生影响
     */
    @After("dbReadWriteMethod()")
    public void after() {
        DataSourceContextHolder.clear();
    }

    @Override
    public int getOrder() {
        /**
         * 值越小，越优先执行
         * 要优于事务的执行
         * 在启动类中加上了@EnableTransactionManagement(order = 10)
         */
        return 1;
    }
}