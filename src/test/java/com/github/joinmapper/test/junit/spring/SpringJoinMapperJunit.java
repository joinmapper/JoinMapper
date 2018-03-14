package com.github.joinmapper.test.junit.spring;

import com.github.joinmapper.test.junit.BaseJoinMapperJunit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * describe:
 * spring junit
 *
 * @author zhangkai02
 * @date 2018/03/09
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath*:applicationContext*.xml"})
public class SpringJoinMapperJunit extends BaseJoinMapperJunit {
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
