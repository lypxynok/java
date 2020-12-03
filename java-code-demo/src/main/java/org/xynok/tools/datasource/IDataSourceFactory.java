package org.xynok.tools.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.core.env.Environment;

/**
 * DataSource创建接口
 * 各类DataSource的具体创建
 * DataSourceAutoConfiguration 
abstract class org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration$Hikari  各类数据源创建
DataSourceProperties

     断点postProcessBeforeInitialization，观察bean和beanName，进行调试，完成数据源属性注入
factory method:
com.zaxxer.hikari.HikariDataSource org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration$Hikari.dataSource(org.springframework.boot.autoconfigure.jdbc.DataSourceProperties)

	private static ConfigurationPropertiesBean create(String name, Object instance, Class<?> type, Method factory) {
		ConfigurationProperties annotation = findAnnotation(instance, type, factory, ConfigurationProperties.class);
		if (annotation == null) {
			return null;
		}
		Validated validated = findAnnotation(instance, type, factory, Validated.class);
		Annotation[] annotations = (validated != null) ? new Annotation[] { annotation, validated }
				: new Annotation[] { annotation };
		ResolvableType bindType = (factory != null) ? ResolvableType.forMethodReturnType(factory)
				: ResolvableType.forClass(type);//com.zaxxer.hikari.HikariDataSource
		Bindable<Object> bindTarget = Bindable.of(bindType).withAnnotations(annotations);
		if (instance != null) {
			bindTarget = bindTarget.withExistingValue(instance);
		}
		return new ConfigurationPropertiesBean(name, instance, annotation, bindTarget);
	}



 */
public interface IDataSourceFactory {
    static final String HIKARI_TYPE="com.zaxxer.hikari.HikariDataSource";
    static final String TOMCAT_TYPE="org.apache.tomcat.jdbc.pool.DataSource";
    static final String DBCP_TYPE="org.apache.commons.dbcp.BasicDataSource";
    static final String DBCP2_TYPE="org.apache.commons.dbcp2.BasicDataSource";
    /**
     * 创建数据源
     * 如果没有指定type，默认使用HikariDataSource
     * 指定type，需设置factoryMethod属性，hikari的工厂方法为hikariDataSource
     * @param prefix  数据源配置前缀
     * @return
     * @throws SQLException
     */
	DataSource createDataSource(String prefix) throws Exception;
	/**设置环境变量 */
	void setEnvironment(Environment env);
}