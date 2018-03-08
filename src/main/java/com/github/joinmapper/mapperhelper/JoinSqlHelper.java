package com.github.joinmapper.mapperhelper;

import com.github.joinmapper.util.JoinOGNL;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.util.Set;

public class JoinSqlHelper {
    public static final String JOIN_OGNL_TYPE_NAME = JoinOGNL.class.getTypeName();

    /**
     * example支持查询指定列时
     *
     * @return
     */
    public static String exampleSelectColumns(Class<?> entityClass) {
        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(_parameter)\">");
        sql.append("<foreach collection=\"_parameter.selectColumns\" item=\"selectColumn\" separator=\",\">");
        sql.append(tableName + ".${selectColumn} as " + tableName + "_${selectColumn}");
        sql.append("</foreach>");
        sql.append("</when>");
        //不支持指定列的时候查询全部列
        sql.append("<otherwise>");
        sql.append("${@" + JOIN_OGNL_TYPE_NAME + " @getAllColumns(_parameter)}");// 使用_parameter
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass
     * @return
     */
    public static String getAllColumns(Class<?> entityClass) {
        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnList) {
            String column = entityColumn.getColumn();
            sql.append(tableName + "." + column + " as " + tableName + '_' + column).append(",");//给字段加前缀tableName
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * Example查询中的where结构，用于只有一个Example参数时
     *
     * @return
     */
    public static String exampleWhereClause(Class<?> entityClass) {
        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        String aliasStr = tableName + ".";
        return "<if test=\"_parameter != null\">" +
                "<where>\n" +
                "  <foreach collection=\"oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} " + aliasStr + "${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} " + aliasStr + "${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} " + aliasStr + "${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} " + aliasStr + "${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                "</where>" +
                "</if>";
    }

    /**
     * from tableName - 动态表名
     *
     * @param entityClass
     * @return
     */
    public static String fromTable(Class<?> entityClass) {
        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM ");
        sql.append(tableName + " " + tableName);
        sql.append(" ");
        return sql.toString();
    }

    public static String join(Class<?> entityClass) {
        return " ${@" + JOIN_OGNL_TYPE_NAME + " @join(_parameter)} ";
    }

    /**
     * example查询中的orderBy条件，会判断默认orderBy
     *
     * @return
     */
    public static String exampleOrderBy(Class<?> entityClass) {
/*        String tableName = EntityHelper.getEntityTable(entityClass).getName();
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"orderByClause != null\">");
        sql.append("order by " + tableName + ".${orderByClause}");
        sql.append("</if>");
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append("<if test=\"orderByClause == null\">");
            sql.append("ORDER BY " + tableName + "." + orderByClause);
            sql.append("</if>");
        }*/
//        return sql.toString();
        return " ${@" + JOIN_OGNL_TYPE_NAME + "@orderBy(_parameter)} ";
    }

    /**
     * example 支持 for update
     *
     * @return
     */
    public static String exampleForUpdate() {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"@tk.mybatis.mapper.util.OGNL@hasForUpdate(_parameter)\">");
        sql.append("FOR UPDATE");
        sql.append("</if>");
        return sql.toString();
    }

}
