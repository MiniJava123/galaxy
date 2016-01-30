package com.dianping.data.warehouse.core.handler.impl;

import junit.framework.TestCase;

/**
 * Created by hongdi.tang on 14-3-4.
 */
public class MediaHandlerImplTest extends TestCase {
    public MediaHandlerImpl service;

    public void setUp() throws Exception {
        service = new MediaHandlerImpl();
    }

    public void testGetConnection() throws Exception {
        service.executeDDL("gpanalysis","dianpingdw","bi","use bi;desc dpstg_sss");
        //service.getConnection("hive","bi",null);
    }

    public void testCreateTable() throws Exception {
//        String ddl ="create table etl_task_test as select * From etl_task_cfg where 1=2;";
//        System.out.println(ddl);
    }

    public void testDropTable() throws Exception {

    }
}
