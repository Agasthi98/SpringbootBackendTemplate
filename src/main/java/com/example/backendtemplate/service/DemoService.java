package com.example.backendtemplate.service;

import com.example.backendtemplate.model.request.DemoRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;

import java.util.HashMap;

public interface DemoService {

    BaseDetailsResponse<HashMap<String, Object>> printName(DemoRequest demoRequest);

}
