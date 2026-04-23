package com.authunidate.service;

import com.authunidate.dto.request.LoginRequest;
import com.authunidate.dto.request.RefreshRequest;
import com.authunidate.dto.request.RegisterRequest;
import com.authunidate.dto.response.LoginResponse;
import com.authunidate.dto.response.RefreshResponse;
import com.authunidate.entity.User;
import com.authunidate.exception.AppException;
import com.authunidate.exception.ErrorCode;
import com.authunidate.mapper.UserMapper;
import com.authunidate.repo.UserRepo;
import com.google.firebase.auth.FirebaseToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String GOOGLE_AUTH_PASSWORD = "__GOOGLE_AUTH__";

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepo.findByEmail(loginRequest.getUsername())
                .or(() -> userRepo.findByPhone(loginRequest.getUsername()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        boolean auth = passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash());
        if (!auth) {
            throw new AppException(ErrorCode.LOGIN_FAIL);
        }

        String roleCode = roleService.getRoleCodeByUserId(user.getId());
        String token = jwtService.generateToken(user, roleCode);
        String refreshToken = jwtService.generateRefreshToken(user);
        return LoginResponse.builder()
                .authenticated(true)
                .accessToken(token)
                .refreshToken(refreshToken)
                .user(withRole(user, roleCode))
                .build();
    }

    public RefreshResponse refresh(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT;
        try {
            signedJWT = jwtService.verifyJwt(request.getRefreshToken());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String userName = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepo.findByEmail(userName)
                .or(() -> userRepo.findByPhone(userName))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String roleCode = roleService.getRoleCodeByUserId(user.getId());
        return RefreshResponse.builder()
                .token(jwtService.generateToken(user, roleCode))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public LoginResponse handleGoogleLogin(FirebaseToken token) {
        String email = token.getEmail();
        if (email == null || email.isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (userRepo.existsByEmail(email)) {
            User existing = userRepo.findUserByEmail(email);
            if (!passwordEncoder.matches(GOOGLE_AUTH_PASSWORD, existing.getPasswordHash())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            return toLoginResponse(existing);
        }

        User newUser = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(GOOGLE_AUTH_PASSWORD))
                .fullName(token.getName() == null ? email : token.getName())
                .avatarUrl(token.getPicture())
                .active(true)
                .build();
        userRepo.save(newUser);
        roleService.ensureUserHasRole(newUser.getId(), RoleService.ROLE_USER);
        return toLoginResponse(newUser);
    }

    public User registerUser(RegisterRequest request) {
        User user = processRegister(
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword(),
                () -> userMapper.toUser(request)
        );
        roleService.ensureUserHasRole(user.getId(), RoleService.ROLE_USER);
        return user;
    }

    @Transactional
    public User processRegister(String email, String password, String confirmPassword, Supplier<User> userSupplier) {
        if (userRepo.existsByEmail(email)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (!password.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        User user = userSupplier.get();
        if (user.getPhone() != null && !user.getPhone().isBlank() && userRepo.findByPhone(user.getPhone()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setActive(true);
        return userRepo.save(user);
    }

    private LoginResponse toLoginResponse(User user) {
        String roleCode = roleService.getRoleCodeByUserId(user.getId());
        return LoginResponse.builder()
                .authenticated(true)
                .accessToken(jwtService.generateToken(user, roleCode))
                .refreshToken(jwtService.generateRefreshToken(user))
                .user(withRole(user, roleCode))
                .build();
    }

    private com.authunidate.dto.response.UserResponse withRole(User user, String roleCode) {
        com.authunidate.dto.response.UserResponse response = userMapper.toUserResponse(user);
        response.setRole(roleCode);
        return response;
    }
}
