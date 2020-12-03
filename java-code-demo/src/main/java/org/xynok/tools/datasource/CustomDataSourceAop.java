package org.xynok.tools.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 目标类切面定义
 */
@Component
@Aspect
// 保证该AOP在@Transactional之前执行
@Order(-1)
@Slf4j
public class CustomDataSourceAop {

    /**
     * 切换目标数据源
     * 在进入方法之前,更改目标数据源
     * @param point 切点,即用@TargetDataSource注解的目标方法
     * @param ds 目标方法上的注解对象
     */
    @Before("@annotation(ds)")
    public void changeTargetDataSource(JoinPoint point, CustomDataSource ds) throws Throwable {
        String dsId = ds.name();
        if (!DataSourceContextHolder.isValidDataSourceName(dsId)) {
            log.error("数据源({})不存在，使用默认数据源 > {}", ds.name(), point.getSignature());
        } else {
            log.info("Use TargetDataSource : {} > {}", ds.name(), point.getSignature());
            DataSourceContextHolder.setTargetDataSourceName(ds.name());
        }
    }

    /**
     * 清除目标数据源
     * 方法执行后,清除本次切换的目标数据源
     * @param point 切点,即用@TargetDataSource注解的目标方法
     * @param ds 目标方法上的注解对象
     */
    @After("@annotation(ds)")
    public void restoreTargetDataSource(JoinPoint point, CustomDataSource ds) {
        log.info("Clear TargetDataSource : {} > {}", ds.name(), point.getSignature());
        DataSourceContextHolder.clearTargetDataSourceName();
    }
}