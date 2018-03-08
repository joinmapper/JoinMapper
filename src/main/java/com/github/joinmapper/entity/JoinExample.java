package com.github.joinmapper.entity;

import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

public class JoinExample extends Example {
    /**
     * 需要关联查询的Example
     */
    private List<Join> joinList = new ArrayList<>();
    public JoinExample(Class<?> entityClass) {
        super(entityClass);
    }

    public JoinExample(Class<?> entityClass, boolean exists) {
        super(entityClass, exists);
    }

    public JoinExample(Class<?> entityClass, boolean exists, boolean notNull) {
        super(entityClass, exists, notNull);
    }


    private void addJoin(Join join){
        this.joinList.add(join);
    }

    public Join leftJoin(JoinExample joinExample, ResultType resultType, String resultProperty,String onProperty1, String onProperty2){
        Join join = new Join(JoinType.LEFT, joinExample, resultType, resultProperty, onProperty1, onProperty2);
        addJoin(join);
        return join;
    }

    public Join rightJoin(JoinExample joinExample, ResultType resultType, String resultProperty,String onProperty1, String onProperty2){
        Join join = new Join(JoinType.RIGHT, joinExample, resultType, resultProperty, onProperty1, onProperty2);
        addJoin(join);
        return join;
    }

    public Join innerJoin(JoinExample joinExample, ResultType resultType, String resultProperty,String onProperty1, String onProperty2){
        Join join = new Join(JoinType.INNER, joinExample, resultType, resultProperty, onProperty1, onProperty2);
        addJoin(join);
        return join;
    }

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

    public enum JoinType{
        LEFT("LEFT JOIN"),RIGHT("RIGHT JOIN"),INNER("INNER JOIN");
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

    public static class JoinProperty {
        private String  property1;
        private String  property2;

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

        public Join on(String property1, String property2){
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

    public static enum ResultType {
        ONE,MANY
    }
}
