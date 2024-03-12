package com.example.service;

import com.example.dao.SiteUser;
import com.example.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SiteUserServiceImpl implements SiteUserService {
    private final SiteUserRepository userRepository;
    private final DeleteService deleteService;

    @Override
    public Flux<SiteUser> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Mono<SiteUser> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<String> getUserIdsByIds(List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(userRepository::findSiteUserById)
                .map(SiteUser::getUserId);
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
