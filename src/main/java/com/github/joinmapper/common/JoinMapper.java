package com.github.joinmapper.common;

import com.github.joinmapper.entity.JoinExample;
import com.github.joinmapper.provider.JoinProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 关联查询
 *
 * @param <T>
 */
public interface JoinMapper<T> {
    @SelectProvider(type = JoinProvider.class, method = "dynamicSQL")
    List<T> selectJoin(JoinExample joinExample);
}
