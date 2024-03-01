package net.sparkminds.librarymanagement.aop.logging;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Log4j2
@Component
public class LoggingAspect {
    @Before("execution(* net.sparkminds.librarymanagement.controller..*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("Entering {}.{}", className, methodName);
    }

    @AfterReturning(pointcut = "execution(* net.sparkminds.librarymanagement.controller..*(..))", returning = "result")
    public void logAfterControllerMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("Exiting {}.{}. Returned: {}", className, methodName, result);
    }
}
