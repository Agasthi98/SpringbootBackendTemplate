package com.example.backendtemplate.model.dto.auth;

import com.example.backendtemplate.entities.user.Role;
import com.example.backendtemplate.entities.user.User;
import com.example.backendtemplate.repository.RoleRepository;
import com.example.backendtemplate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logWriter = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = getAppUserDetails(username);
            if (user == null) {
                throw new UsernameNotFoundException("User " + username + " was not found in the database");
            }

            return new AuthUser(user);
        } catch (Exception e) {
            logWriter.log(Level.SEVERE, e, () -> "loadUserByUsername-> Exception: {}" + e.getMessage());
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }
    }

    private User getAppUserDetails(String username) {
        Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
        User user = userRepository.findOneByUsername(username);

        if (user != null) {
            Hibernate.initialize(user.getRoles());
            user.getRoles().forEach(role -> {
                Role role1 = roleRepository.findRoleById(role.getId());
                if (role1 != null) {
                    Hibernate.initialize(role1.getPermissions());
                    role1.getPermissions().forEach(permission -> {
                        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
                        grantedAuthoritiesList.add(grantedAuthority);
                    });
                }
            });
            user.setGrantedAuthoritiesList(grantedAuthoritiesList);
            return user;
        } else {
            logWriter.log(Level.WARNING, "getAppUserDetails-> user not found for this user identity : {}", username);
            return null;
        }
    }
}
