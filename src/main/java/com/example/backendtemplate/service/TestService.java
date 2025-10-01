package com.example.backendtemplate.service;

import com.example.backendtemplate.model.response.BaseDetailsResponse;


public interface TestService {

    BaseDetailsResponse<String> getBirthDate(String nic);
    BaseDetailsResponse<String> reverseString(String text);
    BaseDetailsResponse<String> hashText(String text);

}
