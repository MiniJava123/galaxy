package com.dianping.data.warehouse.core.utils;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-5-8.
 */
public class GalaxyDateUtilsTest {
    @Test
    public void testGenCalVariable() throws Exception {

    }

    @Test
    public void testTransferVariable() throws Exception {
        long time = DateTime.parse("2014-06-01").toDate().getTime();
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM_01",System.currentTimeMillis(),"D0"));
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM8_01", System.currentTimeMillis(), "D0"));
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM_LD",System.currentTimeMillis(),"D0"));
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM8_LD",System.currentTimeMillis(),"D0"));

        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM_01",time,"D0"));
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM8_01", time, "D0"));
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM_LD",time,"D0"));
        System.out.println(GalaxyDateUtils.transferVariable("YYYYMM8_LD",time,"D0"));
    }
}
