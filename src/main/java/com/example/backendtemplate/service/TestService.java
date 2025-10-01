package com.example.backendtemplate.service;

import com.example.backendtemplate.model.response.BaseDetailsResponse;

import java.util.HashMap;

public interface TestService {

    BaseDetailsResponse<String> getBirthDate(String nic);

}
