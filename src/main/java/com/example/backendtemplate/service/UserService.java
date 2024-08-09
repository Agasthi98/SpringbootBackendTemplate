package com.example.backendtemplate.service;

import com.example.backendtemplate.model.request.UserRegistrationRequest;
import com.example.backendtemplate.model.request.user.UserLoginRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;

import java.util.HashMap;

public interface UserService {

    BaseDetailsResponse<HashMap<String,Object>> userRegistration(UserRegistrationRequest userRegistrationRequest);
    BaseDetailsResponse<HashMap<String,Object>> login(UserLoginRequest userLoginRequest);

}
