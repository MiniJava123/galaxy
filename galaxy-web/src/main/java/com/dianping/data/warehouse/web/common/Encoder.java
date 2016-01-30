package com.dianping.data.warehouse.web.common;

/**
 * Created by Sunny on 14-6-3.
 */

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 14-6-3.
 */

public class Encoder {

    static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

    public static String encode(String content) {
        try {
            return new String(content.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decode(String s) {
        Matcher m = reUnicode.matcher(s);
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            m.appendReplacement(sb,
                    Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

}