package com.homework.AuthService.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "token_storage")
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;
    private long userId;
    private boolean isRevoked;

    public UserToken(long userId, String token) {
        this.userId = userId;
        this.token = token;
        isRevoked = false;
    }

    public UserToken() {

    }
}
