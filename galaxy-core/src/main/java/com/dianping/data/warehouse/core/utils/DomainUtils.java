package com.dianping.data.warehouse.core.utils;

import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;

/**
 * Created by hongdi.tang on 14-5-7.
 */
public class DomainUtils {

    public static StarShuttleParasDO processData(StarShuttleParasDO para){
        String targetTableName = para.getTargetTableName().toLowerCase().trim();
        if(para.getDateType() !=null){
            para.setDateType(para.getDateType().trim());
        }
        para.setTargetTableName(targetTableName);
        return para;
    }
}
