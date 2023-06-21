package ru.practicum.shareit;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "ru.practicum.shareit.*")
@EnableJpaRepositories(basePackages = "ru.practicum.shareit.*")
@EntityScan("ru.practicum.shareit.*")
public class JPAConfig {
}
