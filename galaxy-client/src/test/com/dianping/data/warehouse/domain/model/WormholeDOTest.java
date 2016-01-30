package com.dianping.data.warehouse.domain.model;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-24.
 */
public class WormholeDOTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testConvertMap() throws Exception {
        WormholeDO config = new WormholeDO();
        config.setParameterMap("{\"12312\":\"123123234234\",\"sefsef\":\"123123234234\",\"tt\":\"123123234234\",\"efasef\":\"123123234234\"}");
       // System.out.println(config.convertMap());


    }

    public void testConvertJsonStr() throws Exception {
        WormholeDO config = new WormholeDO();
        Map<String,String> map = new HashMap<String,String>();
        map.put("12312","123123234234");
        map.put("tt","123123234234")    ;
        map.put("efasef","123123234234") ;
        map.put("sefsef","123123234234")  ;
        System.out.println(config.convertJsonStr(map));
        config.setParameterMap("{\"12312\":\"123123234234\",\"sefsef\":\"123123234234\",\"tt\":\"123123234234\",\"efasef\":\"123123234234\"}");
        System.out.println(config.convertMap());
    }
}
