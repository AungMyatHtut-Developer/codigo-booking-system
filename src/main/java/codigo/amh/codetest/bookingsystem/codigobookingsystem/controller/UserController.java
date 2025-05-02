package codigo.amh.codetest.bookingsystem.codigobookingsystem.controller;


import codigo.amh.codetest.bookingsystem.codigobookingsystem.common.Messages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user.*;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.UnauthorizedException;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.security.JwtUtil;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.service.UserService;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.ResponseHelper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/v1")
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/unauth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        logger.info("Register Info : {}", registrationRequest);
        userService.register(registrationRequest);
        return ResponseHelper.success(Messages.REGISTRATION_SUCCESS);
    }

    @PostMapping("/unauth/generate-otp")
    public ResponseEntity<?> generateOTP(@Valid @RequestBody GenerateOTPRequest generateOTPRequest) {
        logger.info("Generate OTP : {}", generateOTPRequest);
        userService.generateOtp(generateOTPRequest);
        return ResponseHelper.success(Messages.OTP_SUCCESSFULLY_GENERATED);
    }

    @PostMapping("/unauth/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        logger.info("Generate OTP For forgot Password: {}", forgotPasswordRequest);
        userService.sendForgotPasswordOtp(forgotPasswordRequest);
        return ResponseHelper.success(Messages.OTP_SUCCESSFULLY_GENERATED_FOR_FORGOT_PASSWORD);
    }

    @PostMapping("/unauth/change-password-with-otp")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        logger.info("Change Password with OTP: {}", changePasswordRequest);
        userService.changePasswordWithOTP(changePasswordRequest);
        return ResponseHelper.success(Messages.PASSWORD_CHANGE_SUCCESS);
    }

    @PostMapping("/unauth/verify-account")
    public ResponseEntity<?> verifyAccount(@Valid @RequestBody VerificationRequest verificationRequest) {
        logger.info("Verify Info : {}", verificationRequest);
        //TODO: to add bruteforce attack for otp
        userService.verifyAccount(verificationRequest);
        return ResponseHelper.success(Messages.VERIFICATION_SUCCESS);
    }

    @PostMapping("/unauth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login Info : {}", loginRequest);
        String email = userService.login(loginRequest);
        String token = jwtUtil.generateToken(email);
        return ResponseHelper.success(new AuthResponse(token));
    }


    @GetMapping("/auth/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Get Profile Information");
        String email = extractEmailFromToken(authorizationHeader);

        if (email != null) {
            UserInfoResponse userInfo = userService.getUserInfo(email);
            return ResponseHelper.success(userInfo);
        } else {
            return ResponseHelper.createResponse(HttpStatus.UNAUTHORIZED, null, null);
        }
    }

    @PostMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest, @RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Password Change Request: {}", passwordChangeRequest);

        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }

        userService.changePassword(passwordChangeRequest, email);
        return ResponseHelper.success(Messages.PASSWORD_CHANGE_SUCCESS);
    }

    @GetMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Reset Password");

        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            throw new UnauthorizedException(Messages.USER_NOT_FOUND);
        }

        userService.sendForgotPasswordOtp(new ForgotPasswordRequest(email));
        return ResponseHelper.success(Messages.OTP_SUCCESSFULLY_GENERATED_FOR_FORGOT_PASSWORD);
    }


    private String extractEmailFromToken(String authorizationHeader) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        if (token != null && jwtUtil.isTokenValid(token)) {
            return jwtUtil.extractUsername(token);
        }

        return null;
    }
}
