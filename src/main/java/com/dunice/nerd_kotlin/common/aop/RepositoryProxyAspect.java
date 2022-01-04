package com.dunice.nerd_kotlin.common.aop;

import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
@Data
@Profile("dev")
public class RepositoryProxyAspect {

    @Around("execution(public * *(..)) && @annotation(com.dunice.nerd_kotlin.common.aop.RepositoryProxy)")
    public List<String> RepositoryProxyAspectBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("START");
        Object[] a = joinPoint.getArgs();
//        System.out.println("a " + a);
//        throw new RuntimeException("qweqweqweqweqweqweqw");
//        int count = 0;
//        if (a!=null) count = (int) a[0].toString().chars().filter(e->e==',').count() + 1;
//        List<String> result = new ArrayList<>();
//        for (int i=0; i<count; i++) {result.add(null);}
        return List.of("qwe");
    }

}
