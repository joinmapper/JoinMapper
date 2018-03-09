package com.github.joinmapper.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.joinmapper.JoinInterceptor;
import com.github.joinmapper.model.TableOne;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@AutoConfigureBefore(MyBatisMapperScannerConfig.class)
public class DruidDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        String packageName = TableOne.class.getPackage().getName();
        bean.setTypeAliasesPackage(packageName);
        PageInterceptor pageInterceptor = new PageInterceptor();
//        helperDialect=mysql
//        reasonable=true
//        supportMethodsArguments=true
//        params=count=countSql
//        autoRuntimeDialect=true
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("params", "count=countSql");
        properties.setProperty("autoRuntimeDialect", "true");
        pageInterceptor.setProperties(properties);
        bean.setPlugins(new Interceptor[]{pageInterceptor, new JoinInterceptor()}); // 插件的执行顺序为倒叙,JoinInterceptor先执行
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 基于注解扫描Mapper，不需配置xml路径
            bean.setMapperLocations(resolver.getResources("classpath:mapping/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
