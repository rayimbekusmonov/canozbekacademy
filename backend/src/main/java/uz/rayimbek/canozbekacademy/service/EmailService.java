package uz.rayimbek.canozbekacademy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.rayimbek.canozbekacademy.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Send verification email
     */
    @Async
    public void sendVerificationEmail(User user) {
        try {
            String verificationUrl = frontendUrl + "/verify-email?token=" + user.getEmailVerificationToken();

            String subject = "Verify Your Email - Canozbek Academy";
            String body = buildVerificationEmailBody(user.getFullName(), verificationUrl);

            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send password reset email
     */
    @Async
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

            String subject = "Reset Your Password - Canozbek Academy";
            String body = buildPasswordResetEmailBody(user.getFullName(), resetUrl);

            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send welcome email
     */
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            String subject = "Welcome to Canozbek Academy!";
            String body = buildWelcomeEmailBody(user.getFullName());

            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send enrollment confirmation email
     */
    @Async
    public void sendEnrollmentConfirmationEmail(User user, String courseName) {
        try {
            String subject = "Enrollment Confirmation - " + courseName;
            String body = buildEnrollmentEmailBody(user.getFullName(), courseName);

            sendHtmlEmail(user.getEmail(), subject, body);
            log.info("Enrollment confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send enrollment email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send simple text email
     */
    private void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private String buildVerificationEmailBody(String fullName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 30px; 
                        background-color: #FF6B35; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer { margin-top: 30px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Welcome to Canozbek Academy!</h2>
                    <p>Hello %s,</p>
                    <p>Thank you for registering with Canozbek Academy. To complete your registration, please verify your email address.</p>
                    <p><a href="%s" class="button">Verify Email</a></p>
                    <p>Or copy and paste this link into your browser:</p>
                    <p>%s</p>
                    <p>This link will expire in 24 hours.</p>
                    <div class="footer">
                        <p>If you didn't create an account, please ignore this email.</p>
                        <p>Best regards,<br>Canozbek Academy Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, verificationUrl, verificationUrl);
    }

    private String buildPasswordResetEmailBody(String fullName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 30px; 
                        background-color: #0F3460; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer { margin-top: 30px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Password Reset Request</h2>
                    <p>Hello %s,</p>
                    <p>We received a request to reset your password. Click the button below to create a new password:</p>
                    <p><a href="%s" class="button">Reset Password</a></p>
                    <p>Or copy and paste this link into your browser:</p>
                    <p>%s</p>
                    <p>This link will expire in 24 hours.</p>
                    <div class="footer">
                        <p>If you didn't request a password reset, please ignore this email or contact support if you have concerns.</p>
                        <p>Best regards,<br>Canozbek Academy Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, resetUrl, resetUrl);
    }

    private String buildWelcomeEmailBody(String fullName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 30px; 
                        background-color: #FF6B35; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Welcome to Canozbek Academy! 🎉</h2>
                    <p>Hello %s,</p>
                    <p>Your email has been verified successfully! You can now access all our courses and start your learning journey.</p>
                    <p><a href="%s" class="button">Browse Courses</a></p>
                    <p>Happy learning!</p>
                    <p>Best regards,<br>Canozbek Academy Team</p>
                </div>
            </body>
            </html>
            """.formatted(fullName, frontendUrl + "/catalog");
    }

    private String buildEnrollmentEmailBody(String fullName, String courseName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 30px; 
                        background-color: #0F3460; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Enrollment Confirmed! 🎓</h2>
                    <p>Hello %s,</p>
                    <p>You have successfully enrolled in <strong>%s</strong>!</p>
                    <p>You can now start learning and access all course materials.</p>
                    <p><a href="%s" class="button">Start Learning</a></p>
                    <p>Best regards,<br>Canozbek Academy Team</p>
                </div>
            </body>
            </html>
            """.formatted(fullName, courseName, frontendUrl + "/dashboard");
    }
}