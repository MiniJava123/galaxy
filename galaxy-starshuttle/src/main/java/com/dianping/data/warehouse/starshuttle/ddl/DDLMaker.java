package com.dianping.data.warehouse.starshuttle.ddl;

import com.dianping.data.warehouse.domain.model.WormholeDO;

/**
 * Created by hongdi.tang on 14-4-15.
 */
public interface DDLMaker {
    String preSql(WormholeDO cfg);
    String rollbackSql();
    String postSql();
}
