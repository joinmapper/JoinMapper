####  目录
>1. *Mybatis,Hibernate,tk.mybatis对比*
>2. <u >*Mybatis插件编写方法及原理(本次分享的目的)*</u>
>3. *Mybatis插件编写示例：mybatis-join-mapper*
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
   >pagehelper和tk.mybatis是Mybatis的插件,通过分析这两个插件来学习
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
        - org.apache.ibatis.binding.MapperProxy动态实现Mapper接口
        - 在Mapper接口中使用注解@SelectProvider,用来指定sql的解析方法
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

- Mybatis插件编写方法总结
### 3. Mybatis插件编写示例：mybatis-join-mapper
- 背景
- 需求分析
- 实现步骤
    - 定义Mapper接口
    - 动态实现Mapper接口
    - 在Mybatis中配置插件
    - 测试
        - 创建多个表，通过JoinMapper接口关联查询，并结合pagehelper进行分页
        - 对tk.mybatis的版本兼容测试
- 这个插件的优点和缺点
    - 优点：数据库字段较多或者关联查询很多时，在xml中编写resultMap很繁琐，使用这个插件可以提高编程效率，在什么场景下使用能充分体现它的价值。
    - 缺点：
    
