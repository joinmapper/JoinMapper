package com.github.joinmapper.test.dao;

import com.github.joinmapper.common.JoinMapper;
import com.github.joinmapper.test.model.TableOne;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TableOneMapper extends Mapper<TableOne>, JoinMapper<TableOne> {
    int getCount();

    List<TableOne> selectJoin_();
}
