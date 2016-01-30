package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import org.junit.Test;

/**
 * Created by hongdi.tang on 14-4-15.
 */
public class GreenPlumHandlerTest {
    @Test
    public void testGenerateDropDDL() throws Exception {
        GreenPlumHandler service = new GreenPlumHandler();
        StarShuttleParasDO para = new StarShuttleParasDO();
        para.setTargetTableName("test");
        System.out.println(service.generateDropDDL(para));
    }


    @Test
    public void testGenWriterParas() throws Exception{
        String targetTable = "test";
        String beginStr = "${CAL_YYYYMMDD_P1D}";
        String endStr = "${CAL_YYYYMMDD}";
        StringBuilder preSql = new StringBuilder();
        preSql.append("alter table ").append(targetTable).append(" drop partition if exists p").append(beginStr.replace("YYYYMMDD","YYYYMMDD8")).append(";")
                .append("alter table ").append(targetTable).append(" ADD PARTITION " ).append(" p").append(beginStr.replace("YYYYMMDD","YYYYMMDD8"))
                .append(" START (' ").append(beginStr.replace("YYYYMMDD","YYYYMMDD8")).append("'::date)")
                .append(" END ('").append(endStr.replace("YYYYMMDD", "YYYYMMDD8")).append("'::date)")
                .append(" WITH (APPENDONLY=true, COMPRESSLEVEL=6, ORIENTATION=column, COMPRESSTYPE=zlib, OIDS=FALSE)");
        System.out.println(preSql.toString());
    }
}
