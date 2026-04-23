package com.authunidate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {
    @NotBlank(message = "INVALID_KEY")
    private String refreshToken;
}
