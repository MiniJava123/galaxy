//package com.dianping.data.warehouse.core.handler.impl;
//
//import com.dianping.data.warehouse.core.service.BlackListService;
//import junit.framework.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//
///**
// * Created by hongdi.tang on 14-5-28.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:spring-beans.xml")
//public class BlackListServiceImplTest {
//
//    @Resource(name="blackListServiceImpl")
//    private BlackListService blackListService;
//
//    @Test
//    public void testGetUpdateBlackList() throws Exception {
//        String blackListStr = blackListService.getUpdateBlackList();
//        System.out.println(blackListStr);
//        Assert.assertNotNull(blackListStr);
//
//    }
//}
