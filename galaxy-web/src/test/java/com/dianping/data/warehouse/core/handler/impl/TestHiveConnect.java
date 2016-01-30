package com.dianping.data.warehouse.core.handler.impl;

import java.util.Random;

/**
 * Created by hongdi.tang on 14-3-16.
 */
public class TestHiveConnect {
    public static void main(String[] args){
    //        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
    //        AutoETLService service = (AutoETLService)context.getBean("autoETLServiceImpl");
    //        service.generateAutoETLTask(null,null);
        StringBuilder builder = new StringBuilder();
        builder.append("use bi;");
        String targetTable = "test" ;
        builder.append("alter table ").append(targetTable).append(" rename to ")
                .append(targetTable).append("_drop").append(Math.abs(new Random().nextInt()%100));
        System.out.println(builder.toString());

    }
}
