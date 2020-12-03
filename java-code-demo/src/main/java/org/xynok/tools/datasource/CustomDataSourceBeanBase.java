package org.xynok.tools.datasource;

import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Maps;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 自定义数据源Bean基类
 */
public class CustomDataSourceBeanBase implements EnvironmentAware{
    protected Environment env;
    /**自动注入环境变量 */
    @Override
    public void setEnvironment(Environment env) {
        this.env=env;
    }
    /** 加载默认数据源 */
    protected DataSource defaultDataSource() throws Exception {
        IDataSourceFactory dataSourceFactory = DataSourceFactory.build();
        String prefix = "spring.datasource";
        
        if (env.containsProperty(prefix+".url")) {
            DataSource dataSource = dataSourceFactory.createDataSource(prefix);
            return dataSource;
        } else {
            return null;
        }
    }

    /** 加载自定义数据源 */
    protected Map<String, DataSource> customDataSource() throws Exception {
        String prefix = "custom.datasource.names";
        Map<String, DataSource> map = Maps.newHashMap();
        if (env.containsProperty(prefix)) {
            String names = env.getProperty(prefix, "");
            if (names.length() == 0) {
                return null;
            }
            String[] nameArray = names.split(",");
            DataSource dataSource;
            IDataSourceFactory dataSourceFactory = DataSourceFactory.build();
            for (String name : nameArray) {
                prefix = "custom.datasource." + name;
                dataSource = dataSourceFactory.createDataSource(prefix);
                if (map.containsKey(name)) {
                    throw new IllegalArgumentException(String.format("自定义数据源名字(%s)重复", name));
                }
                map.put(name, dataSource);
            }
            return map;
        } else {
            return map;
        }
    }

}