package com.example.controller;

import com.example.dao.SiteUser;
import com.example.service.SiteUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class SiteUserController {
    private final SiteUserService userService;

    @GetMapping("/id/{id}")
    public Mono<String> getUserIdById(@PathVariable String id) {
        return userService.getUserById(id).map(SiteUser::getUserId);
    }

    @GetMapping("/userId/{userId}")
    public Mono<SiteUser> getSiteUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteSiteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }
}
