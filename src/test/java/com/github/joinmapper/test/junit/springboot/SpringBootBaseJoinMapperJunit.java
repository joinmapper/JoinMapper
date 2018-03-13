package com.github.joinmapper.test.junit.springboot;

import com.github.joinmapper.test.SpringBootMain;
import com.github.joinmapper.test.junit.BaseJoinMapperJunit;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * describe:
 * springboot junit
 *
 * @author zhangkai02
 * @date 2018/03/09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootMain.class)
public class SpringBootBaseJoinMapperJunit extends BaseJoinMapperJunit {

    public void dataSource() {
        super.dataSource();
    }

    public void testMapper() {
        super.testMapper();
    }

    public void testExample() {
        super.testExample();
    }

    public void testJoinExample1() {
        super.testJoinExample1();
    }

    public void testJoinExample2() {
        super.testJoinExample2();
    }

    public void testJoinPageHelper() {
        super.testJoinPageHelper();
    }
}
