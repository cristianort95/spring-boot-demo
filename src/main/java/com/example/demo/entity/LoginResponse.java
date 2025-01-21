package com.example.demo.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@RequiredArgsConstructor
public class LoginResponse {
    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Date expiresIn;

    public LoginResponse(String token, Date expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}