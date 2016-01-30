package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.handler.AbstractHandler;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.handler.MediaHandler;
import com.dianping.data.warehouse.core.utils.JacksonHelper;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.service.LoadCfgService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongdi.tang on 14-4-28.
 */
@Service("migrateService")
public class MigrateService extends AbstractHandler {
    private Logger logger = LoggerFactory.getLogger(MigrateService.class);
    @Resource(name = "handlerMap")
    private Map<String, Handler> handlerMap;

    @Resource(name = "mediaHandlerImpl")
    private MediaHandler mediaHandler;

    @Resource(name = "loadCfgServiceImpl")
    private LoadCfgService loadService;


    public boolean migrateMysql2Hive(Integer taskID) {

        WormholeDO readWormholeDO = new WormholeDO();
        WormholeDO writeWormholeDO = new WormholeDO();
        List<WormholeDO> wormholeDOList = new ArrayList<WormholeDO>();

        List<TaskDO> originTaskDOList = new ArrayList<TaskDO>();

        boolean flag = true;
        try {
            TaskDO taskDO = super.getTaskService().getTaskByTaskId(taskID);
            TaskDO originTaskDO = (TaskDO)taskDO.clone();
            originTaskDOList.add(originTaskDO);

            String param1 = taskDO.getPara1().replace("\"", "");
            String[] paramInfoArray = param1.split("\\s");
            if (paramInfoArray.length != 3) {
                logger.error("param2 do not has enough info...");
                return false;
            }

            //更新task schedule info
            taskDO.setTaskObj(Const.TASK_OBJ);
            taskDO.setPara1("-id ${task_id}");
            taskDO.setPara2("-time ${unix_timestamp}");
            taskDO.setPara3("-offset " + taskDO.getOffset());

            flag = super.getTaskService().updateTask(taskDO);

            String wormholeSourceDB = "etl_mysql_reader_cfg";
            String wormholeTargetDB = null;
            wormholeTargetDB = "etl_hdfs_writer_cfg";

            //更新task wormhole info
            Map<String, String> getWormholeReaderInfoParamMap = new HashMap<String, String>();
            getWormholeReaderInfoParamMap.put("taskID", taskID.toString());
            getWormholeReaderInfoParamMap.put("queryDB", wormholeSourceDB);
            Map<Object, Object> readResult = loadService.getOldLoadCfgByID(getWormholeReaderInfoParamMap);
            Map<String, String> wormholeReaderInfo = (HashMap<String, String>) readResult.get(taskID);

            String sql = wormholeReaderInfo.get("sql");
            String regex = "(where)(\\s+)(\\w+)(\\s*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            String col = null;
            if(matcher.find()){
                col = matcher.group(3);
            }
            sql = StringUtils.replace(sql,"${cal_dt}","${CAL_YYYYMMDD_P1D}");
            sql = StringUtils.replace(sql,"${ncal_dt}","${CAL_YYYYMMDD}");

            for(String key : wormholeReaderInfo.keySet()){
                if(null == wormholeReaderInfo.get(key)){
                    wormholeReaderInfo.remove(key);
                    //wormholeReaderInfo.put(key,String.valueOf(wormholeReaderInfo.get(key)));
                }
                if(key.equals("sql")){
                    wormholeReaderInfo.put(key,sql);
                }
            }
            readWormholeDO.setTaskId(taskID);
            readWormholeDO.setType("reader");
            if(wormholeSourceDB.equals("etl_hive_reader_cfg")){
                readWormholeDO.setConnectProps("hive_bi");
            }else{
                readWormholeDO.setConnectProps(wormholeReaderInfo.get("connectprops"));
            }

            readWormholeDO.setConditionCol(col);
            readWormholeDO.setParameterMapStr(JacksonHelper.pojoToJson(wormholeReaderInfo));
            wormholeDOList.add(readWormholeDO);

            Map<String, String> getWormholeWriterInfoParamMap = new HashMap<String, String>();
            getWormholeWriterInfoParamMap.put("taskID", taskID.toString());
            getWormholeWriterInfoParamMap.put("queryDB", wormholeTargetDB);
            Map<Object, Object> writeResult = loadService.getOldLoadCfgByID(getWormholeWriterInfoParamMap);
            Map<String, String> wormholeWriterInfo = (HashMap<String, String>) writeResult.get(taskID);

            for(String key : wormholeWriterInfo.keySet()){
                if(null == wormholeWriterInfo.get(key)){
                    wormholeWriterInfo.remove(key);
                    //wormholeWriterInfo.put(key,String.valueOf(wormholeWriterInfo.get(key)));
                }
                if(key.equals("hive_table_add_partition_condition")){
                    String condi = wormholeWriterInfo.get("hive_table_add_partition_condition");
                    condi = StringUtils.replace(condi,"${cal_dt}","${CAL_YYYYMMDD_P1D}");
                    condi = StringUtils.replace(condi,"${ncal_dt}","${CAL_YYYYMMDD}");
                    wormholeWriterInfo.put("hive_table_add_partition_condition",condi);
                }
            }

            writeWormholeDO.setTaskId(taskID);
            writeWormholeDO.setType("writer");
            if(wormholeTargetDB.equals("etl_hdfs_writer_cfg")){
                writeWormholeDO.setConnectProps("hive_bi");
            }else{
                writeWormholeDO.setConnectProps(wormholeWriterInfo.get("connectprops"));
            }
            writeWormholeDO.setConditionCol(null);
            writeWormholeDO.setParameterMapStr(JacksonHelper.pojoToJson(wormholeWriterInfo));
            wormholeDOList.add(writeWormholeDO);

            flag = loadService.insertLoadCfg(wormholeDOList);

        } catch (Exception e) {
            flag =false;
            logger.error("migrate task:"+taskID+" data failure...", e);
        }
        if (!flag) {
            for (TaskDO taskDO:originTaskDOList) {
                super.getTaskService().updateTask(taskDO);
            }
            logger.info("migrate task error,task info has roll back...");
            return  false;
        }
        return true;
    }

    @Override
    public String generateDropDDL(StarShuttleParasDO starShuttleParasDO) {
        return null;
    }

    @Override
    public String generateCreateDDL(List<McColumnInfo> columnInfoList, StarShuttleParasDO starShuttleParasDO) {
        return null;
    }

    @Override
    public String generateAlterDDL(List<McColumnInfo> oldColumnList, List<McColumnInfo> newColumnList, StarShuttleParasDO starShuttleParasDO) {
        return null;
    }

    @Override
    public List<McColumnInfo> genTargetTableColumnInfo(StarShuttleParasDO para) {
        return null;
    }

    @Override
    public Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> targetColumns) {
        return null;
    }

    @Override
    public Map<String, Object> genWriterParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns) {
        return null;
    }

    @Override
    public String genReaderSql(StarShuttleParasDO parasDO, String columnStr) {
        return null;
    }

    @Override
    public Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type) {
        return null;
    }

    @Override
    public String genColumnStr(List<McColumnInfo> columns) {
        return null;
    }
}
