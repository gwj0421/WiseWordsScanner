package com.example.service;

import com.example.dto.OCRRequest;
import com.example.dto.OCRResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {
    @Value("${ocr.version}")
    private String version;
    private final WebClient clovaOCRWebClient;

    @Override
    public Mono<ResponseEntity<String>> processOcr(Flux<FilePart> images) {
        return images
                .flatMap(image -> clovaOCRWebClient.post().body(BodyInserters.fromMultipartData(OCRRequest.makeBody(image, version))).retrieve().bodyToMono(OCRResponse.class))
                .map(ocrResponse -> {
                    StringBuilder sb = new StringBuilder();
                    for (OCRResponse.ImageInfo.Field field : ocrResponse.images().get(0).fields()) {
                        sb.append(field.inferText());
                    }
                    return sb.toString();
                }).collectList().map(result -> String.join("\n", result)).map(ResponseEntity::ok);
    }
}

