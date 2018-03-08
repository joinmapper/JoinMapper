### Installation
   - SpringBoot
        - Config Interceptor
   ```java
       @Bean(name="sqlSessionFactory")
       public SqlSessionFactory getSqlSessionFactory(DataSource dataSource){
           SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
           bean.setDataSource(dataSource);
           bean.setTypeAliasesPackage("pers.zhangkai.mybatis.model");
           bean.setPlugins(new Interceptor[] {new JoinInterceptor()});
           ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
           try {
               //基于注解扫描Mapper，不需配置xml路径
               bean.setMapperLocations(resolver.getResources("classpath:mapping/*.xml"));
               return bean.getObject();
           } catch (Exception e) {
               e.printStackTrace();
               throw new RuntimeException(e);
           }
       }
   ```