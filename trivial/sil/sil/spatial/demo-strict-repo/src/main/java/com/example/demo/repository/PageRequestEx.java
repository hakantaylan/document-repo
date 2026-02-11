package com.example.demo.repository;

public record PageRequestEx(int page, int size, SortEx sort) {
  public int offset() { return page * size; }
}
