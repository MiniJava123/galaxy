package com.dianping.data.warehouse.web.process;

import com.dianping.data.warehouse.core.service.impl.TableServiceImpl;
import com.dianping.data.warehouse.service.TableService;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hongdi.tang on 14-2-28.
 */
public class QueryTaskLoadDBsProcessTest extends TestCase {
    public ApplicationContext context;
    public void setUp() throws Exception {
        context =  new ClassPathXmlApplicationContext(
                new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
    }

    public void testProcess() throws Exception {
        TableService tableService = (TableServiceImpl)context.getBean("tableServiceImpl");
        System.out.println(tableService.getDbList("hive"));
    }
}
