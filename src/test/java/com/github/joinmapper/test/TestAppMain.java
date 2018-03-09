package com.github.joinmapper.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = "com.github.joinmapper.test.dao")
public class TestAppMain {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestAppMain.class);
        app.run(args);
    }
}
