package com.authunidate.controller;

import com.authunidate.dto.request.LoginRequest;
import com.authunidate.dto.request.RefreshRequest;
import com.authunidate.dto.request.RegisterRequest;
import com.authunidate.dto.response.ApiResponse;
import com.authunidate.dto.response.LoginResponse;
import com.authunidate.dto.response.RefreshResponse;
import com.authunidate.entity.User;
import com.authunidate.service.AuthService;
import com.authunidate.service.OtpService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder().data(authService.login(request)).build();
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshResponse> refreshToken(@Valid @RequestBody RefreshRequest request) throws ParseException, JOSEException {
        return ApiResponse.<RefreshResponse>builder().data(authService.refresh(request)).build();
    }

    @PostMapping("/firebase")
    public ApiResponse<LoginResponse> verifyToken(@RequestBody Map<String, String> body) throws Exception {
        String idToken = body.get("token");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return ApiResponse.<LoginResponse>builder().data(authService.handleGoogleLogin(decodedToken)).build();
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerUser(request);
        otpService.generateOtpCode(user);
        return ApiResponse.<Void>builder().message("Check your email for OTP to complete sign up.").build();
    }
}
