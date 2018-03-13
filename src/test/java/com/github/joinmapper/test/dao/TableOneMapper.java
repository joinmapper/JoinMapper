package com.github.joinmapper.test.dao;

import com.github.joinmapper.common.JoinMapper;
import com.github.joinmapper.test.model.TableOne;

import java.util.List;

public interface TableOneMapper extends JoinMapper<TableOne> {
    int getCount();

    List<TableOne> selectJoinUseXml();
}
