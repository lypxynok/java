package org.xynok.lab.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xynok.tools.datasource.CustomDataSource;
import org.xynok.tools.datasource.DataSourceFactory;

/**
 * 自定义数据源配置
 * @author xynok
 */
@Configuration
public class CustomDataSourceConfig {
    @Bean
    @Primary
    JdbcTemplate defaultJdbcTemplate(DataSource dataSource) throws SQLException {
        try{
            return new JdbcTemplate(DataSourceFactory.getTargetDataSource(dataSource));
        }catch(Exception e){
            throw new SQLException(e.getCause());
        }
    }

    @CustomDataSource(name = "cus1")
    @Bean
    @Qualifier("cus1")
    JdbcTemplate cus1JdbcTemplate(DataSource dataSource) throws SQLException{
        try{
            return new JdbcTemplate(DataSourceFactory.getTargetDataSource(dataSource));
        }catch(Exception e){
            throw new SQLException(e.getCause());
        }
    }

    @CustomDataSource(name = "cus2")
    @Bean
    @Qualifier("cus2")
    JdbcTemplate cus2JdbcTemplate(DataSource dataSource) throws SQLException {
        try{
            return new JdbcTemplate(DataSourceFactory.getTargetDataSource(dataSource));
        }catch(Exception e){
            throw new SQLException(e.getCause());
        }
    }
}