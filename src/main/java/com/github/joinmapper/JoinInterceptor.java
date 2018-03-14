

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
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class JoinInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1]; // 目标方法中的参数
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
        if (target instanceof Executor){ // 是否创建代理对象
            Object wrap = Plugin.wrap(target, this); // 创建代理, 才会执行intercept方法
            return wrap;
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
