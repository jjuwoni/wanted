package com.wanted.aop.section02.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/* comment.
*   성능 측정 관련 로직을 모아둔 Aspect
* */
@Component
@Aspect
public class PerformanceAspect {

    /* comment.
    *   @Aspect : 해당 클래스가 AOP 의 Aspect(관점) 임을 선언한다.
    *   해당 클래스 내부에서 관점을 어디(Pointcut)에 적용할지와 무엇(Advice)을 할 지를
    *   하나의 단위로 묶게 된다.
    * */

    /* comment.
    *   @Around 어노테이션은 메서드 실행 전과 후에 실행되는 Advice 를 정의하는 어노테이션이다.
    *   - 가장 강력한 어노테이션이며, Advice 는 여러 종류가 있다.
    *   - @Before, @After, @AfterReturning, @AfterThrowing, @Around
    *   - Pointcut : 해당 Advice(메서드) 가 어디에서 실행될 지를 작성하는 용어
    *   - 현재 Pointcut : "execution(* com.wanted.aop.section02.MemberService.*(..))"
    *   - JoinPoint : Pointcut 에 작성한 표현식을 바탕으로 실제 Advice 가 동작하는 대샃
    * */
    @Around("execution(* com.wanted.aop.section02.MemberService.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable{

        System.out.println("joinPoint = " + joinPoint);
        String methodName = joinPoint.getSignature().getName();

        // 핵심 비즈니스 전에 수행
        long startTime = System.currentTimeMillis();

        // 핵심 비즈니스 수행
        try {
            /* comment.
            *   ProceedingJoinPoint : @Around Advice 에서 사용되는 특별한 인터페이스.
            *   일반적인 JoinPoint 를 확장한 것으로, 대상 메서드의 실행을 직접 제어할 수 있다.
            *   .proceed() : 대상 메서드를 실행하는 메서드
            *   proceed() 를 작성하지 않으면, 대상 메서드느 실행되지 않는다. == 강력하다
            *   반환값은 대상 메서드의 결과이며, Object 타입으로 모든 반환 타입을 담을 수 있게 설계한다.
            * */
            Object result = joinPoint.proceed();
            return result;
        } finally {
            // 핵심 비즈니스 후에 수행
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            System.out.println("[성능] " + methodName + " 메소드의 실행 시간 : " + executionTime + "(ms)");
        }
//        return null;

    }

}
