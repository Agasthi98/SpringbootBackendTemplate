package com.example.backendtemplate.repository;

import com.example.backendtemplate.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByNic(String nic);
    UserEntity findOneByUsername(String username);
}
