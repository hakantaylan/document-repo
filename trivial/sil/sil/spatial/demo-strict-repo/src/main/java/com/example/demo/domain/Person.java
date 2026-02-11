package com.example.demo.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Person {
  @Id @GeneratedValue private Long id;
  private String name;
  private boolean active;

  @ManyToOne(fetch = FetchType.LAZY) private Address address;
  @OneToMany(mappedBy = "person", fetch = FetchType.LAZY) private List<Order> orders;
  protected Person() {}
}
