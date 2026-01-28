package com.example.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class OutboxEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String entityType;
    private UUID entityId;
    private String eventType;

    @Lob
    private String payload;
}
