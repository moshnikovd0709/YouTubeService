package server.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.HashMap;
import java.util.Map;

@Aspect
public class LoggingAnnotatedMethodsAspect {

    private final Map<String, ProfileInfo> profileInfoMap;
    private final String profilePackage;

    public LoggingAnnotatedMethodsAspect(String profilePackage) {
        this.profilePackage = profilePackage;
        profileInfoMap = new HashMap<>();
    }

    @Around("@annotation(server.aspect.Profile) && execution(* *(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!joinPoint.getSignature().getDeclaringType().getPackage().toString()
                .startsWith("package " + profilePackage)) {
            return joinPoint.proceed(joinPoint.getArgs());
        }
        long startMs = System.currentTimeMillis();
        System.out.println("Start method " + joinPoint.getSignature().getName());

        Object result = joinPoint.proceed(joinPoint.getArgs());

        ProfileInfo previousProfile = profileInfoMap.getOrDefault(joinPoint.getSignature().getName(), new ProfileInfo(0, 0));
        long executionTimeMs = System.currentTimeMillis() - startMs;
        int newTimesCalled = previousProfile.timesCalled() + 1;
        double newAvgTime = countAvgTime(previousProfile.timesCalled(), previousProfile.avgExecutionTime(), executionTimeMs);
        profileInfoMap.put(
                joinPoint.getSignature().getName(),
                new ProfileInfo(newTimesCalled, newAvgTime)
        );

        System.out.println("Finish method " + joinPoint.getSignature().getName()
                + ", execution time in ms: " + executionTimeMs);
        System.out.println(joinPoint.getSignature().getName() + " average execution time in ms: " + newAvgTime);
        System.out.println(joinPoint.getSignature().getName() + " overall times called: " + newTimesCalled);

        return result;
    }

    private double countAvgTime(int n, double avg, double newValue) {
        return (avg * n + newValue)/(n + 1);
    }
}
