package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.DemoService;
import com.example.backendtemplate.util.ResponseUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public BaseDetailsResponse<HashMap<String, Object>> printName() {
        String name = "Agasthi sankalana";
        try {

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", name);

            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title("Success")
                    .message("Name printed successfully")
                    .data(data)
                    .build();

        } catch (Exception e) {
            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title("Failed")
                    .message("Failed to print name")
                    .build();
        }
    }
}
