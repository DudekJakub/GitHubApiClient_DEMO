package com.demo.githubapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@ConditionalOnProperty(value = "logging.aspect.enabled", havingValue = "true", matchIfMissing = true)
public class LoggingAspect {

    @Pointcut("execution(* com.demo.githubapi.service.GitHubService.*(..))")
    public void gitHubServiceMethods() {
    }

    @Before("gitHubServiceMethods()")
    public void logBeforeGitHubServiceMethods(JoinPoint joinPoint) {
        log.debug("Executing method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "gitHubServiceMethods()", throwing = "exception")
    public void logAfterGitHubServiceMethodsThrowing(JoinPoint joinPoint, Exception exception) {
        log.debug("Exception thrown in method: {}", joinPoint.getSignature().toShortString());
        log.debug("Exception message and cause: {}", exception.getMessage() + " | " + exception.getCause());
    }

    @AfterReturning(pointcut = "gitHubServiceMethods()", returning = "result")
    public void logAfterGitHubServiceMethodsReturning(JoinPoint joinPoint, Object result) {
        log.debug("Method executed successfully: {}", joinPoint.getSignature().toShortString());
        log.debug("Result: {}", result);
    }

    @After("execution(* com.demo.githubapi.config.GitHubClientConfig.gitHub(..))")
    public void logAfterGitHubClientBeanCreation() {
        log.debug("GitHubClient configured successfully");
    }
}
