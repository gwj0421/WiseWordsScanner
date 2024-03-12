package com.example.service;

import com.example.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteServiceImpl implements DeleteService {
    private final SiteUserRepository userRepository;
    private final WebClient.Builder loadBalancedWebClientBuilder;
//    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Override
    public Mono<Void> deleteSiteUser(String id) {
        return userRepository.deleteById(id)
                .then(loadBalancedWebClientBuilder.build().delete().uri("http://notice-board-api/post" + id)
                        .retrieve()
                        .bodyToMono(Void.class));
//        return WebClient.builder()
//                .filter(lbFunction)
//                .build().delete().uri("http://notice-board-api/post")
//                .retrieve()
//                .bodyToMono(Void.class);
    }

}
