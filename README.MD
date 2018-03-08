### Installation
   - SpringBoot
        - Config Interceptor
            ```java
           @Bean(name="sqlSessionFactory")
           public SqlSessionFactory getSqlSessionFactory(DataSource dataSource){
               SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
               bean.setDataSource(dataSource);
               bean.setTypeAliasesPackage("pers.zhangkai.mybatis.model");
               bean.setPlugins(new Interceptor[] {new JoinInterceptor()}); // 拦截器
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
       
        - Config Mapper
          ```java
          @Bean
              public MapperScannerConfigurer mapperScannerConfigurer() {
                  MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
                  mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
                  mapperScannerConfigurer.setBasePackage("pers.zhangkai.mybatis.dao");
          
                  //初始化扫描器的相关配置，这里我们要创建一个Mapper的父类
                  Properties properties = new Properties();
                  properties.setProperty("mappers", "tk.mybatis.mapper.common.Mapper,JoinMapper");
          
                  mapperScannerConfigurer.setProperties(properties);
          
          
                  return mapperScannerConfigurer;
              }