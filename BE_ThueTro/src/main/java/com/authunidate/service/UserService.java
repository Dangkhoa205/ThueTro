package com.authunidate.service;

import com.authunidate.dto.request.ResetPasswordRequest;
import com.authunidate.entity.User;
import com.authunidate.exception.AppException;
import com.authunidate.exception.ErrorCode;
import com.authunidate.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public User getUser(String identity) {
        return userRepo.findByEmail(identity)
                .or(() -> userRepo.findByPhone(identity))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public boolean resetPassword(ResetPasswordRequest request, User user) {
        boolean validOtp = otpService.verifyOtpCode(user, request.getOtp());
        if (!validOtp) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        return true;
    }
}
