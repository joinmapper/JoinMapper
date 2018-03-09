### Installation
- Install in `SpringBoot`
    - Supported version
        >- mapper-spring-boot-starter ( More than 1.1.4 versions ) ,
        >- tk.mytatis ( More than 3.4.3 versions )
            <!-- mapper-spring-boot-starter 1.2.4之后还有问题 java.lang.NoSuchMethodError: org.springframework.boot.SpringApplication.<init>([Ljava/lang/Object;)V -->
    - Config Interceptor ([link text](https://www.google.com "View the details")) 
        ```java
        @Bean(name = "sqlSessionFactory")
        public SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            String packageName = TableOne.class.getPackage().getName();
            bean.setTypeAliasesPackage(packageName);
            PageInterceptor pageInterceptor = new PageInterceptor();
            Properties properties = new Properties();
            properties.setProperty("helperDialect", "mysql");
            properties.setProperty("reasonable", "true");
            properties.setProperty("supportMethodsArguments", "true");
            properties.setProperty("params", "count=countSql");
            properties.setProperty("autoRuntimeDialect", "true");
            pageInterceptor.setProperties(properties);
            bean.setPlugins(new Interceptor[]{pageInterceptor, new JoinInterceptor()}); // 插件的执行顺序为倒叙,让JoinInterceptor先执行
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            bean.setMapperLocations(resolver.getResources("classpath:mapping/*.xml"));
            return bean.getObject();
        }
        ```
    
    - Config Mapper (View the details)
        ```java
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


- Install in `Spring Framework`
    - Supported version
        >- tk.mytatis ( More than 3.4.3 versions )
    - Configuring Interceptor (View the details)
        ```xml
        <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
            <property name="dataSource" ref="dataSource"/>
            <property name="configLocation" value="classpath:mybatis-config.xml"/>
            <property name="mapperLocations" value="classpath*:mapping/**/*Mapper.xml"/>
            <property name="plugins">
                <array>
                    <bean class="com.github.pagehelper.PageInterceptor">
                        <property name="properties">
                            <value>
                                helperDialect=mysql
                                reasonable=true
                                supportMethodsArguments=true
                                params=count=countSql
                                autoRuntimeDialect=true
                            </value>
                        </property>
                    </bean>
                    <bean class="com.github.joinmapper.JoinInterceptor">
                    </bean>
                </array>
            </property>
        </bean>
    
    - Configuring Mapper (View the details)
        ```xml
        <bean id="mapperScannerConfigurer" class="tk.mybatis.spring.mapper.MapperScannerConfigurer">
            <property name="basePackage" value="com/github/joinmapper/dao"/>
            <property name="properties">
                <value>
                    mappers=tk.mybatis.mapper.common.Mapper,JoinMapper
    
                </value>
            </property>
            <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
            <property name="sqlSessionTemplateBeanName" value="sqlSession"/>
        </bean>
  
### Use in code
- Mapper
```
public interface TableOneMapper extends Mapper<TableOne>, JoinMapper<TableOne> {
    int getCount();

    List<TableOne> selectJoin_();
}
```
- select join
```java
JoinExample tableOneEx = new JoinExample(TableOne.class);
tableOneEx.orderBy("id").orderBy("code").desc().orderBy("code").asc();
//tableOneEx.selectProperties("id");
Example.Criteria criteria = tableOneEx.createCriteria();
criteria.andEqualTo("id", "table_one_1");

JoinExample tableTwoEx = new JoinExample(TableTwo.class);
JoinExample tableThreeEx = new JoinExample(TableThree.class);
JoinExample tableFourEx = new JoinExample(TableFour.class);

//关联查询设置
tableOneEx.leftJoin(tableTwoEx, JoinExample.ResultType.ONE, "tableTwo", "id", "id");
tableTwoEx.leftJoin(tableThreeEx, JoinExample.ResultType.ONE, "tableThree", "id", "id");
tableThreeEx.leftJoin(tableFourEx, JoinExample.ResultType.MANY, "tableFourList", "id", "id");

List<TableOne> tableOneList = tableOneMapper.selectJoin(tableOneEx);
```

- select join, page
```java
public PageInfo<TableOne> getPage() {
    Page<TableOne> page = PageHelper.startPage(1, 10);
    JoinExample tableOneEx = new JoinExample(TableOne.class);
    tableOneEx.orderBy("id").orderBy("code").desc().orderBy("code").asc();
    //tableOneEx.selectProperties("id");
    Example.Criteria criteria = tableOneEx.createCriteria();
    criteria.andEqualTo("id", "table_one_1");

    JoinExample tableTwoEx = new JoinExample(TableTwo.class);

    JoinExample tableThreeEx = new JoinExample(TableThree.class);

    JoinExample tableFourEx = new JoinExample(TableFour.class);

    //关联查询设置
    tableOneEx.leftJoin(tableTwoEx, JoinExample.ResultType.ONE, "tableTwo", "id", "id");
    tableTwoEx.leftJoin(tableThreeEx, JoinExample.ResultType.ONE, "tableThree", "id", "id");
    tableThreeEx.leftJoin(tableFourEx, JoinExample.ResultType.MANY, "tableFourList", "id", "id");

    List<TableOne> tableOneList = tableOneMapper.selectJoin(tableOneEx);
    return page.toPageInfo();
}
```