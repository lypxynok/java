package org.xynok.tools.datasource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;

/**
 * com.zaxxer.hikari.HikariConfig hikari配置
 * 参考org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration中数据源工厂方法
 * 参考org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor.postProcessBeforeInitialization()
 * 处理@ConfigurationProperties注解
 */

@SuppressWarnings("unchecked")
@Slf4j
public class DataSourceFactory implements IDataSourceFactory{
    /** 环境变量 */
    protected Environment env;
    /**数据源工厂 */
    private static DataSourceFactory dataSourceFactory;
    private static Object LOCK=new Object();

    private DataSourceFactory(){

    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    /**构建工厂实例 */
    public static IDataSourceFactory build(){
        if(dataSourceFactory==null){
            synchronized(LOCK){
                if(dataSourceFactory==null){
                    dataSourceFactory=new DataSourceFactory();
                }
            }
        }
        return dataSourceFactory;
    }

    /**
     * 指定数据源配置绑定到数据源对象上
     * 动态设置注解@ConfigurationProperties{prefix=...}
     * 利用Proxy代理AnnotationInvocationHandler，修改注解内容
     * 利用SpringBoot Binder，实现配置到数据源的动态绑定
     * @param dataSource 数据源对象
     * @param method     数据源工厂方法
     * @param prefix     指定数据源配置前缀
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException 
     */
    protected void bindDataSourceConfiguration(DataSource dataSource, Method method, String prefix)
            throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        // 取DataSource工厂方法（就是创建DataSource实例的方法）上的注解@ConfigurationProperties
        ConfigurationProperties configAnnotation = method.getAnnotation(ConfigurationProperties.class);
        // 获取代理实例所持有的 InvocationHandler，Java注解由代理实现，代理处理对象是AnnotationInvocationHandler
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(configAnnotation);
        // 获取 AnnotationInvocationHandler 的 memberValues 字段
        Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
        // 打开访问权限
        memberValuesField.setAccessible(true);
        // 获取 memberValues (成员和值的映射表，目标注解的信息都在 memberValues 中)
        Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
        // 1)、动态获取 TargetAnnotation.targetAnnotationClassField 标注的注解属性值
        Object prefixValue = memberValues.get("prefix");
        log.info("修改前@ConfigurationProperties prefix:{}", prefixValue);
        // 2)、动态修改 TargetAnnotation.targetAnnotationClassField 标注的注解属性值
        memberValues.put("prefix", prefix);
        // 3)、再次获取修改后的 TargetAnnotation.targetAnnotationClassField 标注的注解属性值
        prefixValue = memberValues.get("prefix");
        log.info("修改后@ConfigurationProperties prefix:{}", prefixValue);
        // 绑定工厂返回类型DataSource类型
        ResolvableType bindType = ResolvableType.forMethodReturnType(method);
        // DataSource类型绑定注解
        Bindable<Object> bindTarget = Bindable.of(bindType).withAnnotations(new Annotation[] { configAnnotation });
        // 绑定具体对象
        bindTarget = bindTarget.withExistingValue(dataSource);
        // 绑定事件处理对象
        BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
        // 将特定的DataSource配置绑定到数据源上
        Binder.get(env).bind(configAnnotation.prefix(), bindTarget, handler);
    }

    /**
     * 创建数据源 
     * 如果没有指定type，默认使用HikariDataSource
     * 指定type，需设置factoryMethod属性，hikari的工厂方法为hikariDataSource
     * 
     * @param prefix 数据源配置前缀
     * @return
     */
    @Override
    public DataSource createDataSource(String prefix) throws Exception{
        DataSourceProperties properties = Binder.get(env).bind(prefix, DataSourceProperties.class).orElse(null);
        DataSource dataSource;
        Method method=getFactoryMethod("newDataSource");
        dataSource=newDataSource(properties);
        bindDataSourceConfiguration(dataSource, method, prefix);
        return dataSource;
    }

    /**
     * 获取数据源工厂方法对象 需在AbstractCreateDataSource或子类中实现工厂方法 工厂方法参数为DataSourceProperties
     * 工厂方法必须使用@ConfigurationProperties(prefix="")注解，用于后续动态注入配置参数
     * 
     * @param methodName 工厂方法名
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    protected Method getFactoryMethod(String methodName) throws NoSuchMethodException, SecurityException {
        Method method = this.getClass().getMethod(methodName, new Class[] { DataSourceProperties.class });
        return method;
    }

    /**
     * 通过数据源属性创建数据源
     * 
     * @param <T>        数据源具体实现类
     * @param properties 数据源属性
     * @param type       数据源实现类
     * @return 数据源实例
     */
    public static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }


    /**
     * 数据源工厂方法
     * 
     * @param properties 数据源属性
     * @return 
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource newDataSource(DataSourceProperties properties) {
        DataSource dataSource;
        //如果没有配置数据源实现类，使用HikariDataSource，否则使用配置的实现类
        if(properties.getType()==null){
            dataSource=createDataSource(properties, HikariDataSource.class);
        }else{
            dataSource=createDataSource(properties,properties.getType());
        }
        return dataSource;
    }


    /** 多数据源切换，获取实际的数据源 */
    public static DataSource getTargetDataSource(DataSource dataSource)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (dataSource instanceof AbstractRoutingDataSource) {
            Class<?> clz = dataSource.getClass();
            // 因为determineTargetDataSource在AbstractRoutingDataSource类中，需要回溯到这个类上面
            while (clz != AbstractRoutingDataSource.class && clz!=Object.class) {
                clz = clz.getSuperclass();
            }
            if(clz==Object.class){
                return dataSource;
            }
            Method method = clz.getDeclaredMethod("determineTargetDataSource");
            method.setAccessible(true);
            return (DataSource) method.invoke(dataSource);
        } else {
            return dataSource;
        }
    }
}