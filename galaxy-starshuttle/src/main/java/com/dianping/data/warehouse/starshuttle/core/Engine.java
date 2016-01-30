package com.dianping.data.warehouse.starshuttle.core;

import com.dianping.data.warehouse.core.common.DateConst;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.service.LoadCfgService;
import com.dianping.data.warehouse.starshuttle.common.Const;
import com.dianping.data.warehouse.starshuttle.common.GlobalResource;
import com.dianping.data.warehouse.starshuttle.model.Parameter;
import com.dianping.data.warehouse.starshuttle.service.ProcessExecuter;
import com.dianping.data.warehouse.starshuttle.service.impl.ProcessExecuterImpl;
import com.dianping.data.warehouse.starshuttle.service.impl.XMLConverterImpl;
import com.dianping.data.warehouse.starshuttle.utils.StringUtils;
import org.apache.commons.cli.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-4.
 */
@Service
public class Engine {
    @Resource(name="loadCfgServiceImpl")
    private LoadCfgService loadService;

    @Resource(name="XMLConverterImpl")
    private XMLConverterImpl converter;

    private static Logger logger = LoggerFactory.getLogger(Engine.class);

    public static Parameter parseParmeter(String[] args){
        Options options = new Options();
        Const.PARAMETERS[] paras = Const.PARAMETERS.values();
        for (int i =0 ;i<paras.length;i++){
            options.addOption(paras[i].toString(),true,paras[i].getDesc());
        }
        options.addOption("src",true,"zipper source table");
        options.addOption("target",true,"zipper target table");
        options.addOption("col",true,"zipper column");

        Integer taskID = null;
        Long time = null;
        String offset = null;
        String column = null;
        String src = null;
        String target = null;

        CommandLineParser parser = new PosixParser();
        try{
            CommandLine cmd = parser.parse(options,args);
            if(!cmd.hasOption("id") || !cmd.hasOption("time") || !cmd.hasOption("offset")){
                throw new NullPointerException("you must enter \"-id\" \"-time\" \"-offset\" option ");
            }

            taskID = Integer.valueOf(cmd.getOptionValue(Const.PARAMETERS.id.toString()));
            time = Long.valueOf(cmd.getOptionValue(Const.PARAMETERS.time.toString()));
            offset = cmd.getOptionValue(Const.PARAMETERS.offset.toString());
            column = cmd.getOptionValue("col");
            src = cmd.getOptionValue("src");
            target = cmd.getOptionValue("target");

            Parameter parameter = new Parameter();
            parameter.setId(taskID);
            parameter.setTime(time *1000);
            parameter.setOffset(offset);
            parameter.setCol(column);
            parameter.setSrc(src);
            parameter.setTarget(target);
            return parameter;
        }catch (ParseException e){
            throw new RuntimeException("occur error in the input parameters",e);
        }
    }

    public static void main(String[] args){
        Parameter para = null;
        try{
             para = Engine.parseParmeter(args);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            System.exit(Const.RET_PARA_ERROR);
        }

        ApplicationContext context = new ClassPathXmlApplicationContext(
            new String[]{"spring-applicationcontext.xml"}
        ) ;
        Engine engine = (Engine)context.getBean("engine");
        System.exit(engine.execute(para));
    }

    public Integer execute(Parameter para){
        //读取数据
        List<WormholeDO> wormholes = loadService.getLoadCfgListByID(para.getId());

        Map<String,String> dateVars = new HashMap<String, String>();
        for(DateConst.BATCH_CAL_VARS var :DateConst.BATCH_CAL_VARS.values()){
            String key = var.toString();
            String dateVar = GalaxyDateUtils.transferVariable(key, para.getTime(), para.getOffset());
            dateVars.put(key, dateVar);
        }
        //解析参数
        Document doc = converter.convert(wormholes);
        String content = StringUtils.replaceVariables(doc.asXML(),dateVars);
        String fileName = GlobalResource.ENV_MAP.get("XML_HOME").concat(File.separator).concat(String.valueOf(para.getId())).concat(".xml");
        try{
            this.writeXMLFile(content, fileName);
        }catch(IOException e1){
            logger.error("write xml file error",e1);
            return Const.RET_INTERNAL_ERROR;
        }catch(DocumentException e2){
            logger.error("parse json error",e2);
            return Const.RET_INTERNAL_ERROR;
        }
        return executeWormhole(para,fileName,dateVars);
    }

    private int executeWormhole(Parameter para,String fileName,Map<String,String> dateVars){
        ProcessExecuter executer = new ProcessExecuterImpl();
        List<String> commands = new ArrayList<String>();
        commands.add(GlobalResource.ENV_MAP.get("WORMHOLE_SHELL"));
        commands.add(fileName);
        try{
            if(org.apache.commons.lang.StringUtils.isNotBlank(para.getSrc()) &&
                    org.apache.commons.lang.StringUtils.isNotBlank(para.getTarget()) &&
                    org.apache.commons.lang.StringUtils.isNotBlank(para.getCol())){
                int rtn = executer.execute(commands);
                if(rtn != Const.RET_SUCCESS){
                    return rtn;
                }else{
                    List<String> zipperCmds = new ArrayList<String>();
                    zipperCmds.add("/bin/sh");
                    zipperCmds.add(GlobalResource.ENV_MAP.get("ZIPPER_SHELL"));
                    zipperCmds.add("-t");
                    zipperCmds.add(para.getSrc());
                    zipperCmds.add("-x");
                    zipperCmds.add(para.getTarget());
                    zipperCmds.add("-k");
                    zipperCmds.add(para.getCol());
                    zipperCmds.add("-d");
                    zipperCmds.add(dateVars.get("YYYYMMDD8"));
                    StringBuilder zipperShell = new StringBuilder(GlobalResource.ENV_MAP.get("ZIPPER_SHELL"));
                    for(String cmd : zipperCmds){
                        zipperShell.append(" ").append(cmd);
                    }
                    System.out.println("zipperCmds: "+ zipperCmds);
                    return executer.execute(zipperCmds);
                }
            }
            return executer.execute(commands);
        }catch(Exception e){
            logger.error("starshuttle execute error",e);
            return Const.RET_INTERNAL_ERROR;
        }
    }

    private void writeXMLFile(String content, String fileName) throws IOException,DocumentException{
        Document doc = DocumentHelper.parseText(content);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter output;
        format.setEncoding("UTF-8");
        output = new XMLWriter(new FileWriter(fileName), format);
        output.write(doc);
        output.close();
    }

}
