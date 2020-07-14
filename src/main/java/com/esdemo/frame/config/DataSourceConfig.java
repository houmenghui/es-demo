package com.esdemo.frame.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 由于要进行读写分离，不能再用springboot的默认配置，我们需要手动来进行配置。
 * 首先生成数据源，使用@ConfigurProperties自动生成数据源
 *
 * @author kco1989
 * @email kco1989@qq.com
 * @date 2019-05-08 15:52
 */
@SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    /**
     * 写数据源
     *
     * @Primary 标志这个 Bean 如果在多个同类 Bean 候选时，该 Bean 优先被考虑。
     * 多数据源配置的时候注意，必须要有一个主数据源，用 @Primary 标志该 Bean
     */
    @Primary
    @Bean(name = "writeDataSource", destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties("spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    /**
     * 读数据源
     * 有多少个读库就要设置多少个读数据源
     */
    @Bean(name = "readDataSource", destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    /**
     * bill数据源
     */
    @Bean(name = "billDataSource", destroyMethod = "close", initMethod = "init")
    @ConfigurationProperties(prefix = "spring.datasource.bill")
    public DataSource billDataSource() {
        return DataSourceBuilder.create().type(dataSourceType).build();
    }
}
