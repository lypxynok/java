package org.xynok;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// @Import(CustomDataSourceBeanRegister.class)
@MapperScan("org.xynok.lab.mapper")
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class);
    }
}