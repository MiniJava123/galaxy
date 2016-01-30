package com.dianping.data.warehouse.starshuttle.utils;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongdi.tang on 14-3-10.
 */
public class StringUtils {
    private static final Logger logger = Logger.getLogger(StringUtils.class);

    private static final String VARIABLE_PATTERN = "(\\$)\\{(\\w+)\\}";

    private StringUtils(){
    }

    public static String replaceVariables(String text,Map<String,String> variableMap) {
        Pattern pattern = Pattern.compile(VARIABLE_PATTERN);
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()){
            logger.info("replace " + matcher.group(2) +
                    " with " + variableMap.get(matcher.group(2).replace("CAL_","")));

            text = org.apache.commons.lang.StringUtils.replace(text, matcher.group(),
                    org.apache.commons.lang.StringUtils.defaultString(variableMap.get(matcher.group(2).replace("CAL_","")), matcher.group()));
        }
        return text;
    }
}
