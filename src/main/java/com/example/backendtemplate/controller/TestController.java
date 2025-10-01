package com.example.backendtemplate.controller;

import com.example.backendtemplate.model.request.NicRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.service.TestService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final TestService testService;

    @PostMapping("/get-birthdate")
    @PreAuthorize("hasAuthority('APP_USER')")
    public ResponseEntity<DefaultResponse> getBirthDate(@Valid @RequestBody NicRequest nicRequest) {
        BaseDetailsResponse<String> response = testService.getBirthDate(nicRequest.getNic());
        return ReturnResponseUtil.returnResponse(response);
    }

    @PostMapping("/reverse")
    public ResponseEntity<DefaultResponse> reverseString(@RequestBody String text) {
        BaseDetailsResponse<String> response = testService.reverseString(text);
        return ReturnResponseUtil.returnResponse(response);
    }

    @PostMapping("/hash-text")
    public ResponseEntity<DefaultResponse> hashText(@RequestBody String text) {
        BaseDetailsResponse<String> response = testService.hashText(text);
        return ReturnResponseUtil.returnResponse(response);
    }


}
