package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.service.impl.AutoETLServiceImpl;
import com.dianping.data.warehouse.core.service.impl.LoadCfgServiceImpl;
import com.dianping.data.warehouse.domain.TableExistStatus;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.masterdata.service.McMetaService;
import com.dianping.data.warehouse.masterdata.service.MercuryService;
import com.dianping.data.warehouse.service.LoadCfgService;
import com.dianping.data.warehouse.service.TableService;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mt on 2014/3/27.
 */
public class TableServiceImplTest extends TestCase {
    public ApplicationContext context;
    public AutoETLServiceImpl service;
    public LoadCfgService loadCfgService;
    public TaskService taskService;
    public McMetaService mcMetaService;
    private MercuryService mercuryService;
    TableService tableService;
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
        service = (AutoETLServiceImpl)context.getBean("autoETLServiceImpl");
        loadCfgService = (LoadCfgServiceImpl)context.getBean("loadCfgServiceImpl");
        taskService = (TaskService)context.getBean("taskService");
        mcMetaService = (McMetaService)context.getBean("mcMetaService");
        mercuryService = (MercuryService)context.getBean("mercuryService");
        tableService = (TableService)context.getBean("tableService");
    }

    public void testUpdateStatus() throws Exception {
        TableExistStatus status = new TableExistStatus();
        Map<String, Boolean> existMap = new HashMap<String, Boolean>();
        existMap.put("hive",false);
        existMap.put("gpReport",false);
        existMap.put("GpAnalysis",false);
        status.setExistMap(existMap);
        Map<String,Integer> taskIdMap = new HashMap<String, Integer>();
        taskIdMap.put("hive",10001);
        taskIdMap.put("gpReport",10002);
        taskIdMap.put("GpAnalysis",0);
        status.setTaskIdMap(taskIdMap);

//        List<TaskDO> taskDOList = taskService.getAllTasks();
//        Boolean isSuccess = tableService.updateStatus(taskDOList, status);

        Assert.assertEquals(status.getTaskStorageLocationMap().get("hive"),Boolean.FALSE);
        Assert.assertEquals(status.getTaskStorageLocationMap().get("gpReport"),Boolean.TRUE);
        Assert.assertEquals(status.getTaskStorageLocationMap().get("GpAnalysis"),Boolean.FALSE);
//        Assert.assertEquals(isSuccess,Boolean.TRUE);
    }
}
