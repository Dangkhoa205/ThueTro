package com.authunidate.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Size(min = 6, message = "PASSWORD_INVALID")
    private String newPassword;

    @NotBlank(message = "OTP_INVALID")
    private String otp;
}
