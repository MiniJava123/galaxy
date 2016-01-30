package com.dianping.data.warehouse.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongdi.tang on 14-4-28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-applicationcontext.xml",
        "classpath:spring-beans.xml"})
public class MigrateServiceTest {

    @Resource(name="migrateService")
    private MigrateService serivce;

    @Test
    public void testMigrateTaskData() throws Exception {
        serivce.migrateMysql2Hive(13232);
    }

    @Test
    public void testRegexTest() throws Exception{
        String regex = "(where)(\\s)+(\\w+)(\\s)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("select * from XXX where  mmm  = 'sefsef'");
        while(matcher.find()){

            System.out.println("1"+matcher.group()+"2");
            System.out.println("1"+matcher.group(1));
            System.out.println("1"+matcher.group(2));
            System.out.println("1"+matcher.group(3));
            System.out.println("1"+matcher.group(4));
            System.out.println("1" + matcher.group(5));
        }
    }
}
