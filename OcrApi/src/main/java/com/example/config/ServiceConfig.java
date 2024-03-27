package com.example.config;

import com.example.service.OcrServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {
    private final WebClient clovaOCRWebClient;
    @Bean
    public OcrServiceImpl ocrService() {
        return new OcrServiceImpl(clovaOCRWebClient);
    }
}
