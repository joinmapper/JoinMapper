/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.joinmapper.provider;

import com.github.joinmapper.mapperhelper.JoinMapperTemplate;
import com.github.joinmapper.mapperhelper.JoinSqlHelper;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

/**
 * ExampleProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class JoinProvider extends JoinMapperTemplate {


    public JoinProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 根据Example查询
     *
     * @param ms MappedStatement
     * @return result
     */
    public String selectJoin(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);

        StringBuilder sql = new StringBuilder("SELECT ");
        if (isCheckExampleEntityClass()) {
            sql.append(SqlHelper.exampleCheck(entityClass));
        }
        sql.append("<if test=\"distinct\">distinct</if>");
        //支持查询指定列
        //SELECT <if test="distinct">distinct</if><choose><when test="@tk.mybatis.mapper.util.OGNL@hasSelectColumns(_parameter)"><foreach collection="_parameter.selectColumns" item="selectColumn" separator=",">${selectColumn}</foreach></when><otherwise>id,code</otherwise></choose>
        //_parameter 是Example类型， 从tk.mybatis.mapper.util.OGNL@hasSelectColumns可以看出， 操作Example类，给tk.mybatis.mapper.entity.Example.selectColumns集合中的字段增加前缀
        sql.append(JoinSqlHelper.exampleSelectColumns(entityClass));
        sql.append(JoinSqlHelper.fromTable(entityClass));
        sql.append(JoinSqlHelper.join(entityClass));
        sql.append(JoinSqlHelper.exampleWhereClause(entityClass));
        sql.append(JoinSqlHelper.exampleOrderBy(entityClass));
//        sql.append(JoinSqlHelper.exampleForUpdate()); // 暂不支持
        return sql.toString();
    }

}
