//package com.dianping.data.warehouse.core.service.impl;
//
//import com.dianping.data.warehouse.domain.model.WormholeDO;
//import com.dianping.data.warehouse.service.LoadCfgService;
//import junit.framework.Assert;
//import junit.framework.TestCase;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import java.util.List;
//
///**
// * Created by hongdi.tang on 14-3-20.
// */
//public class LoadCfgServiceImplTest extends TestCase {
//
//    public ApplicationContext context;
//    public LoadCfgService service;
//    public void setUp() throws Exception {
//        context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
//        service = (LoadCfgServiceImpl)context.getBean("loadCfgServiceImpl");
//    }
//
//    public void testGetLoadCfgListByID() throws Exception {
//        List<WormholeDO> list = service.getLoadCfgListByID(10652);
//        Assert.assertNotNull(list);
//        Assert.assertNotNull(list.get(0).getParameterMapStr());
//        Assert.assertNotNull(list.get(1).getParameterMapStr());
//    }
//
//}
