

package com.github.joinmapper;

import com.github.joinmapper.entity.JoinExample;
import com.github.joinmapper.mapperhelper.JoinMapperTemplate;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * 关联查询拦截器<br/>
 * 项目地址 : https://github.com/joinmapper/JoinMapper
 *
 * @author zhangkai
 * @version 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class,

                        Object.class,

                        RowBounds.class,

                        ResultHandler.class}),
        }
)
public class JoinInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        // 关联查询判断
        if ( parameter != null && parameter instanceof JoinExample) {
            // 设置resultMap
            JoinExample joinExample = (JoinExample) parameter;
            JoinMapperTemplate.setJoinResultType(ms,joinExample);
        }
        Object proceed = invocation.proceed();
        return proceed;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
