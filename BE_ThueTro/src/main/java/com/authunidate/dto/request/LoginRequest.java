package com.authunidate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "EMAIL_NOT_BLANK")
    private String username;
    @NotBlank(message = "PASSWORD_NOT_BLANK")
    private String password;
}
