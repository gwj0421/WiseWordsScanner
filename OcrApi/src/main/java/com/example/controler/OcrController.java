package com.example.controler;

import com.example.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ocr")
@Slf4j
public class OcrController {
    private final OcrService ocrService;
    @PostMapping()
    public Mono<ResponseEntity<String>> processOcr(@RequestPart Flux<FilePart> images) {
        return ocrService.processOcr(images);
    }
}
