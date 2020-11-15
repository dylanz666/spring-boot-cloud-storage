package com.github.dylanz666.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@Component
public class CorsInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CorsInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String logPattern = "[%s][%s]:%s";
        logger.info(String.format(logPattern, request.getMethod(), request.getRemoteAddr(), request.getRequestURI()));
        return true;
    }
}
