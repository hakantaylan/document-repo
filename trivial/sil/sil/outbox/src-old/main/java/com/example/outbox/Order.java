package com.example.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Order extends BaseEntity {
    private String status = "NEW";

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    public void ship() { status = "SHIPPED"; }
    public void addItem(OrderItem item) { items.add(item); }
}
