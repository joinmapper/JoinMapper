package com.github.joinmapper.util;

import com.github.joinmapper.entity.JoinExample;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.util.List;
import java.util.Set;

/**
 * @author zhangkai02
 */
public abstract class JoinOGNL {

    /**
     * 拼接join语句
     * @param joinFrom
     * @return
     */
    public static String join(JoinExample joinFrom) {
        if (joinFrom == null) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        buildJoinSql(joinFrom, sql); // 递归查找拼接join语句
        return sql.toString();
    }

    /**
     * 递归查找拼接join语句
     * @param joinExample
     * @param sql
     */
    private static void buildJoinSql(JoinExample joinExample, StringBuilder sql) {
        List<JoinExample.Join> joinList = joinExample.getJoinList();
        //递归结束语句
        if (joinList == null || joinList.isEmpty()) {
            return;
        }
        String fromTableName = EntityHelper.getEntityTable(joinExample.getEntityClass()).getName();
        for (JoinExample.Join join : joinList) {
            JoinExample.JoinType joinType = join.getJoinType();
            List<JoinExample.JoinProperty> joinPropertyList = join.getJoinPropertyList();
            JoinExample joinTo = join.getJoinTo();
            EntityTable entityTable = EntityHelper.getEntityTable(joinTo.getEntityClass());
            String toTableName = entityTable.getName();
            sql.append(joinType.getType() + " " + toTableName + " " + toTableName + " ");
            for (int i = 0; i < joinPropertyList.size(); i++) {
                JoinExample.JoinProperty jp = joinPropertyList.get(i);
                if (i == 0) {
                    sql.append("on ");
                } else {
                    sql.append("and ");
                }
                String column1 = joinExample.column(jp.getProperty1());
                String column2 = joinTo.column(jp.getProperty2());
                sql.append(fromTableName + "." + column1 + "=" + toTableName + "." + column2 + " ");
            }
            buildJoinSql(joinTo, sql);
        }
    }

    /**
     * 获取所有的column
     * @param joinExample
     * @return
     */
    public static String getAllColumns(JoinExample joinExample) {
        StringBuilder sql = new StringBuilder();
        addColumnsFromJoinExample(joinExample, sql);
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * 获取所有的order by
     * @param joinExample
     * @return
     */
    public static String orderBy(JoinExample joinExample) {
        StringBuilder sql = new StringBuilder();
        buildOrderBy(joinExample, sql); // 递归
        return sql.toString();
    }

    private static void buildOrderBy(JoinExample joinExample, StringBuilder sql) {
        Class<?> entityClass = joinExample.getEntityClass();
        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        String orderByClause = joinExample.getOrderByClause();
//        orderByClause = "id,code DESC,code ASC"
        if (orderByClause != null || !"".equals(orderByClause)){
            sql.append(" order by ");
            String[] orderByColumns = orderByClause.replace("orderType","").split(",");
            for ( int i = 0; i < orderByColumns.length; i++ ){
                sql.append(tableName + "_" + orderByColumns[i]);
                if(i!=orderByColumns.length-1){
                    sql.append(",");
                }
            }
        }
        /*List<JoinExample.Join> joinList = joinExample.getJoinList();
        if (joinExample.getJoinList() != null && !joinExample.getJoinList().isEmpty()) { // 递归结束语句
            for (JoinExample.Join join : joinList) {
                JoinExample joinTo = join.getJoinTo();
                buildOrderBy(joinTo, sql);
            }
        }*/
    }

    public static void main(String[] args) {
        StringBuilder sql = new StringBuilder();
        sql.append("a,b,");
        int i = sql.lastIndexOf(",");
        sql = sql.replace(i-1, i, "");
        System.out.println(sql);
    }
    private static void addColumnsFromJoinList(List<JoinExample.Join> joinList, StringBuilder sql) {
        for (JoinExample.Join join : joinList) {
            JoinExample joinTo = join.getJoinTo();
            addColumnsFromJoinExample(joinTo, sql);
        }
    }

    private static void addColumnsFromJoinExample(JoinExample joinExample, StringBuilder sql) {
        Class<?> entityClass = joinExample.getEntityClass();
        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn entityColumn : columnList) {
            String column = entityColumn.getColumn();
            sql.append(tableName + "." + column + " as " + tableName + '_' + column).append(",");//给字段加前缀tableName
        }
        if (joinExample.getJoinList() != null && !joinExample.getJoinList().isEmpty()) {
            addColumnsFromJoinList(joinExample.getJoinList(), sql);
        }
    }

}
