package org.xynok.tools.datasource;

import java.util.Set;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

/**
 * 目标数据源上下文
 */
@Slf4j
public class DataSourceContextHolder {
    /* 当前目标数据源名字 */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    /** 有效自定义数据源名字列表 */
    private static Set<String> dataSourceNames = Sets.newConcurrentHashSet();

    /**
     * 设置目标数据源名字
     * 
     * @param name
     */
    public static void setTargetDataSourceName(String name) {
        contextHolder.set(name);
    }

    /** 取目标数据源名字 */
    public static String getTargetDataSourceName() {
        return contextHolder.get();
    }

    /** 清除目标数据源名字 */
    public static void clearTargetDataSourceName() {
        contextHolder.remove();
    }

    /**
     * 增加有效数据源名称
     * 
     * @param name
     */
    public static void addValidDataSourceName(String name) {
        if (!dataSourceNames.add(name)) {
            log.warn("Datasource name {} has added!",name);
        }
    }

    /**
     * 检查数据源名是否有效
     * 
     * @param name
     * @return
     */
    public static boolean isValidDataSourceName(String name) {
        return dataSourceNames.contains(name);
    }
}