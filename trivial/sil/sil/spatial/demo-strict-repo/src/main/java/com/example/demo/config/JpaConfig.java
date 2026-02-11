package com.example.demo.config;

import com.example.demo.repository.RestrictedRepositoryFactoryBean;
import com.example.demo.repository.RestrictedRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.demo.repository",
    repositoryBaseClass = RestrictedRepositoryImpl.class,
    repositoryFactoryBeanClass = RestrictedRepositoryFactoryBean.class
)
public class JpaConfig {}
