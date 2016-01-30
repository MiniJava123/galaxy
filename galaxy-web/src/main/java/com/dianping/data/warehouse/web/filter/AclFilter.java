package com.dianping.data.warehouse.web.filter;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.web.common.Const;
import com.dianping.data.warehouse.web.util.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by mt on 2014/5/12.
 */
public class AclFilter extends HttpServlet implements Filter {

    private static final long serialVersionUID = -3842485453989121687L;
    private static final Log logger = LogFactory.getLog(AclFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        UserDO user = getUserDO(request);
        if (user == null) {
            logger.error("get user info failure...");
            return;
        } else {
            request.setAttribute(Const.REQUEST_ATTR_USER, user);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private UserDO getUserDO(HttpServletRequest request) {
        try {
            String userStr = request.getRemoteUser();
            String[] paras = userStr.split("\\|");
            UserDO userDO = new UserDO();
            userDO.setEmployPinyin(paras[0]);
            userDO.setLoginId(Integer.parseInt(paras[1]));
            userDO.setEmployeeId(paras[2]);
            userDO.setEmployeeName(paras[3]);
            userDO.setEmployeeEmail(paras[0] + "@dianping.com");
            return userDO;
        } catch (Exception e) {
            return null;
        }
    }
}
