package com.dianping.data.warehouse.core.dao.impl;

import com.dianping.data.warehouse.core.dao.BaseDAO;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import javax.sql.DataSource;
import java.util.List;

/**
 * @Author <a href="mailto:tsensue@gmail.com">dishu.chen</a>
 * 13-12-24.
 */
@SuppressWarnings("unchecked")
public class BaseDAOImpl<T, Q> extends SqlMapClientDaoSupport implements BaseDAO<T, Q> {
    public SqlMapClientTemplate getSqlMapClientTemplate1(){
        return this.getSqlMapClientTemplate();
    }

    @Override
    public T getById(Integer id, String sqlMapId) {
        return (T) this.getSqlMapClientTemplate().queryForObject(sqlMapId, id);
    }

    @Override
    public T getOldLoadById(Q q, String sqlMapId) {
        return (T) this.getSqlMapClientTemplate1().queryForMap(sqlMapId,q,"task_id");
    }

    @Override
    public List<T> getByQuery(Q q, String sqlMapId) {
        return this.getSqlMapClientTemplate().queryForList(sqlMapId, q);
    }

    @Override
    public void insert(Q q,String sqlMapId){
        this.getSqlMapClientTemplate().insert(sqlMapId,q);
    }

    @Override
    public void update(Q q, String sqlMapId) {
        this.getSqlMapClientTemplate().update(sqlMapId,q);
    }

    @Override
    public void delete(Q q, String sqlMapId) {
        this.getSqlMapClientTemplate().delete(sqlMapId,q);
    }

    @Override
    public void reset(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
}
