package com.example.backendtemplate.controller;

import com.example.backendtemplate.entities.user.User;
import com.example.backendtemplate.model.request.DemoRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.service.DemoService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;
    @PostMapping("/print")
    @PreAuthorize("hasAuthority('APP_USER')")
    public ResponseEntity<DefaultResponse> printName(@Valid @RequestAttribute("user") User user, @RequestBody DemoRequest demoRequest) {
        BaseDetailsResponse<HashMap<String,Object>> response = demoService.printName(demoRequest);
        return ReturnResponseUtil.returnResponse(response);
    }
}
