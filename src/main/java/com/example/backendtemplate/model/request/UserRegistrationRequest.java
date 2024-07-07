package com.example.backendtemplate.model.request;

import com.example.backendtemplate.constants.MessageUtil;
import com.example.backendtemplate.util.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class UserRegistrationRequest {
    @Pattern(regexp = ValidationUtil.USERNAME_PATTERN_REGEX, message = MessageUtil.USERNAME_VALIDATION_MESSAGE)
    @NotEmpty(message = "username shouldn't be empty")
    private String username;
    @NotEmpty(message = "first name shouldn't be empty")
    @JsonProperty("first_name")
    private String firstName;
    @NotEmpty(message = "last name shouldn't be empty")
    @JsonProperty("last_name")
    private String lastName;
    @NotEmpty(message = "password shouldn't be empty")
    @Pattern(regexp = ValidationUtil.USER_PASSWORD_PATTERN_REGEX, message = MessageUtil.PASSWORD_VALIDATION_MESSAGE)
    private String password;
    @NotEmpty(message = "phone number shouldn't be empty")
    @Pattern(regexp = ValidationUtil.PHONE_NUMBER_PATTERN_REGEX, message = MessageUtil.PHONE_NUMBER_VALIDATION_MESSAGE)
    @JsonProperty("phone_number")
    private String phoneNumber;
    @NotEmpty(message = "nic shouldn't be empty")
    @Pattern(regexp = ValidationUtil.NIC_PATTERN_REGEX, message = MessageUtil.NIC_VALIDATION_MESSAGE)
    private String nic;

}
