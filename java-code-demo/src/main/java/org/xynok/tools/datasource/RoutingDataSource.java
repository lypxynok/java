package org.xynok.tools.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * SpringBoot Bean注册中使用的自定义多数据源选择类
 * 一方面实现了DataSource接口,一方面可以根据目标数据源名字选择对应DataSource的功能
 * @author xynok
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getTargetDataSourceName();
    }
}