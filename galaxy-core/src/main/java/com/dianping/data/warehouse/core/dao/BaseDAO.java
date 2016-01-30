package com.dianping.data.warehouse.core.dao;

import javax.sql.DataSource;
import java.util.List;

/**
 * @Author <a href="mailto:tsensue@gmail.com">dishu.chen</a>
 * 13-12-24.
 */
public interface BaseDAO <T, Q> {
    T getById(Integer id, String sqlMapId);
    T getOldLoadById(Q q,String sqlMapId);
    List<T> getByQuery(Q q, String sqlMapId);
    void insert(Q q, String sqlMapId);
    void update(Q q, String sqlMapId);
    void delete(Q q, String sqlMapId);
    void reset(DataSource dataSource);
}
