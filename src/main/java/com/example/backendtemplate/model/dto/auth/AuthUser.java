package com.example.backendtemplate.model.dto.auth;


import com.example.backendtemplate.entities.user.User;

public class AuthUser extends org.springframework.security.core.userdetails.User {

    public AuthUser(User user) {
        super(user.getUsername(), user.getPassword(), user.getGrantedAuthoritiesList());
    }
}
