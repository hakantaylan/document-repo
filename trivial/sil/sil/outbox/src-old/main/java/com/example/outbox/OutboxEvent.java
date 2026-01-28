package com.example.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OutboxEvent {
    private final BaseEntity entity;
    private final String eventType;
}
