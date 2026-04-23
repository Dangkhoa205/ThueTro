# AuthUnidate

Standalone authentication service extracted from UniDate.

## Features
- Register with OTP verification
- Login with JWT access token + refresh token
- Refresh token endpoint
- Firebase login (`/api/auth/firebase`)
- Forgot password + reset password with OTP
- Swagger UI at `/swagger-ui/index.html`

## Main endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/firebase`
- `POST /api/verify-otp`
- `POST /api/resend-otp`
- `POST /api/users/forgot-password`
- `POST /api/users/reset-password`

## Run
```bash
mvn spring-boot:run
```

## Config
Edit `src/main/resources/application.properties`:
- SQL Server connection
- `jwt.secret` (must be a strong secret)
- mail credentials
- firebase credentials path
