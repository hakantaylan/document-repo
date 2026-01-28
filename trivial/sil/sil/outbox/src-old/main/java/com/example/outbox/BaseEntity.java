package com.example.outbox;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@MappedSuperclass
@EntityListeners(AutoOutboxListener.class)
@Getter
public abstract class BaseEntity implements OutboxAggregate {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
