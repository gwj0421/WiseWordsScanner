package com.example.config;

import com.example.repository.SiteUserRepository;
import com.example.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {
    private final SiteUserRepository userRepository;
    private final WebClient.Builder loadBalancedWebClientBuilder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SiteUserService userService() {
        return new SiteUserServiceImpl(userRepository, deleteService());
    }

    @Bean
    public LoginService loginService() {
        return new LoginServiceImpl(userRepository, passwordEncoder());
    }

    @Bean
    public DeleteService deleteService() {
        return new DeleteServiceImpl(userRepository, loadBalancedWebClientBuilder);
    }
}
