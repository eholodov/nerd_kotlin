package com.dunice.nerd_kotlin.common.aop;

import com.dunice.nerd_kotlin.common.db.projections.SlackIdFullNameProjectionImpl;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@Data
@Profile("dev")
public class RepositoryProxyAspect {

    @Around("execution(public * *(..)) && @annotation(com.dunice.nerd_kotlin.common.aop.RepositoryProxy)")
    public List<SlackIdFullNameProjectionImpl> RepositoryProxyAspectBefore(ProceedingJoinPoint joinPoint) {
        Object[] a = joinPoint.getArgs();

        return ((List<String>) a[0]).stream().map((item) -> new SlackIdFullNameProjectionImpl(item, String.format("slackId's mock for %s", item))).collect(Collectors.toList());
    }

}
