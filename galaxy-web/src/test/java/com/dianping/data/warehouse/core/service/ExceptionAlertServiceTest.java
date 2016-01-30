//package com.dianping.data.warehouse.core.service;
//
//import com.dianping.data.warehouse.halley.domain.InstanceDisplayDO;
//import com.dianping.data.warehouse.halley.domain.InstanceQueryDO;
//import com.dianping.data.warehouse.halley.service.InstanceService;
//import com.dianping.data.warehouse.service.ExceptionAlertService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.io.*;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by Sunny on 14-7-21.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring-applicationcontext.xml", "classpath:spring-beans.xml"})
//public class ExceptionAlertServiceTest {
//    @Resource(name = "exceptionAlertService")
//    private ExceptionAlertService exceptionAlertService;
//
//    @Test
//    public void testSetExceptionAlert() {
//
//        File file = new File("/Users/Sunny/Desktop/test.txt");
//        try {
//            FileInputStream in = new FileInputStream(file);
//            assertEquals(true, exceptionAlertService.setExceptionAlert("10779201405300", in));
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
