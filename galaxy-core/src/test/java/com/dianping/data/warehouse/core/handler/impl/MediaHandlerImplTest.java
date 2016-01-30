package com.dianping.data.warehouse.core.handler.impl;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by mt on 2014/4/14.
 */
public class MediaHandlerImplTest {
    @org.junit.Test
    public void testGetConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://10.1.21.57:5432/dianpingdw?useUnicode=true&characterEncoding=utf-8",
                "bi", "bi3da93da039dbi");
        System.out.println(connection);
    }
}
