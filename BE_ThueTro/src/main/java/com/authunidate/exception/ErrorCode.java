package com.authunidate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED(9999, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must have at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1004, "Invalid validation key", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    PASSWORD_NOT_BLANK(1009, "Password must not be blank", HttpStatus.BAD_REQUEST),
    CONFIRM_PASSWORD_NOT_BLANK(1010, "Confirm password must not be blank", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_BLANK(1011, "Email must not be blank", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1012, "Email is invalid", HttpStatus.BAD_REQUEST),
    FULLNAME_NOT_BLANK(1013, "Full name must not be blank", HttpStatus.BAD_REQUEST),
    YOB_NOT_BLANK(1014, "Date of birth must not be blank", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1015, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    DATE_FORMAT_INVALID(1016, "Date format is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1017, "Password and confirm password do not match", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_UNSUCCESS(1019, "Failed to send email", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(1022, "Account is locked", HttpStatus.FORBIDDEN),
    OTP_INVALID(1023, "OTP is invalid or expired", HttpStatus.BAD_REQUEST),
    LOGIN_FAIL(1029, "Login failed: wrong password", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1030, "Email is already used by password account", HttpStatus.CONFLICT),
    PHONE_EXISTED(1031, "Phone number already exists", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
