package com.github.joinmapper.test.junit;

import com.github.joinmapper.test.dao.TableOneMapper;
import com.github.joinmapper.test.dao.TableThreeMapper;
import com.github.joinmapper.test.dao.TableTwoMapper;
import com.github.joinmapper.entity.JoinExample;
import com.github.joinmapper.test.model.TableFour;
import com.github.joinmapper.test.model.TableOne;
import com.github.joinmapper.test.model.TableThree;
import com.github.joinmapper.test.model.TableTwo;
import com.github.joinmapper.test.service.TableOneService;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.sql.DataSource;
import java.util.List;

public abstract class BaseJoinMapperJunit extends JunitUtil {
    @Autowired
    protected DataSource dataSource;

    @Test
    public void dataSource() {
        String s = dataSource.toString();
        System.out.println(s);
    }

    @Autowired
    TableTwoMapper tableTwoMapper;
    @Autowired
    TableThreeMapper tableThreeMapper;
    @Autowired
    TableOneService tableOneService;

    @Test
    public void testMapper() {
        /*int count = tableOneMapper.getCount();
        super.print(count);
        int count1 = tableOneMapper.selectCount(new TableOne());
        super.print(count1);*/
        List<TableOne> tableOnes = tableOneMapper.selectJoinUseXml();
        super.print(tableOnes);
    }

    @Test
    public void testExample() {
        /*Example example = new Example(TableOne.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("code", "table_one_code");
        List<TableOne> tableOnes = tableOneMapper.selectByExample(example);
        super.print(tableOnes);*/
    }

    @Test
    public void testJoinExample1() {
        JoinExample tableOneEx = new JoinExample(TableOne.class);
//        tableOneEx.selectProperties("id");
        Example.Criteria criteria = tableOneEx.createCriteria();
        criteria.andEqualTo("id", "table_one_1");

        JoinExample tableTwoEx = new JoinExample(TableTwo.class);

        JoinExample tableThreeEx = new JoinExample(TableThree.class);

        //关联查询设置
        tableOneEx.leftJoin(tableTwoEx, JoinExample.ResultType.ONE, "tableTwo", "id", "id").on("code", "code");
        tableOneEx.rightJoin(tableThreeEx, JoinExample.ResultType.ONE, "tableThree", "id", "id");

        List<TableOne> tableOneList = tableOneMapper.selectJoin(tableOneEx);
        super.print(tableOneList);
    }

    @Autowired
    TableOneMapper tableOneMapper;
    @Test
    public void testJoinExample2() {
        try {
            JoinExample tableOneEx = new JoinExample(TableOne.class);
//            tableOneEx.orderBy("id").orderBy("code").desc().orderBy("code").asc();
            //tableOneEx.selectProperties("id");
            Example.Criteria criteria = tableOneEx.createCriteria();
            criteria.andEqualTo("id", "table_one_1");

            JoinExample tableTwoEx = new JoinExample(TableTwo.class);
//            tableTwoEx.orderBy("id").orderBy("code").desc();
            JoinExample tableThreeEx = new JoinExample(TableThree.class);
//            tableThreeEx.orderBy("id").orderBy("code").desc();
            JoinExample tableFourEx = new JoinExample(TableFour.class);
            tableFourEx.createJoinOrderBy().orderBy(TableOne.class,"id").orderBy("id").desc().orderBy(TableTwo.class, "code");

            //关联查询设置
            tableOneEx.leftJoin(tableTwoEx, JoinExample.ResultType.ONE, "tableTwo", "id", "id");
            tableTwoEx.leftJoin(tableThreeEx, JoinExample.ResultType.ONE, "tableThree", "id", "id");
            tableThreeEx.leftJoin(tableFourEx, JoinExample.ResultType.MANY, "tableFourList", "id", "id");

            List<TableOne> tableOneList = tableOneMapper.selectJoin(tableOneEx);
            super.print(tableOneList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJoinPageHelper() {
        try {
            PageInfo<TableOne> pageInfo = tableOneService.getPage();
            super.print(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
