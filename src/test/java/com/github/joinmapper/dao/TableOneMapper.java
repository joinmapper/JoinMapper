package com.github.joinmapper.dao;

import com.github.joinmapper.common.JoinMapper;
import com.github.joinmapper.model.TableOne;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface TableOneMapper
        extends Mapper<TableOne>, JoinMapper<TableOne>
{
    int getCount();

    List<TableOne> selectJoin_();
}
