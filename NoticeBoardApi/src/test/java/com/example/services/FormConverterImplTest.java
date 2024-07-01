package com.example.services;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

@DataMongoTest
@Import({ServiceConfig.class, WebClientConfig.class, CircuitBreakerAutoConfiguration.class})
@Slf4j
class FormConverterImplTest {
    @Autowired
    private FormConverter formConverter;
}