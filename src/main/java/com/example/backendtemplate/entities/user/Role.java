package com.example.backendtemplate.entities.user;

import com.example.backendtemplate.util.constants.EntityNames;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = EntityNames.ROLES)
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private Collection<User> appUsers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id", referencedColumnName = "id"
            )
    )
    @Builder.Default
    private Collection<Permission> permissions = new ArrayList<>();
}
