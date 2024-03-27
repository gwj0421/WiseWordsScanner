package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final String SECRET_HEADER_KEY = "X-OCR-SECRET";
    @Value("${ocr.secret-key}")
    private String secretKey;
    @Value("${ocr.invoke-url}")
    private String invokeUrl;

    @Bean
    public WebClient clovaOCRWebClient() {
        return WebClient.builder().baseUrl(invokeUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .defaultHeader(SECRET_HEADER_KEY, secretKey).build();
    }
}
