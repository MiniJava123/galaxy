package com.dianping.data.warehouse.starshuttle.utils;

import com.dianping.data.warehouse.core.dao.BaseDAO;
import com.dianping.data.warehouse.core.dao.impl.BaseDAOImpl;
import com.dianping.data.warehouse.core.dao.impl.ExceptionAlertSqlMap;
import com.dianping.data.warehouse.domain.model.ExceptionAlertDO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * Created by Sunny on 14-7-18.
 */
public class ExceptionAnalyze {

    private static Map<String, ExceptionAlertDO> alertMap = new HashMap<String, ExceptionAlertDO>();

    @Resource(name = "baseDAOImpl")
    private BaseDAO dao;

    private ApplicationContext context;

    public void setBaseDAO(BaseDAO dao) {
        this.dao = dao;
    }

    public ExceptionAnalyze() {
        context = new ClassPathXmlApplicationContext(new String[]{"spring-applicationcontext.xml", "spring-beans.xml"});
        dao = (BaseDAOImpl) context.getBean("baseDAOImpl");
        List<ExceptionAlertDO> exceptionAlertDOs = dao.getByQuery("wormhole", ExceptionAlertSqlMap.STATEMENT_GET_EXCEPTIONALERTS_BY_PRODUCT);
        for (ExceptionAlertDO exceptionAlertDO : exceptionAlertDOs) {
            String reason = exceptionAlertDO.getDescription();
            alertMap.put(reason, exceptionAlertDO);
        }
    }

    /**
     * 逐行分析输入流并返回匹配的错误类型
     */
    public ExceptionAlertDO analyze(InputStream is) {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                ExceptionAlertDO alert = analyzeLine(line);
                if (alert != null)
                    return alert;
            }
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * 分析一行日志并返回匹配到的错误类型
     */
    private ExceptionAlertDO analyzeLine(String line) {
        Iterator iter = alertMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (line.toLowerCase().contains(key))
                return (ExceptionAlertDO) entry.getValue();
        }
        return null;
    }

    public static void main(String args[]) {
        File file = new File("/Users/Sunny/Desktop/test.txt");
        try {
            FileInputStream in = new FileInputStream(file);
            new ExceptionAnalyze().analyze(in);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
