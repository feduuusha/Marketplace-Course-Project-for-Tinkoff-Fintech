package ru.itis.marketplace.userservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Value("${logging.level.project.exceptions:warn}")
    private String exceptionsLoggingLevel;

    @Value("${logging.level.project.services:info}")
    private String servicesLoggingLevel;

    @Value("${logging.level.project.controllers:info}")
    private String controllersLoggingLevel;

    @Value("${logging.level.project.clients:info}")
    private String clientsLoggingLevel;

    @Pointcut("within(ru.itis.marketplace.userservice.exception..*)")
    public void exceptionsPointcut() {}
    @Pointcut("within(ru.itis.marketplace.userservice.service..*) && !within(ru.itis.marketplace.userservice.service.impl.UserServiceImpl)")
    public void servicesPointcut() {}
    @Pointcut("within(ru.itis.marketplace.userservice.controller.*) && !within(ru.itis.marketplace.userservice.controller.UserRestController)")
    public void controllersPointcut() {}
    @Pointcut("within(ru.itis.marketplace.userservice.client..*)")
    public void clientsPointcut() {}

    @Before("exceptionsPointcut()")
    public void exceptionsBeforeLoggingAdvice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        logger.atLevel(Level.valueOf(exceptionsLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
    }
    
    @Around("servicesPointcut()")
    public Object servicesAroundLoggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        logger.atLevel(Level.valueOf(servicesLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
        Object obj = joinPoint.proceed();
        logger.atLevel(Level.valueOf(servicesLoggingLevel.toUpperCase())).log("Service method:"+ joinPoint.getSignature().getName() + " return: " + obj);
        return obj;
    }

    @Around("controllersPointcut()")
    public Object controllersAroundLoggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        logger.atLevel(Level.valueOf(controllersLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
        Object obj = joinPoint.proceed();
        logger.atLevel(Level.valueOf(controllersLoggingLevel.toUpperCase())).log("Controller method:"+ joinPoint.getSignature().getName() + " return: " + obj);
        return obj;
    }

    @Around("clientsPointcut()")
    public Object clientsAroundLoggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Logger logger = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        logger.atLevel(Level.valueOf(clientsLoggingLevel.toUpperCase())).log(joinPoint.toShortString() + " with args " + Arrays.toString(args));
        long start = System.nanoTime();
        Object obj = joinPoint.proceed();
        logger.atLevel(Level.valueOf(clientsLoggingLevel.toUpperCase())).log("Controller method:"+ joinPoint.getSignature().getName() + " return: " + obj + " for " + (System.nanoTime() - start) / 1_000_000 + " ms");
        return obj;
    }
}
