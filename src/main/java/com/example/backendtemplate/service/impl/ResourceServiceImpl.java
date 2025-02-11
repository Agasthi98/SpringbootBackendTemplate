package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.ResourceService;
import com.example.backendtemplate.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Value("${file.upload-dir}")
    private String uploadDir;


    /**
     * Save the uploaded image to the file system.
     * @param file
     * @return
     */
    @Override
    public BaseDetailsResponse<String> saveImage(MultipartFile file) {
        try {
            log.info("File upload [started]");
            log.info("File upload directory: {}", uploadDir);

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String contentType = file.getContentType();
            assert contentType != null;
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                throw new IllegalArgumentException("Only JPEG or PNG images are allowed");
            }

            // Get the next file number
            int nextNumber = getNextFileNumber(uploadPath);

            // Determine the file extension
            String extension = contentType.equals("image/png") ? ".png" : ".jpg";
            String newFileName = "image-" + nextNumber + extension;

            log.info("New file name: {}", newFileName);

            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File uploaded successfully [end]");

            return BaseDetailsResponse.<String>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Image uploaded successfully")
                    .data(filePath.toString())
                    .build();
        } catch (IOException e) {
            log.error("Failed to save image: ", e);
            return BaseDetailsResponse.<String>builder()
                    .code(ResponseUtil.INTERNAL_SERVER_ERROR_CODE)
                    .title(ResponseUtil.INTERNAL_SERVER_ERROR)
                    .message("Failed to save image")
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to save image: ", e);
            return BaseDetailsResponse.<String>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Only JPEG or PNG images are allowed")
                    .build();
        } catch (Exception e) {
            log.error("Exception: ", e);
            return BaseDetailsResponse.<String>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message(e.getMessage())
                    .build();
        }
    }

    /**
     * Get the next available file number for naming the uploaded image.
     */
    private int getNextFileNumber(Path uploadPath) throws IOException {
        if (!Files.exists(uploadPath)) {
            return 1;
        }

        try (Stream<Path> files = Files.list(uploadPath)) {
            return files
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.matches("image-\\d+\\.(jpg|png)"))
                    .map(name -> name.replaceAll("[^0-9]", "")) // Extract number
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        }
    }


    /**
     * Get the image from the file system.
     * @param filename
     * @return
     */
    @Override
    public BaseDetailsResponse<Resource> getImage(String filename) {
        try {
            log.info("Get image [started] file name: {}", filename);
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                log.info("Image retrieved successfully [end]");
                return BaseDetailsResponse.<Resource>builder()
                        .code(ResponseUtil.SUCCESS_CODE)
                        .title(ResponseUtil.SUCCESS)
                        .message("Image retrieved successfully")
                        .data(resource)
                        .build();
            } else {
                log.error("failed to load, Image not found");
                return BaseDetailsResponse.<Resource>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Image not found")
                        .build();
            }
        } catch (MalformedURLException e) {
            return BaseDetailsResponse.<Resource>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Image not found")
                    .build();
        }
    }

    /**
     * Download the image from the file system.
     * @param filename
     * @return
     */
    @Override
    public BaseDetailsResponse<Resource> downloadImage(String filename) {
        try {
            log.info("Downloading file [started]: {}", filename);

            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            if (!Files.exists(filePath)) {
                log.error("File not found: {}", filePath);

                return BaseDetailsResponse.<Resource>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("File not found")
                        .build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            return BaseDetailsResponse.<Resource>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("File downloaded successfully")
                    .data(resource)
                    .build();

        } catch (Exception e) {
            log.error("Error downloading file: ", e);
            return BaseDetailsResponse.<Resource>builder()
                    .code(ResponseUtil.INTERNAL_SERVER_ERROR_CODE)
                    .title(ResponseUtil.INTERNAL_SERVER_ERROR)
                    .message("Error downloading file")
                    .build();
        }
    }
}
