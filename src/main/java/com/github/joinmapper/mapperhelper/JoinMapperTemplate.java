package com.github.joinmapper.mapperhelper;

import com.github.joinmapper.entity.JoinExample;
import com.github.joinmapper.mapperhelper.JoinEntityHelper;
import com.github.joinmapper.provider.JoinEntityTable;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinMapperTemplate extends MapperTemplate {
    public JoinMapperTemplate(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 设置resultMap
     * @param ms
     * @param joinExample
     */
    public static void setJoinResultType(MappedStatement ms, JoinExample joinExample) {
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        ResultMap resultMap = getJoinResultMap(ms, joinExample);
        resultMaps.add(resultMap);
        MetaObject metaObject = SystemMetaObject.forObject(ms);
        metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
    }

    public static ResultMap getJoinResultMap(MappedStatement ms, JoinExample joinExample) {
        Class<?> entityClass = joinExample.getEntityClass();
        JoinEntityTable joinEntityTable = JoinEntityHelper.getJoinEntityTable(entityClass);
        ResultMap resultMap = joinEntityTable.getResultMap(ms.getConfiguration());
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        ArrayList<ResultMapping> resultMappingsNew = new ArrayList<>();
        resultMappingsNew.addAll(resultMappings);

        // 关联对象的映射
        for ( JoinExample.Join join:joinExample.getJoinList() ) {
            JoinExample joinTo = join.getJoinTo();
            JoinExample.ResultType resultType = join.getResultType();
            String resultProperty = join.getResultProperty();
            JoinEntityTable toEntityTable = JoinEntityHelper.getJoinEntityTable(joinTo.getEntityClass());
            ResultMap toTesultMap = toEntityTable.getResultMap(ms.getConfiguration());
            Class<?> javaType = joinTo.getEntityClass();
            if (join.getResultType()== JoinExample.ResultType.MANY){
                javaType = List.class; // 一对多结果映射
            }
            ResultMapping resultMapping = new ResultMapping.Builder(ms.getConfiguration(), resultProperty, null, javaType).nestedResultMapId(toTesultMap.getId()).build();
            resultMappingsNew.add(resultMapping);
            ResultMap joinToResultMap = getJoinResultMap(ms, joinTo); // 递归 ， 结束逻辑：joinExample.getJoinList()为空
        }

        // 反射注入
        MetaObject metaObject = SystemMetaObject.forObject(resultMap);
        metaObject.setValue("resultMappings", Collections.unmodifiableList(resultMappingsNew));
        metaObject.setValue("propertyResultMappings", Collections.unmodifiableList(resultMappingsNew));
        metaObject.setValue("hasNestedResultMaps", true);// org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleRowValues  resultMap.hasNestedResultMaps()=false, 应该让他等于true
        return resultMap;
    }
}
