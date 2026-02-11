package com.example.demo.repository;

import java.util.List;

public record SortEx(List<Order> orders) {
  public static SortEx by(String property) { return new SortEx(List.of(new Order(property, Direction.ASC))); }
  public record Order(String property, Direction direction) {}
  public enum Direction { ASC, DESC }
}
