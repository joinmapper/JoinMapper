package com.github.joinmapper.mapperhelper;

import com.github.joinmapper.provider.JoinEntityTable;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JoinEntityHelper {
    /**
     * 实体类 => 表对象
     */
    private static final Map<Class<?>, JoinEntityTable> joinEntityTableMap = new ConcurrentHashMap<Class<?>, JoinEntityTable>();

    /**
     * 获取表对象
     *
     * @param entityClass
     * @return
     */
    public static JoinEntityTable getJoinEntityTable(Class<?> entityClass) {
        JoinEntityTable joinEntityTable = joinEntityTableMap.get(entityClass);
        if (joinEntityTable == null) {
            EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            if (entityTable == null) {
                throw new MapperException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
            }
            joinEntityTable = new JoinEntityTable(entityTable);
            joinEntityTableMap.put(entityClass, joinEntityTable);
        }
        return joinEntityTable;
    }

}
