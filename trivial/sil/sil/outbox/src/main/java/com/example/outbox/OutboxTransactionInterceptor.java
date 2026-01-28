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

        Object result = pjp.proceed();

        // check if transaction is read-only
        boolean readOnly = false;
        if (transactional != null) {
            readOnly = transactional.readOnly();
        } else {
            Class<?> cls = pjp.getTarget().getClass();
            Transactional classAnno = cls.getAnnotation(Transactional.class);
            if (classAnno != null) readOnly = classAnno.readOnly();
        }

        if (!readOnly) {
            // flush entity manager for this transaction (works for REQUIRED & REQUIRES_NEW)
            outboxManager.getEntityManager().flush();

            // flush Outbox events queued in this transaction
            outboxManager.flushAll();
        }

        return result;
    }
}
