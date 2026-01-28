package com.example.outbox;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OutboxTransactionInterceptor {

    private final OutboxManager outboxManager;

    @Around("@within(transactional) || @annotation(transactional)")
    public Object aroundTransactional(ProceedingJoinPoint pjp, Transactional transactional) throws Throwable {
        outboxManager.incrementTransactionDepth();
        try {
            Object result = pjp.proceed();
            if (outboxManager.getTransactionDepth() == 1 && !isReadOnly(pjp, transactional)) {
                outboxManager.flushAll();
            }
            return result;
        } finally {
            outboxManager.decrementTransactionDepth();
            if (outboxManager.getTransactionDepth() == 0) {
                outboxManager.clearQueue();
            }
        }
    }

    private boolean isReadOnly(ProceedingJoinPoint pjp, Transactional transactional) {
        if (transactional != null) return transactional.readOnly();
        Class<?> cls = pjp.getTarget().getClass();
        Transactional classAnno = cls.getAnnotation(Transactional.class);
        return classAnno != null && classAnno.readOnly();
    }
}
