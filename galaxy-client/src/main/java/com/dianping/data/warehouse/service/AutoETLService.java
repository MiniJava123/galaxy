package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;

import java.util.List;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public interface AutoETLService {

    StarShuttleDO getAllInfo(StarShuttleParasDO parasDO);

    StarShuttleDO readAllInfo(StarShuttleParasDO parasDO);

    boolean generateAutoETLTask(StarShuttleParasDO parasDO, StarShuttleDO dos, String updateTime, String updateUser);

    boolean updateAdvanceCfg(StarShuttleDO dos, StarShuttleParasDO parasDO);

    List<McColumnInfo> getSourceColumnInfo(StarShuttleParasDO parasDO);

    boolean updateAutoETLTask(StarShuttleDO dos, StarShuttleParasDO parasDO);

    public boolean deleteAutoEtlTask(int taskId, String updateTime, String updateUser);

    boolean migrateTaskData(Integer taskID);

    List<String> getTargetDBTypeBySourceDBType(String sourceDBType);

    public boolean deleteTaskTables(int taskId);

    public boolean updateMcTableInfo(StarShuttleDO dos, StarShuttleParasDO parasDO);

}
