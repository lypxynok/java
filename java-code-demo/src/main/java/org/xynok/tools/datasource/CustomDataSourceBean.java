package org.xynok.tools.datasource;

import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Maps;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * CustomDataSource Bean 通过@Configuration和@Bean的方式来实现
 * 包引用方式的类，通过@Import({CustomDataSourceBean.class})注入Bean
 * @author xynok
 */
@Configuration
@Slf4j
public class CustomDataSourceBean extends CustomDataSourceBeanBase {
    @Bean
    public DataSource dataSource(){
        try {
            log.info("初始化customDataSourceBean");
            DataSourceFactory.build().setEnvironment(env);
            DataSource defaultDataSource = defaultDataSource();
            String defaultDataSourceName = "dataSource";
            Map<String, DataSource> customDataSourceMap = customDataSource();
            if (defaultDataSource == null && customDataSourceMap.size() == 0) {
                throw new IllegalAccessError("没有定义任何数据源");
            }
            // 增加有效数据源名称
            DataSourceContextHolder.addValidDataSourceName(defaultDataSourceName);
            for (String key : customDataSourceMap.keySet()) {
                DataSourceContextHolder.addValidDataSourceName(key);
            }
            RoutingDataSource dataSource=new RoutingDataSource();
            
            Map<Object, Object> targetDataSources = Maps.newHashMap();
            if(defaultDataSource!=null){
                targetDataSources.put(defaultDataSourceName,defaultDataSource);
                dataSource.setDefaultTargetDataSource(defaultDataSource);
            }else{
                dataSource.setDefaultTargetDataSource(customDataSourceMap.values().iterator().next());
            }
            targetDataSources.putAll(customDataSourceMap);
            dataSource.setTargetDataSources(targetDataSources);
            log.info("初始化customDataSourceBean完毕");
            return dataSource;
        }catch(Exception e){
           log.error("创建自定义数据源Bean失败:{}",e.getMessage(),e);
           throw new IllegalAccessError(String.format("创建自定义数据源Bean失败:%s",e));
        }
    }
}