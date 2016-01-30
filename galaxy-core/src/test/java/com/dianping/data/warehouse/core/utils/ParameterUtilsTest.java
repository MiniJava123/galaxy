package com.dianping.data.warehouse.core.utils;

import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-5-7.
 */
public class ParameterUtilsTest {
    @Test
    public void testProcessData() throws Exception {
        StarShuttleParasDO para = new StarShuttleParasDO();
        para.setTargetTableName("Fuck");
        para = DomainUtils.processData(para);
        System.out.println(para.getTargetTableName());
    }
}
