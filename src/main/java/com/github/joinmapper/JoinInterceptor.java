

package com.github.joinmapper;

import com.github.joinmapper.entity.JoinExample;
import com.github.joinmapper.mapperhelper.JoinMapperTemplate;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * 关联查询拦截器,
 * 项目地址 : https://github.com/joinmapper/JoinMapper
 *
 * @author zhangkai
 * @version 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
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
