package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.service.impl.AutoETLServiceImpl;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mt on 2014/4/3.
 */
public class HiveHandlerTest extends TestCase {
    private ApplicationContext context;
    private AutoETLServiceImpl autoETLServiceImpl;

    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext(new String[]{"classpath:spring-applicationcontext.xml","classpath:spring-beans.xml"});
         autoETLServiceImpl = (AutoETLServiceImpl)context.getBean("autoETLServiceImpl");
    }

    public void testGenerateCreateDDL () throws Exception {
        StarShuttleParasDO parasDO = new StarShuttleParasDO();
        parasDO.setSourceDBType("mysql");
        parasDO.setSourceDBName("DianPingDW2");
        parasDO.setSourceTableName("acl_ldap_map");
        parasDO.setTargetDBType("hive");
        parasDO.setTargetDBName("bi");
        parasDO.setTargetTableType("zipper");
        parasDO.setTargetSchemaName("dpods");
        parasDO.setTargetTableName("dpods_acl_ldap_map");

        Handler targetHandler = new HiveHandler();

        List<McColumnInfo> targetColumns = new ArrayList<McColumnInfo>();

        for (int i=0;i<3;i++) {
            McColumnInfo mcColumnInfo = new McColumnInfo();
            mcColumnInfo.setColumn_name("col"+i);
            mcColumnInfo.setColumn_type("string");
            targetColumns.add(mcColumnInfo);
        }
        Const.EXTRACT_COL_ZIPPER[] array = Const.EXTRACT_COL_ZIPPER.values();
        for(int i=0;i<array.length;i++){
            McColumnInfo mcColumnInfo = new McColumnInfo();
            mcColumnInfo.setColumn_name(array[i].toString());
            mcColumnInfo.setColumn_type("string");
            targetColumns.add(mcColumnInfo);
        }

        String ddl = targetHandler.generateCreateDDL(targetColumns, parasDO);
        String[] sqls = ddl.split(";");
        Assert.assertEquals(sqls.length,5);
        Assert.assertEquals(sqls[0],"use bi");
        Assert.assertEquals(StringUtils.isBlank(sqls[2]),true);
        Assert.assertEquals(sqls[3],"use bi");
        Assert.assertEquals(sqls[1],"\n" +
                "CREATE TABLE load_acl_ldap_map(\n" +
                "col0 string,\n" +
                "col1 string,\n" +
                "col2 string\n" +
                ") ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\005' STORED AS INPUTFORMAT \"com.hadoop.mapred.DeprecatedLzoTextInputFormat\" OUTPUTFORMAT \"org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat\"");
        Assert.assertEquals(sqls[4],"\n" +
                "CREATE TABLE dpods_acl_ldap_map(\n" +
                "col0 string,\n" +
                "col1 string,\n" +
                "col2 string,\n" +
                "dw_status string,\n" +
                "valid_start_dt string,\n" +
                "valid_end_dt string,\n" +
                "dw_ins_date string,\n" +
                "hp_valid_end_dt string\n" +
                ") ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\005' STORED AS INPUTFORMAT \"com.hadoop.mapred.DeprecatedLzoTextInputFormat\" OUTPUTFORMAT \"org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat\"");
    }
}
