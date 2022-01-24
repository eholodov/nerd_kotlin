package com.dunice.nerd_kotlin.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class Logger {

    private final HttpServletRequest httpServletRequest;

    private final String header = "requestId";

    public void logStart(String message, String className, Object... ss) {
        log.info(message, httpServletRequest.getHeader(header), className, ss);
    }
    public void logFun(String message, String className, Object... ss) {
        log.info(message, httpServletRequest.getHeader(header), className, ss);
    }
    public void logFinish(String message, String className) {
        log.info(message, httpServletRequest.getHeader(header), className);
    }
}