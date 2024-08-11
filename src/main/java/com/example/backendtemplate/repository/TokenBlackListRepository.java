package com.example.backendtemplate.repository;

import com.example.backendtemplate.entities.user.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {

    boolean existsByToken(String token);

}
