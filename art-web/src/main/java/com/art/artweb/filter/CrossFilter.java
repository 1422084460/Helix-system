package com.art.artweb.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter("/")
public class CrossFilter implements Filter {
    private static final String ORIGIN = "Origin";

    private static final String REFERER = "Referer";

    private static final String TRUE = "true";

    private static final String CACHE_86400 = "86400";

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    private static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

    private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    @Override
    public void init(FilterConfig filterConfig) {
        //do something
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 解决跨域请求问题
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String origin = req.getHeader (ORIGIN);
        if (origin == null) {
            origin = req.getHeader (REFERER);
        }
        // 允许指定域访问跨域资源
        setHeader (resp, ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        // 允许客户端携带跨域cookie，此时origin值不能为“*”，只能为指定单一域名
        setHeader (resp, ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE);
        if (RequestMethod.OPTIONS.toString ().equals (req.getMethod ())) {
            String allowMethod = req.getHeader (ACCESS_CONTROL_REQUEST_METHOD);
            String allowHeaders = req.getHeader (ACCESS_CONTROL_REQUEST_HEADERS);
            // 浏览器缓存预检请求结果时间,单位:秒
            setHeader (resp, ACCESS_CONTROL_MAX_AGE, CACHE_86400);
            // 允许浏览器在预检请求成功之后发送的实际请求方法名
            setHeader (resp, ACCESS_CONTROL_ALLOW_METHODS, allowMethod);
            // 允许浏览器发送的请求消息头
            setHeader (resp, ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
            return;
        }
        chain.doFilter (request, response);
    }

    private void setHeader(HttpServletResponse resp, String key, String value) {
        resp.setHeader (key, value);
    }

    @Override
    public void destroy() {
        //do someThing
    }
}
