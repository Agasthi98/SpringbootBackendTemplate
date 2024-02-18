package com.example.backendtemplate.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseDetailsResponse<T> {
    private int code;
    private String title;
    private String message;
    private T data;
}
