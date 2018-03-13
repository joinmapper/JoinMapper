package com.github.joinmapper.provider;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class JoinEntityTable {
    private EntityTable entityTable;
    private ResultMap joinResultMap;

    public JoinEntityTable(EntityTable entityTable) {
        this.entityTable = entityTable;
    }

    /**
     * 生成当前实体的resultMap对象
     *
     * @param configuration Configuration
     * @return result
     */
    public ResultMap getResultMap(Configuration configuration) {
        //joinResultMap保存组基本的map，然后根据关联映射的需求进行组装
        if (this.joinResultMap == null) {
            Set<EntityColumn> entityClassColumns = entityTable.getEntityClassColumns();
            if (entityClassColumns == null || entityClassColumns.size() == 0) {
                return null;
            }
            List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
            String tableName = entityTable.getName();
            for (EntityColumn entityColumn : entityClassColumns) {
                String column = entityColumn.getColumn();
                //去掉可能存在的分隔符
                Matcher matcher = EntityTable.DELIMITER.matcher(column);
                if (matcher.find()) {
                    column = matcher.group(1);
                }
                column = tableName + "_" + column;//字段加前缀tableName
                ResultMapping.Builder builder = new ResultMapping.Builder(configuration, entityColumn.getProperty(), column, entityColumn.getJavaType());
                if (entityColumn.getJdbcType() != null) {
                    builder.jdbcType(entityColumn.getJdbcType());
                }
                if (entityColumn.getTypeHandler() != null) {
                    try {
                        builder.typeHandler(entityTable.getInstance(entityColumn.getJavaType(), entityColumn.getTypeHandler()));
                    } catch (Exception e) {
                        throw new MapperException(e);
                    }
                }
                List<ResultFlag> flags = new ArrayList<ResultFlag>();
                if (entityColumn.isId()) {
                    flags.add(ResultFlag.ID);
                }
                builder.flags(flags);
                resultMappings.add(builder.build());
            }
            ResultMap.Builder builder = new ResultMap.Builder(configuration, entityTable.getName() + ".BaseMapperJoinResultMap", entityTable.getEntityClass(), resultMappings, true);
            this.joinResultMap = builder.build();
        }
        if (!configuration.hasResultMap(this.joinResultMap.getId())) {
            configuration.addResultMap(this.joinResultMap);// org.apache.ibatis.executor.resultset.DefaultResultSetHandler.getNestedResultMap 需要， 用于resultMap嵌套时搜索
        }
        return this.joinResultMap;
    }

    public EntityTable getEntityTable() {
        return entityTable;
    }
}
