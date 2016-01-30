//package com.dianping.data.warehouse.starshuttle.utils;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by Sunny on 14-7-21.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring-applicationcontext.xml", "classpath:spring-beans.xml"})
//public class ExceptionAnalyzeTest {
//
//    @Test
//    public void testAnalyze() {
//        File file = new File("/Users/Sunny/Desktop/test.txt");
//        try {
//            FileInputStream in = new FileInputStream(file);
//            assertEquals(true, ExceptionAnalyze.analyze(in));
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//}
