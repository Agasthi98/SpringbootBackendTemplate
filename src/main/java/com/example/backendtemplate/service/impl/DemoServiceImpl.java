package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.request.DemoRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.DemoService;
import com.example.backendtemplate.util.ResponseCodeUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public BaseDetailsResponse<HashMap<String, Object>> printName(DemoRequest demoRequest) {
        try {

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", demoRequest.getName());

            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseCodeUtil.SUCCESS_CODE)
                    .title(ResponseCodeUtil.SUCCESS)
                    .message("Name printed successfully")
                    .data(data)
                    .build();

        } catch (Exception e) {
            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseCodeUtil.FAILED_CODE)
                    .title(ResponseCodeUtil.FAILED)
                    .message("Failed to print name")
                    .build();
        }
    }
}
