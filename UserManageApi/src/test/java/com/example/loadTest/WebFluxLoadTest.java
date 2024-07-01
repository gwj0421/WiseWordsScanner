package com.example.loadTest;

import com.example.dto.SignUpForm;
import com.example.repository.SiteUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
public class WebFluxLoadTest {
    private static final int TARGET_SIZE = 1000;
    private final WebClient webClient = WebClient.create();
    private final RestTemplate restTemplate = new RestTemplate();

    private final String baseUrl = "http://localhost:8011/api/performance";
    @Autowired
    private SiteUserRepository siteUserRepository;

    @Test
    void testPerformance() throws InterruptedException {
        long start;
        long end;
        siteUserRepository.deleteAll().block();

        start = System.currentTimeMillis();
        for (int i = 0; i < TARGET_SIZE; i++) {
            restTemplate.postForEntity(baseUrl, new SignUpForm("user" + i, "userID", "password","password", "email@gmail.com"), String.class);
        }
        end = System.currentTimeMillis();
        long restTemplateResult = end - start;
        System.out.println("restTemplate result = " + restTemplateResult);

        siteUserRepository.deleteAll().block();

        CountDownLatch countDownLatch = new CountDownLatch(TARGET_SIZE);
        start = System.currentTimeMillis();
        for (int i = 0; i < TARGET_SIZE; i++) {
            webClient.post()
                    .uri(baseUrl)
                    .bodyValue(new SignUpForm("user" + i, "userID", "password", "password", "email@gmail.com"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(res -> countDownLatch.countDown());
        }
        countDownLatch.await();

        end = System.currentTimeMillis();
        long webFluxResult = end - start;
        System.out.println("webFlux result = " + webFluxResult);
        System.out.println("upgrade ratio = " + (restTemplateResult / webFluxResult));
    }
}
