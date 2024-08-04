package com.example.backendtemplate.entities;

import com.example.backendtemplate.enums.UserStatus;
import com.example.backendtemplate.util.constants.EntityNames;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EntityNames.USER)
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;
    private String username;
    @Column(unique = true)
    private String userId = UUID.randomUUID().toString();
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private int loginAttempts = 0;
    private String nic;
    private String birthDay;
    @Builder.Default
    private String status = UserStatus.ACTIVE.name();
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();
}
