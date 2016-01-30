package com.dianping.data.warehouse.starshuttle.common;

import com.dianping.data.warehouse.core.common.GlobalResources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hongdi.tang on 14-4-22.
 */
public class GlobalResource {
    private static final String conf = "env.properties";
    public static Map<String,String> ENV_MAP = new HashMap<String, String>();

    static{
        try{
            Properties properties = new Properties();
            properties.load(GlobalResources.class.getClassLoader().getResourceAsStream(conf));
            for (Map.Entry<Object,Object> entry : properties.entrySet()){
                ENV_MAP.put((String)entry.getKey(),(String)entry.getValue());
            }
        }catch(IOException e){
            throw new RuntimeException("load env file error",e);
        }
    }

}
