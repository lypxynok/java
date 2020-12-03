package org.xynok.tools.datasource;

import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Maps;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * 多数据源选择Bean注册到SpringBoot 为了使注册生效,需要在SpringBoot启动类中使用
 * @Import({CustomDataSourceBeanRegister.class})导入配置,触发SpringBoot注册Bean
 */
@Slf4j
public class CustomDataSourceBeanRegister extends CustomDataSourceBeanBase implements ImportBeanDefinitionRegistrar{
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
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
            // 定义DataSource Bean
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            // Bean类型，DataSource路由类
            beanDefinition.setBeanClass(RoutingDataSource.class);
            beanDefinition.setSynthetic(true);
            //Bean的Set方法,通过属性名来标识,RoutingDataSource继承AbstractRoutingDataSource
            //AbstractRoutingDataSource中setDefaultTargetDataSource(Object defaultTargetDataSource)设置默认DataSource
            //AbstractRoutingDataSource中setTargetDataSources(Map<Object, Object> targetDataSources)设置候选DataSource
            MutablePropertyValues mpv = beanDefinition.getPropertyValues();
            Map<Object, DataSource> targetDataSources = Maps.newHashMap();
            if(defaultDataSource!=null){
                targetDataSources.put(defaultDataSourceName,defaultDataSource);
                mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
            }else{
                mpv.addPropertyValue("defaultTargetDataSource", customDataSourceMap.values().iterator().next());
            }
            targetDataSources.putAll(customDataSourceMap);
            mpv.addPropertyValue("targetDataSources", targetDataSources);
            registry.registerBeanDefinition("customDataSource", beanDefinition);
        }catch(Exception e){
           log.error("注册自定义数据源失败:{}",e.getMessage(),e);
           throw new IllegalAccessError(String.format("注册自定义数据源失败:%s",e));
        }
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env=env;
    }
}