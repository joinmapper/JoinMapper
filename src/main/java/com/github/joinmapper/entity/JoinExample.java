package com.github.joinmapper.entity;

import com.github.joinmapper.mapperhelper.JoinEntityHelper;
import com.github.joinmapper.provider.JoinEntityTable;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinExample extends Example {
    /**
     * 需要关联查询的Example
     */
    protected List<Join> joinList = new ArrayList<>();
    protected String joinOrderByClause;
    public JoinExample(Class<?> entityClass) {
        super(entityClass);
        initJoinOrderBy();
    }

    public JoinExample(Class<?> entityClass, boolean exists) {
        super(entityClass, exists);
        initJoinOrderBy();
    }

    public JoinExample(Class<?> entityClass, boolean exists, boolean notNull) {
        super(entityClass, exists, notNull);
        initJoinOrderBy();
    }

    //重写赋值OrderBy
    protected void initJoinOrderBy() {
        super.ORDERBY = new OrderBy(this, propertyMap);
    }

    public String getJoinOrderByClause() {
        return joinOrderByClause;
    }

    @Override
    public void setOrderByClause(String orderByClause) {
        if (StringUtil.isNotEmpty(orderByClause)) {
            if (orderByClause.contains(",")) {
                StringBuilder obc = new StringBuilder();
                String[] arr = orderByClause.split(",");
                for (String column : arr) {
                    obc.append(tableName + "." + column + ",");
                }
                super.setOrderByClause(obc.toString().substring(0, obc.length() - 1));
            } else {
                super.setOrderByClause(orderByClause);
            }
        }
    }

    public JoinOrderBy createJoinOrderBy(){
        return new JoinOrderBy(this);
    }

    protected String property(String property) {
        if (StringUtil.isEmpty(property) || StringUtil.isEmpty(property.trim())) {
            throw new MapperException("接收的property为空！");
        }
        property = property.trim();
        if (!propertyMap.containsKey(property)) {
            throw new MapperException("当前实体类不包含名为" + property + "的属性!");
        }
        return propertyMap.get(property).getColumn();
    }

    private void addJoin(Join join) {
        this.joinList.add(join);
    }

    /**
     * left join
     *
     * @param joinExample    要关联的JoinExample对象
     * @param resultType     关联结果类型（一对一，一对多）
     * @param resultProperty 查询结果映射到哪个属性
     * @param onProperty1    关联条件
     * @param onProperty2    关联条件
     * @return result 
     */
    public Join leftJoin(JoinExample joinExample, ResultType resultType, String resultProperty, String onProperty1, String onProperty2) {
        Join join = new Join(JoinType.LEFT, joinExample, resultType, resultProperty, onProperty1, onProperty2);
        addJoin(join);
        return join;
    }

    /**
     * right join
     *
     * @param joinExample    要关联的JoinExample对象
     * @param resultType     关联结果类型（一对一，一对多）
     * @param resultProperty 查询结果映射到哪个属性
     * @param onProperty1    关联条件
     * @param onProperty2    关联条件
     * @return Join
     */
    public Join rightJoin(JoinExample joinExample, ResultType resultType, String resultProperty, String onProperty1, String onProperty2) {
        Join join = new Join(JoinType.RIGHT, joinExample, resultType, resultProperty, onProperty1, onProperty2);
        addJoin(join);
        return join;
    }

    /**
     * inner join
     *
     * @param joinExample    要关联的JoinExample对象
     * @param resultType     关联结果类型（一对一，一对多）
     * @param resultProperty 查询结果映射到哪个属性
     * @param onProperty1    关联条件
     * @param onProperty2    关联条件
     * @return result
     */
    public Join innerJoin(JoinExample joinExample, ResultType resultType, String resultProperty, String onProperty1, String onProperty2) {
        Join join = new Join(JoinType.INNER, joinExample, resultType, resultProperty, onProperty1, onProperty2);
        addJoin(join);
        return join;
    }

    /**
     * 根据属性获取数据库字段
     *
     * @param property String
     * @return result
     */
    public String column(String property) {
        if (propertyMap.containsKey(property)) {
            return propertyMap.get(property).getColumn();
        } else if (exists) {
            throw new MapperException("当前实体类不包含名为" + property + "的属性!");
        } else {
            return null;
        }
    }

    public List<Join> getJoinList() {
        return joinList;
    }

    /**
     * 关联类型
     */
    public enum JoinType {
        LEFT("LEFT JOIN"), RIGHT("RIGHT JOIN"), INNER("INNER JOIN");
        private String Type;

        private JoinType(String type) {
            Type = type;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }
    }

    /**
     * 关联属性对
     */
    public static class JoinProperty {
        private String property1;
        private String property2;

        public JoinProperty(String property1, String property2) {
            this.property1 = property1;
            this.property2 = property2;
        }

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }

    /**
     * 关联对象
     */
    public static class Join {
        private JoinType joinType;
        private JoinExample joinTo;
        private ResultType resultType;
        private String resultProperty;
        private List<JoinProperty> joinPropertyList = new ArrayList<>();

        public Join(JoinType joinType, JoinExample joinTo) {
            this.joinType = joinType;
            this.joinTo = joinTo;
        }

        public Join(JoinType joinType, JoinExample joinTo, ResultType resultType, String resultProperty, String onProperty1, String onProperty2) {
            this.joinType = joinType;
            this.joinTo = joinTo;
            this.resultType = resultType;
            this.resultProperty = resultProperty;
            this.on(onProperty1, onProperty2);
        }

        public Join on(String property1, String property2) {
            JoinProperty joinProperty = new JoinProperty(property1, property2);
            joinPropertyList.add(joinProperty);
            return this;
        }

        public JoinType getJoinType() {
            return joinType;
        }

        public void setJoinType(JoinType joinType) {
            this.joinType = joinType;
        }

        public JoinExample getJoinTo() {
            return joinTo;
        }

        public void setJoinTo(JoinExample joinTo) {
            this.joinTo = joinTo;
        }

        public List<JoinProperty> getJoinPropertyList() {
            return joinPropertyList;
        }

        public void setJoinPropertyList(List<JoinProperty> joinPropertyList) {
            this.joinPropertyList = joinPropertyList;
        }

        public ResultType getResultType() {
            return resultType;
        }

        public void setResultType(ResultType resultType) {
            this.resultType = resultType;
        }

        public String getResultProperty() {
            return resultProperty;
        }

        public void setResultProperty(String resultProperty) {
            this.resultProperty = resultProperty;
        }
    }

    /**
     * 关联查询结果映射类型
     */
    public static enum ResultType {
        ONE, MANY
    }

    /**
     * 重写排序
     */
    public static class OrderBy extends tk.mybatis.mapper.entity.Example.OrderBy {
        protected JoinExample joinExample;
        protected boolean isProperty;

        public OrderBy(JoinExample joinExample, Map<String, EntityColumn> propertyMap) {
            super(joinExample, propertyMap);
            this.joinExample = joinExample;
        }

        /**
         * 给排序字段增加别名
         *
         * @param property String
         * @return result
         */
        @Override
        public Example.OrderBy orderBy(String property) {
            String column = property(property);
            if (column == null) {
                isProperty = false;
                return this;
            }
            String tableName = EntityHelper.getEntityTable(joinExample.getEntityClass()).getName();
            MetaObject exMetaObject = SystemMetaObject.forObject(joinExample);
            if (StringUtil.isNotEmpty(joinExample.getJoinOrderByClause())) {
                exMetaObject.setValue("joinOrderByClause", joinExample.getJoinOrderByClause() + "," + tableName + "." + column);
            } else {
                exMetaObject.setValue("joinOrderByClause", tableName + "." + column);
            }
            isProperty = true;
            return this;
        }

        @Override
        public OrderBy desc() {
            if (isProperty) {
                MetaObject exMetaObject = SystemMetaObject.forObject(joinExample);
                exMetaObject.setValue("joinOrderByClause", joinExample.getJoinOrderByClause() + " DESC");
                isProperty = false;
            }
            return this;
        }

        @Override
        public OrderBy asc() {
            if (isProperty) {
                MetaObject exMetaObject = SystemMetaObject.forObject(joinExample);
                exMetaObject.setValue("joinOrderByClause", joinExample.getJoinOrderByClause() + " ASC");
                isProperty = false;
            }
            return this;
        }

        protected String property(String property) {
            if (StringUtil.isEmpty(property) || StringUtil.isEmpty(property.trim())) {
                throw new MapperException("接收的property为空！");
            }
            property = property.trim();
            if (!propertyMap.containsKey(property)) {
                throw new MapperException("当前实体类不包含名为" + property + "的属性!");
            }
            return propertyMap.get(property).getColumn();
        }
    }

    public static class JoinOrderBy {
        protected JoinExample joinExample;
        protected boolean isProperty;

        public JoinOrderBy(JoinExample joinExample) {
            this.joinExample = joinExample;
        }

        protected String property(Class<?> entityClass, String property) {
            Map<String, EntityColumn> propertyMap = this.getPropertyMap(entityClass);
            if (StringUtil.isEmpty(property) || StringUtil.isEmpty(property.trim())) {
                throw new MapperException("接收的property为空！");
            }
            property = property.trim();
            if (!propertyMap.containsKey(property)) {
                throw new MapperException("当前实体类不包含名为" + property + "的属性!");
            }
            return propertyMap.get(property).getColumn();
        }

        protected Map<String, EntityColumn> getPropertyMap(Class<?> entityClass){
            JoinEntityTable joinEntityTable = JoinEntityHelper.getJoinEntityTable(entityClass);
            return joinEntityTable.getEntityTable().getPropertyMap();
        }

        public JoinOrderBy orderBy(String property) {
            return this.orderBy(joinExample.getEntityClass(), property);
        }

        public JoinOrderBy orderBy(Class<?> entityClass, String property) {
            String column = property(entityClass, property);
            if (column == null) {
                isProperty = false;
                return this;
            }
            String tableName = EntityHelper.getEntityTable(entityClass).getName();
            MetaObject exMetaObject = SystemMetaObject.forObject(joinExample);
            if (StringUtil.isNotEmpty(joinExample.getJoinOrderByClause())) {
                exMetaObject.setValue("joinOrderByClause", joinExample.getJoinOrderByClause() + "," + tableName + "." + column);
            } else {
                exMetaObject.setValue("joinOrderByClause", tableName + "." + column);
            }
            isProperty = true;
            return this;
        }

        public JoinOrderBy desc() {
            if (isProperty) {
                MetaObject exMetaObject = SystemMetaObject.forObject(joinExample);
                exMetaObject.setValue("joinOrderByClause", joinExample.getJoinOrderByClause() + " DESC");
                isProperty = false;
            }
            return this;
        }


        public JoinOrderBy asc() {
            if (isProperty) {
                MetaObject exMetaObject = SystemMetaObject.forObject(joinExample);
                exMetaObject.setValue("joinOrderByClause", joinExample.getJoinOrderByClause() + " ASC");
                isProperty = false;
            }
            return this;
        }
    }
}
