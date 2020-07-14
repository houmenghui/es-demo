package com.esdemo.frame.config;

import com.esdemo.frame.db.DataSourceType;
import com.esdemo.frame.db.MyAbstractRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:56
 */
@Slf4j
@AutoConfigureAfter(DataSourceConfig.class)
@ConditionalOnClass({EnableTransactionManagement.class})
@Import({DataSourceConfig.class})
@Configuration
public class MyBatisConfig {

    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext ac) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(roundRobinDataSouceProxy(ac));
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        try {
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        } catch (IOException e) {
            log.error("异常{}", e);
        }
        return sqlSessionFactoryBean;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 设置事务，事务需要知道当前使用的是哪个数据源才能进行事务处理
     *
     * @param ac
     * @return
     */
    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager(ApplicationContext ac) {
        return new DataSourceTransactionManager(roundRobinDataSouceProxy(ac));
    }

    /**
     * 设置数据源路由，通过MyAbstractRoutingDataSource类中的determineCurrentLookupKey决定使用哪个数据源
     *
     * @param ac
     * @return
     */
    @Bean
    public DataSource roundRobinDataSouceProxy(ApplicationContext ac) {
        MyAbstractRoutingDataSource proxy = new MyAbstractRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource writeDataSource = (DataSource) ac.getBean("writeDataSource");
        DataSource readDataSource = (DataSource) ac.getBean("readDataSource");
        DataSource billDataSource = (DataSource) ac.getBean("billDataSource");
        targetDataSources.put(DataSourceType.WRITE.getType(), writeDataSource);
        targetDataSources.put(DataSourceType.BILL.getType(), billDataSource);
        proxy.setDefaultTargetDataSource(readDataSource);
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }

}
