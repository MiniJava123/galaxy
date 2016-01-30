package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.domain.model.WormholeDO;

import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public interface LoadCfgService {
    public boolean insertLoadCfg(List<WormholeDO> wormholeDOList);

    public Map<Object,Object> getOldLoadCfgByID( Map<String,String> loadCfgParam);

    public boolean deleteLoadCfg(Integer id);

    public List<WormholeDO> getLoadCfgListByID(Integer id);

    public boolean updateLoadCfg(Integer taskID, List<WormholeDO> loadCfgList);

    public WormholeDO getReaderCfgByID(Integer id);
}
