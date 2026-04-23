package com.authunidate.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    private String email;
}
