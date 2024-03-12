package com.example.config;

import com.example.repository.RefreshTokenRepository;
import com.example.repository.SiteUserRepository;
import com.example.service.LoginService;
import com.example.service.LoginServiceImpl;
import com.example.service.DeleteService;
import com.example.service.DeleteServiceImpl;
import com.example.service.SiteUserService;
import com.example.service.SiteUserServiceImpl;
import com.example.utils.JwtUtils;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

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
        return new LoginServiceImpl(jwtUtils, userRepository, refreshTokenRepository,passwordEncoder());
    }

    @Bean
    public DeleteService deleteService() {
        return new DeleteServiceImpl(userRepository, loadBalancedWebClientBuilder);
    }
}
