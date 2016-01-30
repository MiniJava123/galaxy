package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.service.impl.AutoBuildTabServiceImpl;
import com.dianping.data.warehouse.domain.McTableQuery;
import com.dianping.data.warehouse.domain.web.BuildTabParaDO;
import com.dianping.data.warehouse.service.AutoBuildTabService;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by shanshan.jin on 14-6-16.
 */
public class AutoBuildTabServiceImplTest extends TestCase {
    public ApplicationContext context;
    public AutoBuildTabService service;

    public void setUp() throws Exception{
        context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
        service = (AutoBuildTabServiceImpl) context.getBean("autoBuildTabServiceImpl");
    }
    public void testGetTableInfo() throws Exception {
        McTableQuery query = new McTableQuery();
        query.setStorageType("hive");
        query.setDbName("bi");
        query.setTableName("dpods_tg_ordercoupon");
        BuildTabParaDO Do = service.getTableInfo(query, -21651);
        System.out.println(Do);
    }

    public void testGetDdl() throws Exception {
        McTableQuery query = new McTableQuery();
        query.setStorageType("hive");
        query.setDbName("bi");
        query.setTableName("dm_dp_rsa_sd");
        BuildTabParaDO Do = service.getTableInfo(query, -21651);
        String ddl = service.getDdl(Do);
        System.out.println(ddl);

    }

    public void testBuildTable() throws Exception {
        McTableQuery query = new McTableQuery();
        query.setStorageType("hive");
        query.setDbName("bi");
        query.setTableName("dm_dp_rsa_sd_bak3");
        BuildTabParaDO Do = service.getTableInfo(query, -21651);
        String ddl = service.getDdl(Do);
        service.buildTable(Do,ddl);
    }
}
