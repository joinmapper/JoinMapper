package com.github.joinmapper.test.service;

import com.github.joinmapper.test.dao.TableOneMapper;
import com.github.joinmapper.entity.JoinExample;
import com.github.joinmapper.test.model.TableFour;
import com.github.joinmapper.test.model.TableOne;
import com.github.joinmapper.test.model.TableThree;
import com.github.joinmapper.test.model.TableTwo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * describe:
 *
 * @author zhangkai02
 * @date 2018/03/09
 */
@Service
public class TableOneServiceImpl implements TableOneService {
    @Autowired
    private TableOneMapper tableOneMapper;

    @Override
    public PageInfo<TableOne> getPage() {
        Page<TableOne> page = PageHelper.startPage(1, 10);
        JoinExample tableOneEx = new JoinExample(TableOne.class);
        tableOneEx.orderBy("id").orderBy("code").desc().orderBy("code").asc();
        //tableOneEx.selectProperties("id");
        Example.Criteria criteria = tableOneEx.createCriteria();
        criteria.andEqualTo("id", "table_one_1");

        JoinExample tableTwoEx = new JoinExample(TableTwo.class);

        JoinExample tableThreeEx = new JoinExample(TableThree.class);

        JoinExample tableFourEx = new JoinExample(TableFour.class);

        //关联查询设置
        tableOneEx.leftJoin(tableTwoEx, JoinExample.ResultType.ONE, "tableTwo", "id", "id");
        tableTwoEx.leftJoin(tableThreeEx, JoinExample.ResultType.ONE, "tableThree", "id", "id");
        tableThreeEx.leftJoin(tableFourEx, JoinExample.ResultType.MANY, "tableFourList", "id", "id");

        List<TableOne> tableOneList = tableOneMapper.selectJoin(tableOneEx);
        return page.toPageInfo();
    }
}
