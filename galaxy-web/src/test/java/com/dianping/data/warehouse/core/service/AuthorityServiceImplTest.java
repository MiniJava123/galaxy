package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.service.AuthorityService;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-applicationcontext.xml", "classpath:spring-beans.xml"})
public class AuthorityServiceImplTest {
    @Resource(name = "authorityService")
    private AuthorityService authorityService;

//    @Test
//    public void testIsAdmin() throws Exception {
//        //shanshan.jin
//        Assert.assertEquals(authorityService.isAdmin(-19940, 1), false);
//        //hongdi
//        Assert.assertEquals(authorityService.isAdmin(-16980, 1), true);
//    }
//
//    @Test
//    public void testIsOwner() throws Exception {
//        Assert.assertEquals(authorityService.isOwner("shanshan.jin", 10700), false);
//        Assert.assertEquals(authorityService.isOwner("shanshan.jin", 10684), true);
//        Assert.assertEquals(authorityService.isOwner("tao.meng", 10700), true);
//        Assert.assertEquals(authorityService.isOwner("tao.meng", 10684), false);
//    }
}