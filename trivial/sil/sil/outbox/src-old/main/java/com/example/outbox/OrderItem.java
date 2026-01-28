package com.example.outbox;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderItem extends BaseEntity {
    private String name;
}
