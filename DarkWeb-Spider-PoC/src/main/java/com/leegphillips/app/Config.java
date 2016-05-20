package com.leegphillips.app;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

@Component
public class Config {
    @Bean
    public CompletionService<List<String>> pipe() {
        return new ExecutorCompletionService<>(Executors.newFixedThreadPool(400));
    }
}
