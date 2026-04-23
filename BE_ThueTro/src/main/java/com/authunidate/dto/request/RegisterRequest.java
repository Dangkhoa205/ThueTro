package com.authunidate.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "PASSWORD_NOT_BLANK")
    @Size(min = 6, message = "PASSWORD_INVALID")
    private String password;

    @NotEmpty(message = "CONFIRM_PASSWORD_NOT_BLANK")
    private String confirmPassword;

    @NotEmpty(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotEmpty(message = "FULLNAME_NOT_BLANK")
    private String fullName;

    private String gender;

    @NotNull(message = "YOB_NOT_BLANK")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate yob;

    @Pattern(regexp = "^(84|0[35789])[0-9]{8}\\b", message = "PHONE_INVALID")
    private String phone;

    private String address;
    private String image;
}
