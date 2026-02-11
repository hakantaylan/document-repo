package com.example.demo.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Method;

public class RestrictedRepositoryFactory extends JpaRepositoryFactory {
    public RestrictedRepositoryFactory(EntityManager em) { super(em); }

    @Override
    protected void validate(RepositoryMetadata metadata) {
        for (Method m : metadata.getRepositoryInterface().getMethods()) {
            if (!m.isDefault() && !m.isSynthetic() && !m.getDeclaringClass().equals(Object.class)
                && !m.getDeclaringClass().equals(RestrictedRepository.class)) {
                throw new IllegalStateException("Derived queries forbidden: " + m);
            }
        }
    }
}
