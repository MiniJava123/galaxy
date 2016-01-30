//package com.dianping.data.warehouse.service;
//
//import com.dianping.data.warehouse.core.service.impl.ExceptionAlertServiceImpl;
//import com.dianping.data.warehouse.domain.model.ExceptionAlertDO;
//import com.dianping.data.warehouse.service.ExceptionAlertService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
///**
// * Created by Sunny on 14-7-22.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring-applicationcontext.xml", "classpath:spring-beans.xml"})
//public class ExceptionAlertServiceImplTest {
//
//    public ApplicationContext context;
//
//    @Resource(name = "exceptionAlertServiceImpl")
//    public ExceptionAlertService service;
//
//
//    @Test
//    public void testAnalyze() throws Exception {
//        File file = new File("/Users/Sunny/Desktop/test.txt");
//        try {
//            FileInputStream in = new FileInputStream(file);
//            ExceptionAlertDO alert = service.analyze(in);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
