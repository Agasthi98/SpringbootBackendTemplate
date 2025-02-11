package com.example.backendtemplate.controller;

import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.service.ResourceService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/uploads/images")
    public ResponseEntity<DefaultResponse> uploadImage(@RequestParam("file") MultipartFile file) {
            BaseDetailsResponse<String> response = resourceService.saveImage(file);
            return ReturnResponseUtil.returnResponse(response);
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {

        BaseDetailsResponse<Resource> response = resourceService.getImage(filename);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Resource resource = response.getData();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        BaseDetailsResponse<Resource> response = resourceService.downloadImage(fileName);
        if (response.getData() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Resource resource = response.getData();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
