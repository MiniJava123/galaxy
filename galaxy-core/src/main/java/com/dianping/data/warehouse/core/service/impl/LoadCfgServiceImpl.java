package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.dao.BaseDAO;
import com.dianping.data.warehouse.core.dao.impl.SqlMap;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.service.LoadCfgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-17.
 */
@Service("loadCfgServiceImpl")
public class LoadCfgServiceImpl implements LoadCfgService{
    @Resource(name="baseDAOImpl")
    private BaseDAO dao;

    private static Logger logger = LoggerFactory.getLogger(LoadCfgServiceImpl.class);

    @Override
    public boolean insertLoadCfg(List<WormholeDO> wormholeDOList) {
        dao.insert(wormholeDOList, SqlMap.STATEMENT_INSERT_LOADCFG);
        return true;
    }

    @Override
    public Map<Object,Object> getOldLoadCfgByID(Map<String, String> loadCfgParam) {
        return  (Map<Object,Object>)dao.getOldLoadById(loadCfgParam,SqlMap.STATEMENT_GET_OLDCFG_BY_ID);
    }

    @Override
    public boolean deleteLoadCfg(Integer id) {
        dao.delete(id,SqlMap.STATEMENT_DELETE_LOADCFG_BY_ID);
        return true;
    }

    @Override
    public List<WormholeDO> getLoadCfgListByID(Integer id) {
        try{
            return (List<WormholeDO>)dao.getByQuery(id,SqlMap.STATEMENT_GET_LOADCFG_BY_ID);
        }catch(Exception e){
            logger.error("get loadcfg failure",e);
            throw new RuntimeException("get loadcfg failure",e);
        }
    }

    @Override
    public boolean updateLoadCfg(Integer taskID,List<WormholeDO> loadCfgList) {
        dao.delete(taskID,SqlMap.STATEMENT_DELETE_LOADCFG_BY_ID);
        dao.insert(loadCfgList,SqlMap.STATEMENT_INSERT_LOADCFG);
        return true;
    }

    @Override
    public WormholeDO getReaderCfgByID(Integer id) {
        return (WormholeDO)dao.getById(id,SqlMap.STATEMENT_GET_READERCFG_BY_ID);
    }

}
