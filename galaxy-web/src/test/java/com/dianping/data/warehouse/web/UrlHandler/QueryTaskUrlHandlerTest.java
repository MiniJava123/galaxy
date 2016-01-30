package com.dianping.data.warehouse.web.UrlHandler;

import com.dianping.data.warehouse.service.AutoETLService;
import com.dianping.data.warehouse.service.TableService;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-applicationcontext.xml", "classpath:spring-beans.xml"})
public class QueryTaskUrlHandlerTest {

    @Resource(name="tableService")
    private TableService tableService;

    @Resource(name = "autoETLServiceImpl")
    private AutoETLService autoETLService;

    @org.junit.Test
    public void testHandleGetSourceDBtypes() throws Exception {
        System.out.println(tableService.getDbList("hive"));
    }

    @org.junit.Test
    public void testHandleGetDBs() throws Exception {

    }

    @org.junit.Test
    public void testHandleGetAllTasks() throws Exception {

    }

    @org.junit.Test
    public void testHandleDeleteTask() throws Exception {

    }

    @org.junit.Test
    public void testHandleSetTaskInvalidOrValid() throws Exception {

    }
}