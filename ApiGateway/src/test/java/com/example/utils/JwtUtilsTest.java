package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void test() {
        String temp = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJndW53b29uZyIsInByaW5jaXBhbCI6IjY1ZTViNDRlOWM4MDhmMTczZWRjYTZmMyIsInJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjQzMDE3OTA5NDZ9.NqGiHd5xAJjrm7YG2a0WIQFG_fyk3DAY_Y6GRzsGDhA";
        String temp2 = jwtUtils.generate("65e5b44e9c808f173edca6f3");
//        Assertions.assertThat(jwtUtils.isValid(temp)).isTrue();
        Assertions.assertThat(temp).isEqualTo(temp2);
    }

}