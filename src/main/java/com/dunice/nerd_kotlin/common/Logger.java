package com.dunice.nerd_kotlin.common;

import com.dunice.nerd_kotlin.AcademyReminders.WeeklyReminderServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class Logger {

    private final HttpServletRequest httpServletRequest;
    private final String header = "requestId";
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(WeeklyReminderServiceImpl.class.getName());

    @Autowired
    public Logger(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public void logStart(String message, String className, Object... ss) {
        logger.info(message, httpServletRequest.getHeader(header), className, ss);
    }
    public void logFun(String message, String className, Object... ss) {
        logger.info(message, httpServletRequest.getHeader(header), className, ss);
    }
    public void logFinish(String message, String className) {
        logger.info(message, httpServletRequest.getHeader(header), className);
    }
}