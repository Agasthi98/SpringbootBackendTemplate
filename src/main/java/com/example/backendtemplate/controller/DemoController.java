package com.example.backendtemplate.controller;

import com.example.backendtemplate.model.request.DemoRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.service.DemoService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;
    @PostMapping("/print")
    public ResponseEntity<DefaultResponse> printName(@Valid @RequestBody DemoRequest demoRequest) {
        BaseDetailsResponse<HashMap<String,Object>> response = demoService.printName(demoRequest);
        return ReturnResponseUtil.returnResponse(response);
    }
}
