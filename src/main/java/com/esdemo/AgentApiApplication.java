package com.esdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 13:49
 */
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement(order = 10)
@ServletComponentScan
public class AgentApiApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(AgentApiApplication.class, args);
    }
}
