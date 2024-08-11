package com.example.backendtemplate.model.dto.auth;

import com.example.backendtemplate.repository.TokenBlackListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlackListService {

    private final TokenBlackListRepository tokenBlackListRepository;

    public boolean isTokenExist(String token) {
        return tokenBlackListRepository.existsByToken(token);
    }
}
