package org.xynok.tools.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author:xynok
 * @since 2020/12/3
 */
public final class DataSourceHelper {
    /** 多数据源切换，获取实际的数据源 */
    public static DataSource getTargetDataSource(DataSource dataSource)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (dataSource instanceof AbstractRoutingDataSource) {
            Class clz = dataSource.getClass();
            // 因为determineTargetDataSource在AbstractRoutingDataSource类中，需要回溯到这个类上面
            while (clz != AbstractRoutingDataSource.class) {
                clz = clz.getSuperclass();
            }
            Method method = clz.getDeclaredMethod("determineTargetDataSource");
            method.setAccessible(true);
            return (DataSource) method.invoke(dataSource);
        } else {
            return dataSource;
        }
    }
}
