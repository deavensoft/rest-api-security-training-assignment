package com.deavensoft.authapi.assignment;

import com.deavensoft.authapi.dtos.LoginUserDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginAttemptAspect {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Pointcut("execution(* com.deavensoft.authapi.controllers.AuthenticationController.authenticate(..))")
    public void loginPointcut() {
    }

    @Around("loginPointcut()")
    public Object aroundLoginAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        LoginUserDto loginUserDto = (LoginUserDto) args[0];

        // if the user is blocked throw here org.springframework.security.authentication.LockedException
        // TODO: implement the logic

        try {
            // Proceed with the login logic
            Object result = joinPoint.proceed();

            // If login succeeds, this line is reached
            // TODO: implement the logic, if needed

            return result; // must return the result object, as defined by Spring AOP

        } catch (Exception e) {
            // If login fails, this line is reached
            // TODO: implement the logic, if needed

            throw e;
        }
    }
}
