package com.dunice.nerd_kotlin.common;

import com.dunice.nerd_kotlin.AcademyReminders.WeeklyReminderServiceImpl;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class Logger {

    private final HttpServletRequest httpServletRequest;
    private final String header = "requestId";

    @Autowired
    public Logger(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

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