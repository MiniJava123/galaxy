package com.dianping.data.warehouse.web.filter;

import com.dianping.data.warehouse.core.utils.LionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sunny on 14/11/13.
 */
public class LoginFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        boolean isSkip = false;
        String path = httpServletRequest.getRequestURI();
        String skipUrlStr = LionUtils.getValue("galaxy.skipUrl");
        String[] skipUrls = skipUrlStr.split(",");
        for (String skipUrl : skipUrls) {
            if (path.endsWith(skipUrl)) {
                isSkip = true;
                break;
            }
        }
        if (isSkip) {
            try {
                httpServletRequest.getRequestDispatcher(path).forward(httpServletRequest, httpServletResponse);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
