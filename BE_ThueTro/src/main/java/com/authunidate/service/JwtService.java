package com.authunidate.service;

import com.authunidate.entity.User;
import com.authunidate.exception.AppException;
import com.authunidate.exception.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMinutes;

    @Value("${refreshjwt.expiration}")
    private long refreshJwtExpirationDays;

    public String generateToken(User user, String roleCode) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("authunidate.local")
                .subject(resolveSubject(user))
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(jwtExpirationMinutes, ChronoUnit.MINUTES)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(roleCode))
                .build();

        JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(claimsSet.toJSONObject()));
        try {
            jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
        } catch (JOSEException e) {
            log.error("Cannot generate access token", e);
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("authunidate.local")
                .subject(resolveSubject(user))
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(refreshJwtExpirationDays, ChronoUnit.DAYS)))
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(claimsSet.toJSONObject()));
        try {
            jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
        } catch (JOSEException e) {
            log.error("Cannot generate refresh token", e);
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
        return jwsObject.serialize();
    }

    public SignedJWT verifyJwt(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        boolean verified = signedJWT.verify(verifier);
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!(verified && expiration.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private String buildScope(String role) {
        StringJoiner joiner = new StringJoiner(" ");
        String normalizedRole = (role == null || role.isBlank()) ? "USER" : role;
        joiner.add("ROLE_" + normalizedRole);
        return joiner.toString();
    }

    private String resolveSubject(User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }
        return user.getPhone();
    }
}
