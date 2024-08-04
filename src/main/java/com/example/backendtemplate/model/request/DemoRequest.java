package com.example.backendtemplate.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DemoRequest {
    @NotEmpty(message = "name shouldn't be empty")
    private String name;
}
