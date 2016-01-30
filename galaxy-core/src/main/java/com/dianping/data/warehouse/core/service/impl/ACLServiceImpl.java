package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.core.service.ACLService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @Author <a href="mailto:tsensue@gmail.com">dishu.chen</a>
 * 14-3-12.
 */
public class ACLServiceImpl implements ACLService {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(ACLServiceImpl.class);
    private String aclUrl;

    @Override
    public UserDO getUserByToken(String token) {
        String url = aclUrl + "/"+"getLoginUserInfo?token=" + token;
        String context = null;
        try {
            context = Jsoup.connect(url).execute().body();
        } catch (IOException e) {
            logger.error("Call ACLService failure", e);
        }

        return StringUtils.isNotBlank(context) ? conver(context) : null;
    }

    private UserDO conver(String context) {
        UserDO user = new UserDO();
        JSONObject jsonObj = JSONObject.fromObject(context);
        JSONObject var1 = jsonObj.getJSONObject("msg");
        user.setLoginId(var1.getInt("loginID"));
        user.setEmployeeId(var1.getString("employee_id"));
        user.setEmployeeName(var1.getString("employee_name"));
        user.setEmployeeEmail(var1.getString("employee_email"));
        user.setEmployPinyin(StringUtils.substringBefore(user.getEmployeeEmail(),"@"));

        return user;
    }

    public void setAclUrl(String aclUrl) {
        this.aclUrl = aclUrl;
    }
}
