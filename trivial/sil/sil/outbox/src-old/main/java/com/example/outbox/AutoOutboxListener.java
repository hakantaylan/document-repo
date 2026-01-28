package com.example.outbox;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class AutoOutboxListener {

    @PrePersist
    public void prePersist(BaseEntity entity) { enqueue(entity, "CREATED"); }

    @PreUpdate
    public void preUpdate(BaseEntity entity) { enqueue(entity, "UPDATED"); }

    @PreRemove
    public void preRemove(BaseEntity entity) { enqueue(entity, "DELETED"); }

    private void enqueue(BaseEntity entity, String eventType) {
        OutboxManager.getCurrentTransactionQueue().add(new OutboxEvent(entity, eventType));
    }
}
