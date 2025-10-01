package com.example.backendtemplate.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NicRequest {
    @NotEmpty(message = "NIC cannot be empty")
    private String nic;
}
