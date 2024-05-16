package com.example.backendtemplate.controller;

import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.service.DemoService;
import com.example.backendtemplate.util.ResponseCodeUtil;
import com.example.backendtemplate.util.ReturnResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;
    @GetMapping("/print")
    public ResponseEntity<DefaultResponse> printName() {
        BaseDetailsResponse<HashMap<String,Object>> response = demoService.printName();
        return ReturnResponseUtil.returnResponse(response);
    }
}
