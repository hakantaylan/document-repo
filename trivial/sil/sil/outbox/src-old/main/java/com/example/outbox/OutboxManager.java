package com.example.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxManager {

    private final EntityManager em;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final ThreadLocal<List<OutboxEvent>> queue = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<Integer> transactionDepth = ThreadLocal.withInitial(() -> 0);

    public static List<OutboxEvent> getCurrentTransactionQueue() { return queue.get(); }
    public void incrementTransactionDepth() { transactionDepth.set(transactionDepth.get() + 1); }
    public void decrementTransactionDepth() { transactionDepth.set(Math.max(0, transactionDepth.get() - 1)); }
    public int getTransactionDepth() { return transactionDepth.get(); }

    public void flushAll() {
        List<OutboxEvent> events = new ArrayList<>(queue.get());
        for (OutboxEvent e : events) {
            try {
                OutboxEntity outbox = new OutboxEntity(
                        e.getEntity().getClass().getSimpleName(),
                        e.getEntity().getId(),
                        e.getEventType(),
                        mapper.writeValueAsString(e.getEntity())
                );
                em.persist(outbox);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        queue.get().clear();
    }

    public void clearQueue() { queue.get().clear(); }
}
