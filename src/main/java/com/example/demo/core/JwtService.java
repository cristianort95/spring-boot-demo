package com.example.demo.core;

import com.example.demo.entity.LoginResponse;
import com.example.demo.entity.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User hashPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    private Key generateSingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public LoginResponse generateJwtToken(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userMap = objectMapper.convertValue(user, Map.class);

        userMap.remove("password");

        Date expiresIn = new Date(System.currentTimeMillis() + jwtExpiration * 60 * 60);

        String token = Jwts.builder()
                .setClaims(userMap)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiresIn)
                .signWith(generateSingKey(), SignatureAlgorithm.HS256)
                .compact();

        return new LoginResponse(token, expiresIn);
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateSingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(String jwtToken) {
        return extractClaims(jwtToken).getSubject();
    }

    public Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken).getExpiration();
    }

    public boolean extractTokenValid(String jwtToken) {
        return new Date().before(extractExpiration(jwtToken));
    }
}