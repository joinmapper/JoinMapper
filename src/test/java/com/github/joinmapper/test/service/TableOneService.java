package com.github.joinmapper.test.service;

import com.github.joinmapper.test.model.TableOne;
import com.github.pagehelper.PageInfo;

/**
 * describe:
 *
 * @author zhangkai02
 * @date 2018/03/09
 */
public interface TableOneService {

    PageInfo<TableOne> getPage();

}
