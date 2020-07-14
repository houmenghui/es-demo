package com.esdemo.frame.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * spring在开始进行数据库操作时会通过这个方法来决定使用哪个数据库，
 * 因此我们在这里调用上面DataSourceContextHolder类的getJdbcType()方法获取当前操作类别,
 * 如果有多个读库，可通过轮询的方式进行读库的负载均衡
 */
public class MyAbstractRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String typeKey = DataSourceContextHolder.getJdbcType();
        if (DataSourceType.READ.getType().equals(typeKey) || typeKey == null) {
            return DataSourceType.READ.getType();
        }
        return typeKey;
    }
}