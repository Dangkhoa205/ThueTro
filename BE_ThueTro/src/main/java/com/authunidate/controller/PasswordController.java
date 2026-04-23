package com.authunidate.controller;

import com.authunidate.dto.request.ForgotPasswordRequest;
import com.authunidate.dto.request.ResetPasswordRequest;
import com.authunidate.dto.response.ApiResponse;
import com.authunidate.dto.response.ResetPasswordResponse;
import com.authunidate.entity.User;
import com.authunidate.service.MailService;
import com.authunidate.service.OtpService;
import com.authunidate.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PasswordController {
    private final UserService userService;
    private final OtpService otpService;
    private final MailService mailService;

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userService.getUser(request.getEmail());
        otpService.generateOtpCode(user);
        return ApiResponse.<String>builder()
                .message("OTP has been sent to your email.")
                .data(user.getEmail())
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        User user = userService.getUser(request.getEmail());
        boolean result = userService.resetPassword(request, user);
        try {
            mailService.sendRegisterNotice(user.getEmail(), user.getFullName());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return ApiResponse.<ResetPasswordResponse>builder()
                .message("Reset password successfully.")
                .data(ResetPasswordResponse.builder().success(result).build())
                .build();
    }
}
