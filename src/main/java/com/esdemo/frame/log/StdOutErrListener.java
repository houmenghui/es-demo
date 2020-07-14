package com.esdemo.frame.log;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by lzj on 2019/9/12.
 */
@WebListener
public class StdOutErrListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        StdOutErrRedirect.redirectSystemOutAndErrToLog();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
