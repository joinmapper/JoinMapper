## 背景
>使用tk.mybatis，可以不用在Mapper文件中写xml配置，快速开发，但是，在关联查询的时候还需要些很多xml配置，我想开发一个关联查询的插件
## 开发思路
1. 分析tk.mybatis源代码
    >- tk.mybatis.mapper.common下是Mapper接口，接口方法使用@SelectProvider(type = BaseSelectProvider.class, method = "dynamicSQL")配置，
    >- tk.mybatis.mapper.provider下是sql的生成方法，
    >- Mapper接口中,根据@SelectProvider注解配置,在provider下查找对应的方法，获取sql
    >- 我们自定义的Mapper接口，框架通过代理模式实现org.apache.ibatis.binding.MapperProxy
    
2. 根据分析，整理开发思路
    >- 自定义Mapper接口，并在方法上配置@SelectProvider
    >- 自定义Provider
    >- 使用多个Example表达关联查询
    >- 通过tk.mybatis.spring.mapper.MapperScannerConfigurer对自定义Mapper注册，就会生效
3. 思路
    >- MappedStatement里有结果映射， MappedStatement在哪里创建？ http://blog.csdn.net/shikaiwencn/article/details/52485883  ，  http://www.iteye.com/news/32753
4. 面临的困难
    >- resultMap如何设置，哪里可以获取到当前请求的参数
    
    ```java
   
    ResultMapping{property='tableTwo', column='null', javaType=class TableTwo, jdbcType=null, nestedResultMapId='TableTwoMapper.BaseResultMap', nestedQueryId='null', notNullColumns=[], columnPrefix='null', flags=[], composites=[], resultSet='null', foreignColumn='null', lazy=false}
    ResultMapping{property='tableTwo', column='null', javaType=class TableTwo, jdbcType=null, nestedResultMapId='BaseMapperJoinResultMap', nestedQueryId='null', notNullColumns=null, columnPrefix='null', flags=[], composites=[], resultSet='null', foreignColumn='null', lazy=false}
    list结果映射配置：ResultMapping{property='tableTwoList', column='null', javaType=interface java.util.List, jdbcType=null, nestedResultMapId='TableOneMapper.mapper_resultMap[BaseResultMap]_collection[tableTwoList]', nestedQueryId='null', notNullColumns=[], columnPrefix='null', flags=[], composites=[], resultSet='null', foreignColumn='null', lazy=false}
    org.apache.ibatis.mapping.MappedStatement.resultMaps 查看resultMap结构
    org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleResultSet 192 , 321 , 862 , 934(查找nestedResultMapId变量) 设置resultMap
    
    org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleRowValues  resultMap.hasNestedResultMaps()=false, 应该让他等于true
    查看 org.apache.ibatis.mapping.ResultMap 96 变量hasNestedResultMaps 查找原因
    
    org.apache.ibatis.executor.resultset.DefaultResultSetHandler.getNestedResultMap 获取嵌套的resultMap
        Result Maps collection does not contain value for table_two.BaseMapperJoinResultMap
        	at org.apache.ibatis.session.Configuration$StrictMap.get(Configuration.java:888)  在Configuration中没有获取到嵌套的resultMap，需要pers.zhangkai.mybatis.JoinEntityTable.getResultMap方法中添加
        	
    到这里， resultMap初步设置成功，太费劲了 2018-3-6 16:09:08
 
 5. 版本测试
    > 
 
 fdsa
 
 ### gradle clean build uploadArchives
    ```