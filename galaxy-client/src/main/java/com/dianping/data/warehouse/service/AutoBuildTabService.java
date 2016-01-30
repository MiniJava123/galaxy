package com.dianping.data.warehouse.service;


import com.dianping.data.warehouse.domain.McTableQuery;

import com.dianping.data.warehouse.domain.web.BuildTabParaDO;

/**
 * Created by shanshan.jin on 14-6-10.
 */
public interface AutoBuildTabService {

    public BuildTabParaDO getTableInfo(McTableQuery query, int loginID);

    public String getDdl(BuildTabParaDO paraDO);

    public boolean buildTable(BuildTabParaDO paraDO, String ddl);
}
