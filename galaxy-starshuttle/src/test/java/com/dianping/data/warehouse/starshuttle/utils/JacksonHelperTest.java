package com.dianping.data.warehouse.starshuttle.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-4-11.
 */
public class JacksonHelperTest {
    @Test
    public void testJsonToPojo() throws Exception {
//        Map<String,String> map2 = new HashMap<String, String>();
//        map2.put("test1","\\n");
//        map2.put("test2","\\005");
//        String jsonStr = "{\"test1\":\"\\\\n\",\"test2\":\"\\\\005\"}";
//        //Gson gson = new Gson();
//        Map<String,String> map1 = JacksonHelper2.jsonToPojo(jsonStr, HashMap.class);
//        String jsonStr1 = JacksonHelper2.pojoToJson(map2);
//        System.out.println(jsonStr1);
//        //System.out.println(map);
//        System.out.println(map1);

        //System.out.println(JacksonHelper2.jsonToPojo(jsonStr1,HashMap.class));

        String tmp =" {\"needSplit\":\"false\",\"sql\":\"select task_id,id,plugin,path,username,password,sql,mode,datadir,reducenumber,concurrency,now() from dpods_etl_hive_reader_cfg\",\"concurrency\":\"10\",\"encoding\":\"utf-8\",\"blockSize\":\"10000\",\"connectProps\":\"DianPingDW2\",\"plugin\":\"mysqlreader\"}";
        Map<String, String> parameterMap = JacksonHelper.jsonToPojo(tmp, HashMap.class);
        HashMap<String,String> map = JacksonHelper.jsonToPojo(tmp,Map.class);
        System.out.println(map);
    }
}
