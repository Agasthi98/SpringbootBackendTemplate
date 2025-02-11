package com.example.backendtemplate.service;

import com.example.backendtemplate.model.response.BaseDetailsResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    BaseDetailsResponse<String> saveImage(MultipartFile file);
    BaseDetailsResponse<Resource> getImage(String filename);
    BaseDetailsResponse<Resource> downloadImage(String filename);
}
