package com.example.backendtemplate.entities.user;

import com.example.backendtemplate.entities.BaseEntity;
import com.example.backendtemplate.enums.Status;
import com.example.backendtemplate.util.constants.EntityNames;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EntityNames.USER)
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;
    private String username;
    @Builder.Default
    @Column(unique = true)
    private String userId = UUID.randomUUID().toString();
    private String fullName;
    private String phoneNumber;
    private String password;
    @Builder.Default
    private int loginAttempts = 0;
    private String nic;
    @Builder.Default
    private String status = Status.ACTIVE.name();
    private String tokenReference;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    @Builder.Default
    private Collection<Role> roles = new ArrayList<>();


    //authenticated user fields
    @Transient
    @Builder.Default
    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
}
