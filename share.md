####  目录
>1. *Mybatis,Hibernate,tk.mybatis对比*
>2. <u >*Mybatis插件编写方法及原理(本次分享的目的)*</u>
>3. *Mybatis插件编写示例：mybatis-join-mapper*
>4. *总结*
### 1. Mybatis,Hibernate,tk.mybatis对比
- 共同点
    - 都是ORM框架（Object Relational Mapping）
- Mybatis优势
    - MyBatis可以进行更为细致的SQL优化，可以减少查询字段。
    - MyBatis容易掌握，而Hibernate门槛较高。
- Hibernate优势
    - Hibernate的DAO层开发比MyBatis简单，Mybatis需要维护SQL和结果映射。
    - Hibernate对对象的维护和缓存要比MyBatis好，对增删改查的对象的维护要方便。
    - Hibernate数据库移植性很好，MyBatis的数据库移植性不好，不同的数据库需要写不同SQL。
    - Hibernate有更好的二级缓存机制，可以使用第三方缓存。MyBatis本身提供的缓存机制不佳。
- tk.mybatis优势
    - 使用注解(@Table,@Id,@Column等)和Mapper接口，不需要在xml中编写sql，快速开发。
### 2. Mybatis插件编写方法及原理
- pagehelper和tk.mybatis是Mybatis的插件,通过分析这两个插件来学习
- pagehelper插件分析
    - 带着问题分析
        - pagehelper如何整合Mybatis？
        - com.github.pagehelper.PageHelper工作原理(分页参数的生命周期)？
    - 分析
        - 整合方法
          >实现并配置拦截器com.github.pagehelper.PageInterceptor(继承org.apache.ibatis.plugin.Interceptor),通过拦截器对我们自定义的方法进行拦截，拦截的目的：执行select count和limit语句及分页的sql方言处理
        - Interceptor接口详解
        ```Java
        @Intercepts(
            {
                /**
                 * @param type 拦截的目标，
                 *         包括：org.apache.ibatis.executor.Executor(方法执行),
                 *               org.apache.ibatis.executor.parameter.ParameterHandler(查询参数处理,了解一下org.apache.ibatis.scripting.defaults.DefaultParameterHandler83行),
                 *               org.apache.ibatis.executor.resultset.ResultSetHandler(查询结果处理,了解一下org.apache.ibatis.executor.resultset.DefaultResultSetHandler152行),
                 *               org.apache.ibatis.executor.statement.StatementHandler(statement处理，了解一下org.apache.ibatis.executor.statement.BaseStatementHandler.setStatementTimeout)
                 * @param method 拦截的方法
                 * @param args 方法的参数
                 *
                 */
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
            }
        )
        public class JoinInterceptor implements Interceptor {
        
            @Override
            public Object intercept(Invocation invocation) throws Throwable {
                Object[] args = invocation.getArgs();
                MappedStatement ms = (MappedStatement) args[0]; // 为什么args[0]获取到的就是MappedStatement，而且在转化类型之前不需要类型判断，因为注解@Intercepts.@Signature.args配置决定
                Object parameter = args[1]; // 自定义Mapper接口方法中的参数
                // 关联查询判断
                if (parameter != null && parameter instanceof JoinExample) {
                    // 设置resultMap
                    JoinExample joinExample = (JoinExample) parameter;
                    JoinMapperTemplate.setJoinResultType(ms, joinExample);
                }
                Object proceed = invocation.proceed();
                return proceed;
            }
        
            /**
             * 是否执行代理方法控制
             * @param target
             * @return
             */
            @Override
            public Object plugin(Object target) {
                if (target instanceof Executor){ // 拦截目标是否是Executor
                    Object wrap = Plugin.wrap(target, this); // 创建代理, 才会执行intercept方法
                    return wrap;
                }
                return target;
            }
        
            /**
             * 设置拦截器所需的参数
             * @param properties
             */
            @Override
            public void setProperties(Properties properties) {
            }
        
        }
        ````
        - com.github.pagehelper.PageHelper工作原理
            - PageHelper.startPage指定了分页参数，使用ThreadLocal存放分页参数
            ```Java
            public abstract class PageMethod {
                protected static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<Page>();
            
                /**
                 * 设置 Page 参数
                 *
                 * @param page
                 */
                protected static void setLocalPage(Page page) {
                    LOCAL_PAGE.set(page);
                }
            ```
            - PageInterceptor.intercept中从TreadLocal中获取分页参数
            - PageHelper的核心是ThreadLocal
                - ThreadLocal是当前线程的全局变量，与当前线程绑定，ThreadLocal中存放的对象对当前线程可见
                - Java框架代码中随处可见new ThreadLocal()，如何实现全局变量的功能, 分析ThreadLocal.get/set:
                ```Java
                public T get() {
                    Thread var1 = Thread.currentThread(); // 获取当前线程，是唯一的,native方法
                    ThreadLocal.ThreadLocalMap var2 = this.getMap(var1); // ThreadLocal.ThreadLocalMap是当前线程存放变量的容器
                    if (var2 != null) {
                        ThreadLocal.ThreadLocalMap.Entry var3 = var2.getEntry(this);
                        if (var3 != null) {
                            Object var4 = var3.value;
                            return var4;
                        }
                    }
            
                    return this.setInitialValue();
                    }
                ```
                > [Thread.currentThread()源码](http://hg.openjdk.java.net/jdk8u/jdk8u/jdk/file/5b86f66575b7/src/share/native/java/lang/Thread.c "Thread.currentThread()源码")

- tk.mybatis插件分析
    - 带着问题分析
        - 我们在编程时没有显示的实现Mapper接口，它是怎么工作的？
        - Mapper接口中的查询参数是如何被使用的，在哪里使用？
        - 我们没有在**Mapper.xml中配置resultMap，查询结果如何映射？
    - 分析
        - 使用org.apache.ibatis.binding.MapperProxy动态实现Mapper接口
        - 在Mapper接口中使用注解@SelectProvider,用来找到sql的解析方法
        ```java
        package tk.mybatis.mapper.common.base.select;
        import org.apache.ibatis.annotations.SelectProvider;
        import tk.mybatis.mapper.provider.base.BaseSelectProvider;
        /**
         * 通用Mapper接口,查询
         *
         * @param <T> 不能为空
         * @author liuzh
         */
        public interface SelectOneMapper<T> {
            /**
             * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
             * type = BaseSelectProvider表示：要在BaseSelectProvider类中查找selectOne方法
             * @param record
             * @return
             */
            @SelectProvider(type = BaseSelectProvider.class, method = "dynamicSQL")
            T selectOne(T record);
        }
        ```
        > BaseSelectProvider.selectOne返回sql或xml，让Mybatis解析
        ```Java
        public class BaseSelectProvider extends MapperTemplate {
            public BaseSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
                super(mapperClass, mapperHelper);
            }
            /**
             * 查询
             * @param ms
             * @return
             */
            public String selectOne(MappedStatement ms) {
                Class<?> entityClass = getEntityClass(ms);
                //修改返回值类型为实体类型
                setResultType(ms, entityClass);
                StringBuilder sql = new StringBuilder();
                sql.append(SqlHelper.selectAllColumns(entityClass));
                sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
                sql.append(SqlHelper.whereAllIfColumns(entityClass, isNotEmpty()));
                return sql.toString();
            }
            。。。。。。
        ```
        > SelectByExampleMapper
        ```Java
        public interface SelectByExampleMapper<T> {
            /**
             * 根据Example条件进行查询
             *
             * @param example
             * @return
             */
            @SelectProvider(type = ExampleProvider.class, method = "dynamicSQL")
            List<T> selectByExample(Object example);
        }
        ```
        > tk.mybatis.mapper.provider.ExampleProvider
        ```Java
        public String selectByExample(MappedStatement ms) {
            Class<?> entityClass = getEntityClass(ms);
            //将返回值修改为实体类型
            setResultType(ms, entityClass);
            StringBuilder sql = new StringBuilder("SELECT ");
            if (isCheckExampleEntityClass()) {
                sql.append(SqlHelper.exampleCheck(entityClass));
            }
            sql.append("<if test=\"distinct\">distinct</if>");
            //支持查询指定列
            sql.append(SqlHelper.exampleSelectColumns(entityClass));
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
            sql.append(SqlHelper.exampleWhereClause());
            sql.append(SqlHelper.exampleOrderBy(entityClass));
            sql.append(SqlHelper.exampleForUpdate());
            return sql.toString();
        }
        ```
        - tk.mybatis.mapper.mapperhelper.SqlHelper的工作原理
            >SqlHelper通过tk.mybatis.mapper.mapperhelper.EntityHelper和tk.mybatis.mapper.util.OGNL拼接sql或xml
        - tk.mybatis.mapper.mapperhelper.EntityHelper
            >EntityHelper存放实体和数据库字段等
        - tk.mybatis.mapper.util.OGNL
            ><if test="ajbh != null and ajbh != ''">是Mybatis中[OGNL表达式](http://blog.csdn.net/isea533/article/details/50061705 "OGNL表达式")
            >${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}"，也是Mybatis的OGNL表达式
            ```Java
            tk.mybatis.mapper.mapperhelper.SqlHelper.exampleSelectColumns
            public static String exampleSelectColumns(Class<?> entityClass) {
                StringBuilder sql = new StringBuilder();
                sql.append("<choose>");
                sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(_parameter)\">"); // hasSelectColumns是OGNL类中的方法，参数_paramter是Mapper接口看中的参数对象tk.mybatis.mapper.entity.Example
                sql.append("<foreach collection=\"_parameter.selectColumns\" item=\"selectColumn\" separator=\",\">"); // selectColumns是参数对象的属性
                。。。。。。
                sql.append("</otherwise>");
                sql.append("</choose>");
                return sql.toString();
            }
            ```
            ```Java
            tk.mybatis.mapper.mapperhelper.SqlHelper.exampleWhereClause
            /**
             * Example查询中的where结构，用于只有一个Example参数时
             */
            public static String exampleWhereClause() {
                return "<if test=\"_parameter != null\">" +
                    "<where>\n" +
                    "  <foreach collection=\"oredCriteria\" item=\"criteria\">\n" +
                    "    <if test=\"criteria.valid\">\n" +
                    "      ${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}" + // 可以不写_parameter,也表示Mapper入参对象的属性tk.mybatis.mapper.entity.Example.criteria
                    "      。。。。。。
                    "    </if>\n" +
                    "  </foreach>\n" +
                    "</where>" +
                    "</if>";
            }
            ```
        - tk.mybatis.mapper.mapperhelper.MapperTemplate.setResultType设置resultMap
        ```Java
        protected void setResultType(MappedStatement ms, Class<?> entityClass) {
            EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            List<ResultMap> resultMaps = new ArrayList<ResultMap>();
            resultMaps.add(entityTable.getResultMap(ms.getConfiguration())); // entityTable.getResultMap获取结果映射 
            MetaObject metaObject = SystemMetaObject.forObject(ms); // ibatis反射工具类
            metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps)); // 反射注入
        }
        ```
        - debug以上提到的相关类和方法，更形象的了解，运行方法com.github.joinmapper.test.junit.springboot.SpringBootBaseJoinMapperJunit.testExample
- Mybatis插件编写方法总结
### 3. Mybatis插件编写示例：mybatis-join-mapper
- 背景
    >我在开发案件管理任务时,数据库表较多,表中字段也很多,关联查询多张表时,编写resultMap很繁琐,而且xml很臃肿,我想:能不能模仿tk.mybatis实现一个关联查询的插件
- 需求分析
    - 如何用对象表达关联查询的需求,如tk.mybatis.mapper.entity.Example
    - 如何拼接关联查询的sql
    - 如何在Java代码中设置resultMap
- 实现步骤
    - 定义JoinExample类(继承Example)，存放关联查询的指令，满足用户的需求
    - 定义JoinMapper接口com.github.joinmapper.common.JoinMapper(JoinExample joinExample)
    - 动态实现Mapper接口com.github.joinmapper.provider.JoinProvider
    - 根据JoinProvider.selectJoin方法所需，合理使用Mybatis,ibatis,tk.mybatis的工具类，并扩展相关工具类
    ```Java
    com.github.joinmapper.mapperhelper.JoinEntityHelper，
    com.github.joinmapper.mapperhelper.JoinMapperTemplate，
    com.github.joinmapper.mapperhelper.JoinSqlHelper,
    com.github.joinmapper.util.JoinOGNL
    ```
    - 定义com.github.joinmapper.JoinInterceptor拦截器，设置resultMap
    - 在Mybatis中配置插件
    ```xml
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <property name="mapperLocations" value="classpath*:mapping/**/*Mapper.xml"/>
        <property name="plugins">
            <!--插件的执行顺序为倒叙,PageInterceptor应该最后执行，放在第一个 -->
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
    <bean id="mapperScannerConfigurer" class="tk.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com/github/joinmapper/test/dao"/>
        <property name="properties">
          <value>
              mappers=tk.mybatis.mapper.common.Mapper,com.github.joinmapper.common.JoinMapper
        
          </value>
        </property>
        <!--多数据源时，根据name装配-->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <property name="sqlSessionTemplateBeanName" value="sqlSession"/>
    </bean>
    ```
- 测试
    - 创建多个表，通过JoinMapper接口关联查询，并结合pagehelper进行分页
    - 对tk.mybatis的版本兼容测试
### 4.总结
- 我们一起了解了pagehelper，tk.mybatis 的基本原理，掌握Mybatis插件编写的方法，并在示例中运用
- 插件中使用到的技术或技巧，可以应用到日常开发中（按照使用频率排序）
    1. 在Mybatis中可以使用灵活的OGNL表达式，如：${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}
    2. 反射工具类org.apache.ibatis.reflection.SystemMetaObject
    3. ThreadLocal存放全局变量
    4. Mybatis插件编写方法，参考示例[mybatis-join-mapper](https://github.com/joinmapper/mybatis-join-mapper "mybatis-join-mapper")
    
