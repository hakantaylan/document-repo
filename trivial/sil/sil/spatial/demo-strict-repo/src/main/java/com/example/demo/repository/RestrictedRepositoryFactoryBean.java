package com.example.demo.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.jpa.repository.JpaRepository;

public class RestrictedRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID>
    extends JpaRepositoryFactoryBean<T, S, ID> {

    public RestrictedRepositoryFactoryBean(Class<? extends T> repo) { super(repo); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new RestrictedRepositoryFactory(em);
    }
}
