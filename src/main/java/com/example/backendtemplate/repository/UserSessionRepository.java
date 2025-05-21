package com.example.backendtemplate.repository;

import com.example.backendtemplate.entities.user.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByUserId(String userId);
    UserSession findOneByUserId(String userId);

}
