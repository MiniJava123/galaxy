package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.service.impl.AutoETLServiceImpl;
import com.dianping.data.warehouse.core.service.impl.LoadCfgServiceImpl;
import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.service.LoadCfgService;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hongdi.tang on 14-3-6.
 */
public class AutoETLServiceImplTest extends TestCase {

    public ApplicationContext context;
    public AutoETLServiceImpl service;
    public LoadCfgService loadCfgService;
    public TaskService taskService;
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext(new String[]{"classpath:spring-applicationcontext.xml","classpath:spring-beans.xml"});
        service = (AutoETLServiceImpl)context.getBean("autoETLServiceImpl");
        loadCfgService = (LoadCfgServiceImpl)context.getBean("loadCfgServiceImpl");
        taskService = (TaskService)context.getBean("taskService");
    }

//    public void testMigrateData() throws Exception {
//        Boolean isSuccess = service.migrateTaskData(10002);
//        TaskDO taskDO  = taskService.getTaskByID(10002);
//        WormholeDO wormholeDO = loadCfgService.getReaderCfgByID(10002);
//
//        Assert.assertEquals(taskDO.getTaskObj(), "sh /data/deploy/dwarch/conf/ETL/bin/starshuttle.sh");
//        Assert.assertEquals(taskDO.getPara1(),"-id ${task_id}");
//        Assert.assertEquals(taskDO.getPara2(),"-time ${unix_timestamp}");
//        Assert.assertEquals(taskDO.getPara3(),"-offset D0");
//
//        Assert.assertEquals(wormholeDO.getConditionCol(),null);
//        Assert.assertEquals(wormholeDO.getType(),"reader");
////        Assert.assertEquals(wormholeDO.getParameterMapStr(),"");
//        Assert.assertEquals(isSuccess,Boolean.TRUE);
//    }
//
//    public void testGenerateAutoETLTask() throws Exception {
//        StarShuttleParasDO para = new StarShuttleParasDO();
//
//        para.setOwner("tao.meng");
//        para.setSourceSchemaName(null);
//        para.setSourceDBType("mysql");
//        para.setSourceTableName("acl_push_dbs");
//        para.setSourceDBName("DianPingDW2");
//
//        para.setTargetDBType("hive");
//        para.setTargetTableName("dpods_acl_push_dbs");
//        para.setTargetDBName("bi");
//        para.setTargetSchemaName("dpods");
//        para.setDateType("D");
//        para.setDateOffset("D1");
//        para.setDateNumber(0);
//        para.setTargetTableType(Const.TABLE_TYPE_COMPLETE);
//
//        StarShuttleDO ss = service.getAllInfo(para);
//        service.generateAutoETLTask(para,ss);
//    }

    public void testDelAutoETLTask() throws Exception {
       service.deleteAutoEtlTask(10374,"2014-11-11","ning.sun");
    }
}
