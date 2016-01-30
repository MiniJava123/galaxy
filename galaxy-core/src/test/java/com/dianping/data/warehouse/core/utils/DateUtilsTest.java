
package com.dianping.data.warehouse.core.utils;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-3-5.
 */
public class DateUtilsTest {

//    public void testGetCalVariables() throws Exception {
//        System.out.println(GalaxyDateUtils.genCalVariable("MTD",null));
//
//import junit.framework.Assert;
//import org.joda.time.DateTime;
//import org.junit.Test;
//
///**
// * Created by hongdi.tang on 14-3-5.
// */
//public class DateUtilsTest  {
//
////    public void testGetCalVariables() throws Exception {
////        System.out.println(GalaxyDateUtils.genCalVariable("MTD",null));
////
////    }
//    @Test
//    public void genCalVariableTest() throws Exception{
//        long time = DateTime.parse("2014-04-01").toDate().getTime();
//        System.out.println(time);
//        String var = GalaxyDateUtils.genCalVariable("MTD", "D0");
//        String var1 = GalaxyDateUtils.transferVariable("YYYYMM8_01", time, "D0");
//        String var2 = GalaxyDateUtils.transferVariable("YYYYMM_01", time, "D0");
//        String var3 = GalaxyDateUtils.transferVariable("YYYYMM8_LD", System.currentTimeMillis(), "D0");
//        String var4 = GalaxyDateUtils.transferVariable("YYYYMM_LD", System.currentTimeMillis(), "D0");
//        String var5 = GalaxyDateUtils.transferVariable("CUR_YYYYMM_01", time, "D0");
//        String var6 = GalaxyDateUtils.transferVariable("CUR_YYYYMM8_01", time, "D0");
//        System.out.println(var);
//        System.out.println(var1);
//        System.out.println(var2);
//        System.out.println(var3);
//        System.out.println(var4);
//        System.out.println(var5);
//        System.out.println(var6);
//        Assert.assertEquals(var2,var5);
//        Assert.assertEquals(var3,var6);
//    }

    @Test
    public void stringConcatTest() throws Exception{
        System.out.println("2233" + " " + null + " asefs");
    }

    @Test
    public void genCalVariableTest() throws Exception {
        long time = DateTime.parse("2014-04-15").toDate().getTime();
        System.out.println(time);
        String var = GalaxyDateUtils.genCalVariable("MTD", "D0");
        String var1 = GalaxyDateUtils.transferVariable("YYYYMM8_01", time, "D0");
        String var2 = GalaxyDateUtils.transferVariable("YYYYMM_01", time, "D0");
        String var3 = GalaxyDateUtils.transferVariable("YYYYMM8_LD", time, "D0");
        String var4 = GalaxyDateUtils.transferVariable("YYYYMM_LD", time, "D0");
        String var5 = GalaxyDateUtils.transferVariable("CUR_YYYYMM_01", time, "D0");
        String var6 = GalaxyDateUtils.transferVariable("CUR_YYYYMM8_01", time, "D0");
        System.out.println(var);
        System.out.println(var1);
        System.out.println(var2);
        System.out.println(var3);
        System.out.println(var4);
        System.out.println(var5);
        System.out.println(var6);
    }
}