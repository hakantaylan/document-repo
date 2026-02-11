package com.example.demo.repository;

import java.util.List;

public record FetchGraph(List<String> paths) {
  public static FetchGraph of(String... paths) { return new FetchGraph(List.of(paths)); }
}
