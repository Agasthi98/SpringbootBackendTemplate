package com.example.backendtemplate.entities;

import com.example.backendtemplate.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String password;
    private int loginAttempts = 0;
    private String nic;
    @Builder.Default
    private String status = UserStatus.ACTIVE.name();
}
