package com.dunice.nerd_kotlin.common;

import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {

        Optional<String> header = Optional.ofNullable(request.getHeader("requestId"));
        MDC.put("requestId", header.isPresent() ? request.getHeader("requestId") : "IsEmpty");
        return true;
    }
}