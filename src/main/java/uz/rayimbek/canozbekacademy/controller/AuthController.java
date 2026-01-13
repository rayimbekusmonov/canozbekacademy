package uz.rayimbek.canozbekacademy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rayimbek.canozbekacademy.dto.request.LoginRequest;
import uz.rayimbek.canozbekacademy.dto.request.RefreshTokenRequest;
import uz.rayimbek.canozbekacademy.dto.request.RegisterRequest;
import uz.rayimbek.canozbekacademy.dto.response.AuthResponse;
import uz.rayimbek.canozbekacademy.dto.response.MessageResponse;
import uz.rayimbek.canozbekacademy.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request");
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user (invalidate tokens)
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        // In a production app, you'd add the token to a blacklist in Redis
        log.info("Logout request");
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    /**
     * Verify email
     * GET /api/v1/auth/verify-email?token=xxx
     */
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        log.info("Email verification request");
        authService.verifyEmail(token);
        return ResponseEntity.ok(new MessageResponse("Email verified successfully"));
    }

    /**
     * Request password reset
     * POST /api/v1/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam String email) {
        log.info("Password reset request for email: {}", email);
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok(new MessageResponse("Password reset email sent"));
    }

    /**
     * Reset password with token
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        log.info("Password reset confirmation");
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }
}