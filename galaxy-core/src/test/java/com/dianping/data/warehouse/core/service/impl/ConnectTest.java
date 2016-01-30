package com.dianping.data.warehouse.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hongdi.tang on 14-4-17.
 */
public class ConnectTest {
    public static void main(String[] args) throws Exception{
//        String url = "jdbc:postgresql://10.1.21.57:5432/beta58?useUnicode=true&characterEncoding=utf-8";
//        String driver = "org.postgresql.Driver";
//        String username = "bi";
//        String pass = "bi3da93da039dbi";
//        Connection ret = null;
//        try {
//            Class.forName(driver);
//            ret = DriverManager.getConnection(url,username, pass);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdf.parse("2014-05-01");
        System.out.println(d.getTime());
    }
}
