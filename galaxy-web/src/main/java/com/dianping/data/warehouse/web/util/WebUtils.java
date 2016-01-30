package com.dianping.data.warehouse.web.util;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.core.service.ACLService;
import com.dianping.data.warehouse.core.service.impl.ACLServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class WebUtils {

    private static ACLService aclService = new ACLServiceImpl();

	public static ModelAndView getErrorModelAndView(Map<String, String> messages) {
		ModelAndView result = new ModelAndView("error");
		result.addAllObjects(messages);
		return result;
	}
	
	public static ModelAndView getErrorModelAndViewWithSingleMessage(String key, String value) {
		ModelAndView result = new ModelAndView("error");
		result.addObject(key, value);
		return result;
	}
	
	/**
	 * 如果为空，则返回defaultValue
	 * @param key
	 * @param defaultValue
	 * @param request
	 * @return
	 */
	public static String getParameter(String key, String defaultValue, HttpServletRequest request) {
        return StringUtils.trim(
                StringUtils.isBlank(request.getParameter(key)) ? defaultValue :request.getParameter(key));
	}
	
	public static String getParameter(String key, HttpServletRequest request) {
        return WebUtils.getParameter(key, null, request);
    }

    public static String getMethod(HttpServletRequest request) {
        return StringUtils.trim(request.getMethod());
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }

    public static UserDO getUserFromCookie(String loginID,String employee_pinyin,String tk) {
        UserDO user = new UserDO();
        if (StringUtils.isBlank(loginID)||StringUtils.isBlank(employee_pinyin)) {
            if (StringUtils.isNotBlank(tk)) {
                user = aclService.getUserByToken(tk);
            } else {
                return null;
            }
        } else {
            user.setLoginId(new Integer(loginID));
            user.setEmployPinyin(employee_pinyin);
        }
        return user;
    }
}
