package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.TestService;
import com.example.backendtemplate.util.NumberUtil;
import com.example.backendtemplate.util.ResponseUtil;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public BaseDetailsResponse<String> getBirthDate(String nic) {
        return BaseDetailsResponse.<String>builder()
                .code(ResponseUtil.SUCCESS_CODE)
                .title(ResponseUtil.SUCCESS)
                .message("Birth date fetched successfully")
                .data(NumberUtil.getBirthDateFromNIC(nic))
                .build();
    }
}
