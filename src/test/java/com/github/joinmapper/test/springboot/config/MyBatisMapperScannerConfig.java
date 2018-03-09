package com.github.joinmapper.test.springboot.config;

import com.github.joinmapper.common.JoinMapper;
import com.github.joinmapper.test.dao.TableOneMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

@Configuration
@AutoConfigureAfter(DruidDataSourceConfig.class)
public class MyBatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        String packageName = TableOneMapper.class.getPackage().getName();
        mapperScannerConfigurer.setBasePackage(packageName);
        Properties properties = new Properties();
        properties.setProperty("mappers", Mapper.class.getTypeName() + "," + JoinMapper.class.getTypeName()); // mappers
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }

    public static void main(String[] args) {
        /*MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("pers.zhangkai.mybatis.dao");

        //初始化扫描器的相关配置，这里我们要创建一个Mapper的父类
        Properties properties = new Properties();
        properties.setProperty("mappers", "tk.mybatis.mapper.common.Mapper,JoinMapper");

        mapperScannerConfigurer.setProperties(properties);
        System.out.println(JSONObject.toJSON(mapperScannerConfigurer.getMapperHelper()));*/

        System.out.println(JoinMapper.class.getTypeName());
    }
}
