package com.dianping.data.warehouse.starshuttle.ddl;

import com.dianping.data.warehouse.domain.model.WormholeDO;

/**
 * Created by hongdi.tang on 14-4-15.
 */
public class GreenplumMakerImpl implements DDLMaker{

    @Override
    public String preSql(WormholeDO cfg) {

        return null;
    }

    @Override
    public String rollbackSql() {
        return null;
    }

    @Override
    public String postSql() {
        return null;
    }
}
