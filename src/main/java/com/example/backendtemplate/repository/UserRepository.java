package com.example.backendtemplate.repository;

import com.example.backendtemplate.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByNic(String nic);
    User findOneByUsername(String username);
    User findOneByUserId(String userId);
}
