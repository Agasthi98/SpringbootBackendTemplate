package com.example.backendtemplate.model.dto;

import com.example.backendtemplate.entities.UserEntity;
import com.example.backendtemplate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logWriter = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findOneByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            logWriter.log(Level.SEVERE, () -> "Username not found: " + username);
            throw new UsernameNotFoundException("could not found user !");
        }
        logWriter.info("User Authenticated Success !");
        return new CustomUserDetails(user);
    }

    public UserEntity loadUserDetailByUsername(String username) {
        UserEntity user = userRepository.findOneByUsername(username);
        return user;
    }
}
