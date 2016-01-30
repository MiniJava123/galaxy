package com.dianping.data.warehouse.core.common;

import com.dianping.data.warehouse.core.model.DSInfo;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-2-20.
 */

public class GlobalResourcesTest{

    @Test
    public void testGetDsInfo(){
        DSInfo dsInfo = GlobalResources.getDsInfoFromLion("gp_report.gpreport58.bi");
        DSInfo dsInfo1 = GlobalResources.getDsInfoFromLion("gp_report.gpreport58.bi");
        DSInfo dsInfo2 = GlobalResources.getDsInfoFromLion("gp_olaptest.test");
        Assert.assertNull(dsInfo);
    }
}
