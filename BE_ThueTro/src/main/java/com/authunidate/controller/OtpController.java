package com.authunidate.controller;

import com.authunidate.dto.request.ResendOtpRequest;
import com.authunidate.dto.request.VerifyOtpRequest;
import com.authunidate.dto.response.ApiResponse;
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
@RequiredArgsConstructor
@RequestMapping("/api")
public class OtpController {
    private final UserService userService;
    private final OtpService otpService;
    private final MailService mailService;
    private static final String LOGIN_URL = "http://localhost:5173/login";

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        User user = userService.getUser(request.getEmail());
        boolean valid = otpService.verifyOtpCode(user, request.getOtp());
        if (valid) {
            try {
                String displayName = user.getFullName() == null ? user.getEmail() : user.getFullName();
                mailService.sendEmail(user.getEmail(), LOGIN_URL, displayName);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        String message = valid ? "Verification successful. Your account is now activated." : "Invalid or expired OTP.";
        return ApiResponse.<Void>builder().message(message).build();
    }

    @PostMapping("/resend-otp")
    public ApiResponse<Void> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        User user = userService.getUser(request.getEmail());
        otpService.generateOtpCode(user);
        return ApiResponse.<Void>builder().message("A new OTP has been sent to your email.").build();
    }
}
