package com.example.controller;

import com.example.dao.SiteUser;
import com.example.dto.SiteUserForm;
import com.example.services.SiteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class SiteUserController {
    private final SiteUserService userService;

    @GetMapping("/getAllUser")
    public Flux<SiteUser> getAllSiteUsers() {
        return userService.getAllUser();
    }

    @GetMapping("/id/{id}")
    public Mono<SiteUser> getSiteUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/userId/{userId}")
    public Mono<SiteUser> getSiteUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

    @PostMapping
    public Mono<SiteUser> createUser(@RequestBody SiteUserForm user) {
        return userService.createSiteUser(user.toSiteUser());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteSiteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }
}
