package com.example.demo.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface RestrictedRepository<T, ID, R extends RestrictedRepository<T, ID, R>>
        extends Repository<T, ID> {

    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    void deleteById(ID id);

    default R self() { return (R) this; }
}
