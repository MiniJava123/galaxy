package com.dianping.data.warehouse.core.domain;

import com.dianping.data.warehouse.core.utils.ValidatorUtils;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import junit.framework.TestCase;

/**
 * Created by hongdi.tang on 14-2-27.
 */
public class WormholeDOTest extends TestCase {
    //public ApplicationContext context;

    public WormholeDO entity;
    public void setUp() throws Exception {
        //context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
        entity = new WormholeDO();
        entity.setTaskId(null);
        entity.setType("asefaes");
        entity.setConnectProps("aesfse");
    }

    public void testGetTaskId() throws Exception {
          System.out.println(entity.getTaskId());
        try{
            //Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            ValidatorUtils.validateModel(entity);
            //ValidatorUtils.validateProperties(entity,"type");
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
