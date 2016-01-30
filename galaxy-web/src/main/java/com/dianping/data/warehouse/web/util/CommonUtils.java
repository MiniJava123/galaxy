package com.dianping.data.warehouse.web.util;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Sunny on 14-8-13.
 */
public class CommonUtils {

    /**
     * 检查参数是够全由数字和逗号组成
     */
    private static boolean isTaskId(String taskNameOrId) {
        String ids[] = taskNameOrId.split(",");
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i].trim();
            if (!isAllDigitals(id))
                return false;
        }
        return true;
    }

    /**
     * 检查参数是够全由数字组成
     */
    private static boolean isAllDigitals(String taskId) {
        for (int i = 0; i < taskId.length(); i++)
            if (taskId.charAt(i) < '0' || taskId.charAt(i) > '9')
                return false;
        return true;
    }

    /**
     * 检查参数是否是task name
     */
    public static boolean isTaskName(String taskNameOrId) {
        if (StringUtils.isBlank(taskNameOrId))
            return true;
        return !isTaskId(taskNameOrId);
    }


}
