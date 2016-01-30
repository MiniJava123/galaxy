package com.dianping.data.warehouse.starshuttle.service.impl;

import com.dianping.data.warehouse.core.service.impl.LoadCfgServiceImpl;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.starshuttle.service.WormholeConverter;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.dom4j.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by hongdi.tang on 14-3-19.
 */
public class XMLConverterImplTest extends TestCase {
    public ApplicationContext context;

    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext(new String[] {"spring-applicationcontext.xml",
                "spring-beans.xml"});
    }


    public void testConvert() throws Exception {
        LoadCfgServiceImpl loadService = (LoadCfgServiceImpl)context.getBean("loadCfgServiceImpl");
        WormholeConverter converter = (XMLConverterImpl)context.getBean("XMLConverterImpl");
        List<WormholeDO> list = loadService.getLoadCfgListByID(10506);
        //JacksonHelper.jsonToPojo(starShuttleDOJson.toString(), new StarShuttleDO());
        Document doc = (Document) converter.convert(list);
        Assert.assertNotNull("document is not null",doc);
    }
}
