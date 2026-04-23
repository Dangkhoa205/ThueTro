package com.authunidate.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yob;
    private String avatar;
    private String address;
    private String role;
    private boolean verified;
}
