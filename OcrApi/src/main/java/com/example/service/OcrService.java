package com.example.service;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OcrService {
    Mono<ResponseEntity<String>> processOcr(Flux<FilePart> images);
}
