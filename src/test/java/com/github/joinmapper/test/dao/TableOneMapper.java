package com.github.joinmapper.test.dao;

import com.github.joinmapper.common.JoinMapper;
import com.github.joinmapper.test.model.TableOne;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TableOneMapper extends JoinMapper<TableOne>, Mapper<TableOne> {
    int getCount();

    List<TableOne> selectJoinUseXml();
}
