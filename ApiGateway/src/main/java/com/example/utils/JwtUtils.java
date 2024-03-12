package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dto.TokenUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtils implements InitializingBean {

    private static final String ROLE_CLAIM_KEY = "role";
    private static final String PRINCIPAL_CLAIM_KEY = "principal";

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiration-time}")
    private int expirationTime;

    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    @Override
    public void afterPropertiesSet() {
        this.algorithm = Algorithm.HMAC512(secretKey);
        this.jwtVerifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public boolean isValid(String token) {
        try {
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generate(String id) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationTime);
        Date expiredAt = calendar.getTime();

        return JWT.create()
                .withIssuer(issuer)
                .withClaim(PRINCIPAL_CLAIM_KEY, id)
                .withClaim(ROLE_CLAIM_KEY, "ROLE_USER")
                .withExpiresAt(expiredAt)
                .sign(algorithm);
    }

    public TokenUser decode(String token) {
        DecodedJWT jwt = jwtVerifier.verify(token);
        String id = jwt.getClaim(PRINCIPAL_CLAIM_KEY).asString();
        String role = jwt.getClaim(ROLE_CLAIM_KEY).asString();
        return new TokenUser(id, role);
    }
}
