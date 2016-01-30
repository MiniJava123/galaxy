//package com.dianping.data.warehouse.core.handler.impl;
//
//import com.dianping.data.warehouse.core.handler.Handler;
//import com.dianping.data.warehouse.domain.McColumnInfo;
//import junit.framework.TestCase;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by shanshan.jin on 14-4-1.
// */
//public class HandlerTest extends TestCase {
//    public ApplicationContext context;
//    public Map<String,Handler> handlerMap;
//
//    public void setUp(){
//        context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml","spring-beans.xml"});
//        handlerMap = (Map<String,Handler>) context.getBean("handlerMap");
//    }
//    public void testGenerateAlterDDL1() throws Exception {
//        Handler handler = handlerMap.get("hive");
//        McColumnInfo column_1 = new McColumnInfo();
//        column_1.setColumn_rn(1);
//        column_1.setColumn_name("id");
//        column_1.setColumn_type("int");
//        McColumnInfo column_2 = new McColumnInfo();
//        column_2.setColumn_rn(2);
//        column_2.setColumn_name("name");
//        column_2.setColumn_type("string");
//        McColumnInfo column_3 = new McColumnInfo();
//        column_3.setColumn_rn(3);
//        column_3.setColumn_name("score");
//        column_3.setColumn_type("double");
//        List<McColumnInfo> oldColumns = new ArrayList<McColumnInfo>();
//        List<McColumnInfo> newColumns = new ArrayList<McColumnInfo>();
//        oldColumns.add(column_1);
//        oldColumns.add(column_2);
//        newColumns.add(column_1);
//        newColumns.add(column_2);
//        newColumns.add(column_3);
//        //String ddl = handler.generateAlterDDL(oldColumns,newColumns,"jinss_test_add");
//        String ddl = null;
//        System.out.print(ddl);
//        String expect = "use bi;\nalter table jinss_test_add ADD COLUMNS (score double);\n";
//        assertEquals(expect,ddl);
//
//    }
//
//    public void testGenerateAlterDDL2() throws Exception {
//        Handler handler = handlerMap.get("greenplum");
//        McColumnInfo column_1 = new McColumnInfo();
//        column_1.setColumn_rn(1);
//        column_1.setColumn_name("id");
//        column_1.setColumn_type("int");
//        McColumnInfo column_2 = new McColumnInfo();
//        column_2.setColumn_rn(2);
//        column_2.setColumn_name("name");
//        column_2.setColumn_type("string");
//        McColumnInfo column_3 = new McColumnInfo();
//        column_3.setColumn_rn(3);
//        column_3.setColumn_name("score");
//        column_3.setColumn_type("double");
//        List<McColumnInfo> oldColumns = new ArrayList<McColumnInfo>();
//        List<McColumnInfo> newColumns = new ArrayList<McColumnInfo>();
//        oldColumns.add(column_1);
//        oldColumns.add(column_2);
//        newColumns.add(column_1);
//        newColumns.add(column_2);
//        newColumns.add(column_3);
//        //String ddl = handler.generateAlterDDL(oldColumns,newColumns,"jinss_test_add");
//        String ddl = null;
//        System.out.print(ddl);
//        String expect = "alter table jinss_test_add ADD COLUMN score double;\n";
//        assertEquals(expect,ddl);
//    }
//
//    public void testGenerateAlterDDL3() throws Exception {
//        Handler handler = handlerMap.get("hive");
//        McColumnInfo column_1 = new McColumnInfo();
//        column_1.setColumn_rn(1);
//        column_1.setColumn_name("id");
//        column_1.setColumn_type("int");
//        McColumnInfo column_2 = new McColumnInfo();
//        column_2.setColumn_rn(2);
//        column_2.setColumn_name("name");
//        column_2.setColumn_type("string");
//        McColumnInfo column_3 = new McColumnInfo();
//        column_3.setColumn_rn(3);
//        column_3.setColumn_name("score");
//        column_3.setColumn_type("double");
//        McColumnInfo column_3_old = new McColumnInfo();
//        column_3_old.setColumn_rn(3);
//        column_3_old.setColumn_name("score1");
//        column_3_old.setColumn_type("double");
//        List<McColumnInfo> oldColumns = new ArrayList<McColumnInfo>();
//        List<McColumnInfo> newColumns = new ArrayList<McColumnInfo>();
//        oldColumns.add(column_1);
//        oldColumns.add(column_2);
//        oldColumns.add(column_3_old);
//        newColumns.add(column_1);
//        newColumns.add(column_2);
//        newColumns.add(column_3);
//        //String ddl = handler.generateAlterDDL(oldColumns,newColumns,"jinss_test_add");
//        String ddl = null;
//        System.out.print(ddl);
//        String expect = "use bi;\n" +
//                "alter table jinss_test_add change score1 score_tmp double;\n" +
//                "alter table jinss_test_add change score_tmp score double;\n";
//        assertEquals(expect,ddl);
//    }
//
//    public void testGenerateAlterDDL4() throws Exception {
//        Handler handler = handlerMap.get("greenplum");
//        McColumnInfo column_1 = new McColumnInfo();
//        column_1.setColumn_rn(1);
//        column_1.setColumn_name("id");
//        column_1.setColumn_type("int");
//        McColumnInfo column_2 = new McColumnInfo();
//        column_2.setColumn_rn(2);
//        column_2.setColumn_name("name");
//        column_2.setColumn_type("string");
//        McColumnInfo column_3 = new McColumnInfo();
//        column_3.setColumn_rn(3);
//        column_3.setColumn_name("score");
//        column_3.setColumn_type("double");
//        McColumnInfo column_3_old = new McColumnInfo();
//        column_3_old.setColumn_rn(3);
//        column_3_old.setColumn_name("score1");
//        column_3_old.setColumn_type("double");
//        List<McColumnInfo> oldColumns = new ArrayList<McColumnInfo>();
//        List<McColumnInfo> newColumns = new ArrayList<McColumnInfo>();
//        oldColumns.add(column_1);
//        oldColumns.add(column_2);
//        oldColumns.add(column_3_old);
//        newColumns.add(column_1);
//        newColumns.add(column_2);
//        newColumns.add(column_3);
//        //String ddl = handler.generateAlterDDL(oldColumns,newColumns,"jinss_test_add");
//        String ddl = null;
//        System.out.print(ddl);
//        String expect = "alter table jinss_test_add RENAME COLUMN score1 to score;\n";
//        assertEquals(expect,ddl);
//    }
//
//    public void testGenerateAlterDDL5() throws Exception {
//        Handler handler = handlerMap.get("greenplum");
//        McColumnInfo column_1 = new McColumnInfo();
//        column_1.setColumn_rn(1);
//        column_1.setColumn_name("id");
//        column_1.setColumn_type("int");
//        McColumnInfo column_2 = new McColumnInfo();
//        column_2.setColumn_rn(2);
//        column_2.setColumn_name("name");
//        column_2.setColumn_type("string");
//        McColumnInfo column_3 = new McColumnInfo();
//        column_3.setColumn_rn(3);
//        column_3.setColumn_name("score");
//        column_3.setColumn_type("double");
//        McColumnInfo column_3_old = new McColumnInfo();
//        column_3_old.setColumn_rn(3);
//        column_3_old.setColumn_name("score");
//        column_3_old.setColumn_type("int");
//        List<McColumnInfo> oldColumns = new ArrayList<McColumnInfo>();
//        List<McColumnInfo> newColumns = new ArrayList<McColumnInfo>();
//        oldColumns.add(column_1);
//        oldColumns.add(column_2);
//        oldColumns.add(column_3_old);
//        newColumns.add(column_1);
//        newColumns.add(column_2);
//        newColumns.add(column_3);
//        //String ddl = handl7er.generateAlterDDL(oldColumns,newColumns,"jinss_test_add");
//        String ddl = null;
//        System.out.print(ddl);
//        String expect = "alter table jinss_test_add ALTER COLUMN score TYPE double;\n";
//        assertEquals(expect,ddl);
//    }
//}
