package com.example.backendtemplate.controller;

import com.example.backendtemplate.model.request.UserRegistrationRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.service.UserService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
//    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse> userRegistration(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        BaseDetailsResponse<HashMap<String,Object>> response = userService.userRegistration(userRegistrationRequest);
        return ReturnResponseUtil.returnResponse(response);

    }
//    @PostMapping("/api/v1/login")
//    public AuthResponseDto AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDTO){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
//        if(authentication.isAuthenticated()){
//            return AuthResponseDto.builder()
//                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername())).build();
//        } else {
//            throw new UsernameNotFoundException("invalid user request !");
//        }
//    }
}
