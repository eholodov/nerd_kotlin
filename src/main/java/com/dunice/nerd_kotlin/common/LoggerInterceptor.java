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
        if (header.isPresent()){
            MDC.put("requestId", request.getHeader("requestId"));
        } else {
            MDC.put("requestId", "IsEmpty");
        }
        return true;
    }
}