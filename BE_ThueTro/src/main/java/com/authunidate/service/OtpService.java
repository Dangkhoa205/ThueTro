package com.authunidate.service;

import com.authunidate.entity.User;
import com.authunidate.entity.UserOtp;
import com.authunidate.exception.AppException;
import com.authunidate.exception.ErrorCode;
import com.authunidate.repo.UserOtpRepo;
import com.authunidate.repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private final UserRepo userRepo;
    private final UserOtpRepo userOtpRepo;
    private final MailService mailService;

    @Transactional
    public void generateOtpCode(User user) {
        userOtpRepo.deleteAllByUserID(user.getId());

        int number = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(number);

        UserOtp userOtp = new UserOtp();
        userOtp.setUser(user);
        userOtp.setOtpCode(otp);
        userOtp.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        userOtpRepo.save(userOtp);

        try {
            mailService.sendOTP(user.getEmail(), user.getFullName(), otp);
        } catch (MessagingException | MailException e) {
            throw new AppException(ErrorCode.EMAIL_SEND_UNSUCCESS);
        }
    }

    @Transactional
    public boolean verifyOtpCode(User user, String otpCode) {
        log.info("Verifying OTP for user {}", user.getEmail());
        Optional<UserOtp> userOtpOpt = userOtpRepo.findValidOtp(user.getId(), otpCode, LocalDateTime.now());
        if (userOtpOpt.isEmpty()) {
            return false;
        }

        UserOtp otp = userOtpOpt.get();
        otp.setUsed(true);
        userOtpRepo.save(otp);

        user.setActive(true);
        userRepo.save(user);
        return true;
    }
}
