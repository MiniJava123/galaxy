package com.dianping.data.warehouse.starshuttle.core;

import com.dianping.data.warehouse.core.common.DateConst;
import com.dianping.data.warehouse.core.service.impl.LoadCfgServiceImpl;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.service.LoadCfgService;
import com.dianping.data.warehouse.starshuttle.common.GlobalResource;
import com.dianping.data.warehouse.starshuttle.common.MockData;
import com.dianping.data.warehouse.starshuttle.model.Parameter;
import com.dianping.data.warehouse.starshuttle.service.impl.XMLConverterImpl;
import com.dianping.data.warehouse.starshuttle.utils.StringUtils;
import junit.framework.Assert;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-19.
 */
public class EngineTest {
    private static ApplicationContext context;

    @BeforeClass
    public static void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("spring-applicationcontext.xml");
        loadService = (LoadCfgServiceImpl)context.getBean("loadCfgServiceImpl");
        converter = (XMLConverterImpl)context.getBean("XMLConverterImpl");
        engine = (Engine)context.getBean("engine");
    }

    //@Resource(name="loadCfgServiceImpl")
    private static LoadCfgService loadService;

    private static XMLConverterImpl converter;

    private static Engine engine;

    @Test
    public void testParseParmeter() throws Exception {
        String[] args = new String[]{"-id", "312312", "-offset", "D1", "-time", "12341234"};
        Parameter para = Engine.parseParmeter(args);
        Assert.assertEquals(Integer.valueOf(args[1]),para.getId());
        Assert.assertEquals(args[3],para.getOffset());
        Assert.assertEquals(para.getTime(),Long.valueOf(args[5]));
    }

    public void testMain() throws Exception {

    }

    @Test
    public void testExecute() throws Exception {
        System.out.println(System.currentTimeMillis());
        Parameter parameter = new Parameter();
        parameter.setTime(System.currentTimeMillis());
        parameter.setOffset("D0");
       // parameter.setId(10656);
        //parameter.setId(10660);
        //parameter.setId(10663);
        parameter.setId(10683);
        engine.execute(parameter);
    }

    @Test
    public void testGetLoadCfg(){
        List<WormholeDO> wormholes = this.loadService.getLoadCfgListByID(10506);
        Assert.assertEquals(wormholes.size(),2);
    }

    @Test
    public void testWriteFile()  throws Exception{
        Parameter para = new Parameter();
        para.setTime(System.currentTimeMillis());
        para.setOffset("D0");
        para.setId(10001);

        Map<String,String> dateVars = new HashMap<String, String>();
        for(DateConst.BATCH_CAL_VARS var :DateConst.BATCH_CAL_VARS.values()){
            String key = var.toString();
            String dateVar = GalaxyDateUtils.transferVariable(key, para.getTime(), para.getOffset());
            dateVars.put(key, dateVar);
            System.out.println(key +"->" + dateVar);
        }

        List<WormholeDO> wormholes = MockData.mockWormholeList();
        Document doc = converter.convert(wormholes);
        String content = StringUtils.replaceVariables(doc.asXML(), dateVars);
        Document doc1 = DocumentHelper.parseText(content);

        //建表
        String fileName = GlobalResource.ENV_MAP.get("XML_HOME").concat(File.separator).concat(String.valueOf(para.getId())).concat(".xml");
        System.out.println(fileName);
        //FileUtils.writeStringToFile(new File(fileName), content);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter output;
        format.setEncoding("UTF-8");
        output = new XMLWriter(new FileWriter(fileName), format);
        output.write(doc1);
        output.close();
    }

    @Test
    public void testDateVars(){
        Parameter para = new Parameter();
        para.setTime(System.currentTimeMillis());
        para.setOffset("D0");

        Map<String,String> dateVars = new HashMap<String, String>();
        for(DateConst.BATCH_CAL_VARS var :DateConst.BATCH_CAL_VARS.values()){
            String key = var.toString();
            String dateVar = GalaxyDateUtils.transferVariable(key, para.getTime(), para.getOffset());
            dateVars.put(key, dateVar);
            System.out.println(key +"->" + dateVar);
        }

        Assert.assertNotNull(dateVars);

    }
}
