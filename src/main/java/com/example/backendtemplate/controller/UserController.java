package com.example.backendtemplate.controller;

import com.example.backendtemplate.entities.user.User;
import com.example.backendtemplate.model.dto.auth.AuthResponseDto;
import com.example.backendtemplate.model.dto.auth.JwtService;
import com.example.backendtemplate.model.request.user.UserRegistrationRequest;
import com.example.backendtemplate.model.request.user.UserLoginRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.model.response.SignOutResponse;
import com.example.backendtemplate.service.UserService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse> userRegistration(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        BaseDetailsResponse<HashMap<String,Object>> response = userService.userRegistration(userRegistrationRequest);
        return ReturnResponseUtil.returnResponse(response);

    }
    @PostMapping("/login")
    public ResponseEntity<DefaultResponse> authenticateAndGetToken(@RequestBody UserLoginRequest userLoginRequest){
       BaseDetailsResponse<AuthResponseDto> response = userService.login(userLoginRequest);
       return ReturnResponseUtil.returnResponse(response);
    }
    @PostMapping("/signout")
    public ResponseEntity<DefaultResponse> signOut(@RequestAttribute("user") User user){
        BaseDetailsResponse<SignOutResponse> response = userService.signOut(user.getUsername());
        return ReturnResponseUtil.returnResponse(response);
    }
}
