package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.service.impl.AutoETLServiceImpl;
import com.dianping.data.warehouse.core.service.impl.LoadCfgServiceImpl;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.service.LoadCfgService;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by mt on 2014/3/26.
 */
public class AutoETLServiceImplTest extends TestCase {
    public ApplicationContext context;
    public AutoETLServiceImpl service;
    public LoadCfgService loadCfgService;
    public TaskService taskService;

    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext(new String[]{"classpath:spring-applicationcontext.xml", "classpath:spring-beans.xml"});
        service = (AutoETLServiceImpl) context.getBean("autoETLServiceImpl");
        loadCfgService = (LoadCfgServiceImpl) context.getBean("loadCfgServiceImpl");
        taskService = (TaskService) context.getBean("taskService");
    }

    public void testDelAutoEtlTask() throws Exception {
        service.deleteAutoEtlTask(10374,"2014-11-11","ning.sun");
    }
}

//    public void testMigrateData() throws Exception {
//        Boolean isSuccess = service.migrateTaskData(10001);
//
//import com.dianping.data.warehouse.halley.service.TaskService;
//import com.dianping.data.warehouse.service.LoadCfgService;
//import junit.framework.TestCase;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
///**
// * Created by mt on 2014/3/26.
// */
//public class AutoETLServiceImplTest extends TestCase {
//    public ApplicationContext context;
//    public AutoETLServiceImpl service;
//    public LoadCfgService loadCfgService;
//    public TaskService taskService;
//
//    public void setUp() throws Exception {
//        context = new ClassPathXmlApplicationContext(new String[]{"classpath:spring-applicationcontext.xml","classpath:spring-beans.xml"});
//        service = (AutoETLServiceImpl)context.getBean("autoETLServiceImpl");
//        loadCfgService = (LoadCfgServiceImpl)context.getBean("loadCfgServiceImpl");
//        taskService = (TaskService)context.getBean("taskService");
//    }
//      public void testDelAutoEtlTask() throws Exception {
//          service.deleteAutoEtlTask(10374);
//      }
//
////    public void testMigrateData() throws Exception {
////        Boolean isSuccess = service.migrateTaskData(10001);
////
////        TaskDO taskDO  = taskService.getTaskByID(10001);
////        WormholeDO wormholeDO = loadCfgService.getReaderCfgByID(10001);
////
////        Assert.assertEquals(taskDO.getTaskObj(),"sh /data/deploy/dwarch/conf/ETL/bin/starshuttle.sh");
////        Assert.assertEquals(taskDO.getPara1(),"-id ${task_id}");
////        Assert.assertEquals(taskDO.getPara2(),"-time ${unix_timestamp}");
////        Assert.assertEquals(taskDO.getPara3(),"-offset D0");
////
////        Assert.assertEquals(wormholeDO.getConditionCol(),null);
////        Assert.assertEquals(wormholeDO.getType(),"reader");
//////        Assert.assertEquals(wormholeDO.getParameterMapStr(),"");
////        Assert.assertEquals(isSuccess,Boolean.TRUE);
////    }
//}
