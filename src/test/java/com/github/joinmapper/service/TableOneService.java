package com.github.joinmapper.service;

import com.github.joinmapper.model.TableOne;
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
