package com.example.service;

import com.example.dao.SiteUser;
import com.example.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class SiteUserServiceImpl implements SiteUserService {
    private final SiteUserRepository userRepository;
    private final DeleteService deleteService;

    @Override
    public Mono<SiteUser> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<SiteUser> getUserByUserId(String userId) {
        return userRepository.findSiteUserByUserId(userId);
    }

    @Override
    public Mono<SiteUser> createSiteUser(SiteUser user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return deleteService.deleteSiteUser(id);
    }

}
