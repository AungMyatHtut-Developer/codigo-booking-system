package codigo.amh.codetest.bookingsystem.codigobookingsystem.service;

import codigo.amh.codetest.bookingsystem.codigobookingsystem.common.Messages;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.dto.user.*;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.exception.*;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.model.AppUser;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.repository.UserRepository;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.OtpUtils;
import codigo.amh.codetest.bookingsystem.codigobookingsystem.util.PasswordUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    // validate password
    public boolean validatePassword(String email, String password) {
        logger.info("Find User With Email : {}", email);
        AppUser appUser = findUserByEmail(email);
        if (appUser != null) {
            String computedHash = PasswordUtils.hashPassword(password, appUser.getSalt());
            return passwordEncoder.matches(computedHash, appUser.getPassword());
        } else {
            return false;
        }
    }

    //find user
    public AppUser findUserByEmail(String email) {
        return userRepository.findAppUserByEmail(email);
    }

    public void generateOtp(GenerateOTPRequest request) {
        logger.info("Generate OTP : {}", request);
        Map<String, String> errors = new HashMap<>();
        Optional<AppUser> user = Optional.ofNullable(userRepository.findAppUserByEmail(request.email()));
        if (user.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.INVALID_REQUEST);
        }
        // check user already enabled or not
        if (user.get().getIsEnabled()) {
            throw new ForbiddenException(Messages.ALREADY_VERIFIED);
        }
        // check user account locked or not
        if (user.get().getIsLock()) {
            throw new ForbiddenException(Messages.ACCOUNT_LOCKED);
        }

        AppUser updateUser = user.get();
        String otp = OtpUtils.generateOtp();
        updateUser.setOtp(otp);
        updateUser.setOtpExpireTime(LocalDateTime.now().plusMinutes(5));
        updateUser.setIsOtpActive(true);
        userRepository.save(updateUser);
        emailService.sendEmail(otp, updateUser.getEmail(), otp);
    }

    public void register(RegistrationRequest request) {
        if (userRepository.existsAppUserByEmail(request.email())) {
            throw new ConflictException(Messages.EMAIL_ALREADY_REGISTERED);
        }

        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(request.password(), salt);
        String encodedPassword = passwordEncoder.encode(hashedPassword);

        AppUser user = AppUser.builder()
                .email(request.email())
                .userName(request.username())
                .password(encodedPassword)
                .salt(salt)
                .isEnabled(false)
                .isLock(false)
                .countryCode(request.countryCode())
                .createdDate(LocalDateTime.now())
                .build();

        String otp = OtpUtils.generateOtp();
        logger.info("Generated OTP : {}", mask(otp));
        user.setOtp(otp);
        user.setOtpExpireTime(LocalDateTime.now().plusMinutes(5));
        user.setIsOtpActive(true);
        userRepository.save(user);

        emailService.sendEmail(otp, "Message", request.email());
    }

    public void sendForgotPasswordOtp(ForgotPasswordRequest request) {
        Map<String, String> errors = new HashMap<>();

        Optional<AppUser> user = Optional.ofNullable(userRepository.findAppUserByEmail(request.email()));
        //check is user already registered or not
        if (user.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.INVALID_REQUEST);
        }

        // check user already enabled or not
        if (!user.get().getIsEnabled()) {
            throw new ForbiddenException(Messages.VERIFY_FIRST);
        }

        // check user account locked or not
        if (user.get().getIsLock()) {
            throw new ForbiddenException(Messages.ACCOUNT_LOCKED);
        }

        AppUser updateUser = user.get();
        String otp = OtpUtils.generateOtp();
        updateUser.setOtp(otp);
        updateUser.setOtpExpireTime(LocalDateTime.now().plusMinutes(5));
        updateUser.setIsOtpActive(true);
        userRepository.save(updateUser);

        emailService.sendEmail("Forgot Password OTP", updateUser.getEmail(), otp);
    }

    public void changePasswordWithOTP(ChangePasswordRequest request) {
        //check is user already registered or not
        Map<String, String> errors = new HashMap<>();
        Optional<AppUser> user = Optional.ofNullable(userRepository.findAppUserByEmail(request.email()));
        if (user.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.INVALID_REQUEST);
        }

        //check password and confirm password correct
        if (!request.password().contentEquals(request.confirmPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.PASSWORD_AND_CONFIRM_NOT_MATCH);
        }

        // verify otp expired or not
        if (user.get().getOtpExpireTime().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException(Messages.OTP_EXPIRED);
        }

        // verify otp active or not
        if (!user.get().getIsOtpActive()) {
            throw new UnauthorizedException(Messages.OTP_USED);
        }

        //check otp correct or not
        if (!user.get().getOtp().contentEquals(request.otp())) {
            throw new UnauthorizedException(Messages.OTP_VERIFICATION_NOT_CORRECT);
        }

        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(request.password(), salt);

        AppUser updateUser = user.get();
        updateUser.setSalt(salt);
        updateUser.setPassword(passwordEncoder.encode(hashedPassword));
        updateUser.setUpdatedDate(LocalDateTime.now());
        updateUser.setIsOtpActive(false);
        userRepository.save(updateUser);
    }

    public void verifyAccount(VerificationRequest request) {
        //check is user already registered or not
        Map<String, String> errors = new HashMap<>();
        Optional<AppUser> user = Optional.ofNullable(userRepository.findAppUserByEmail(request.email()));
        if (user.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.INVALID_REQUEST);
        }

        //check is already verified or not
        if (user.get().getIsEnabled()) {
            throw new ForbiddenException(Messages.ALREADY_VERIFIED);
        }

        // verify otp expired or not
        if (user.get().getOtpExpireTime().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException(Messages.OTP_EXPIRED);
        }

        if (!request.otp().contentEquals(user.get().getOtp())) {
            AppUser appUser = user.get();
            appUser.setOtpCount(appUser.getOtpCount() == null ? 0 : appUser.getOtpCount() + 1);
            userRepository.save(appUser);
            throw new UnauthorizedException(Messages.OTP_VERIFICATION_NOT_CORRECT);
        }

        // verify otp active or not
        if (!user.get().getIsOtpActive()) {
            throw new UnauthorizedException(Messages.OTP_USED);
        }

        AppUser updateUser = user.get();
        updateUser.setIsEnabled(true);
        updateUser.setIsOtpActive(false);

        userRepository.save(updateUser);
    }

    public String login(LoginRequest request) {
        Optional<AppUser> user = Optional.ofNullable(userRepository.findAppUserByEmail(request.email()));
        if (user.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.EMAIL_OR_PASSWORD_IS_NOT_VALID);
        }

        //verify account is enabled or not
        if (!user.get().getIsEnabled()) {
            throw new ForbiddenException(Messages.VERIFY_FIRST);
        }

        //verify account is not lock
        if (user.get().getIsLock()) {
            throw new ForbiddenException(Messages.ACCOUNT_LOCKED);
        }

        //verify password
        if (!validatePassword(request.email(), request.password())) {
            throw new UnauthorizedException(Messages.EMAIL_OR_PASSWORD_IS_NOT_VALID);
        }

        return user.get().getEmail();
    }

    public UserInfoResponse getUserInfo(String email) {
        AppUser user = findUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(Messages.USER_NOT_FOUND);
        }

        return new UserInfoResponse(user.getUserName(), user.getCreatedDate(), user.getEmail(), user.getCountryCode());
    }

    public void changePassword(PasswordChangeRequest passwordChangeRequest, String email) {
        String currentPassword = passwordChangeRequest.currentPassword();
        String newPassword = passwordChangeRequest.newPassword();
        String confirmPassword = passwordChangeRequest.confirmPassword();

        Optional<AppUser> user = Optional.ofNullable(userRepository.findAppUserByEmail(email));
        if (user.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.EMAIL_OR_PASSWORD_IS_NOT_VALID);
        }

        //verify password
        if (!validatePassword(email, currentPassword)) {
            throw new UnauthorizedException(Messages.CURRENT_PASSWORD_NOT_CORRECT);
        }

        //check new password and confirm password match
        if (!newPassword.contentEquals(confirmPassword)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), Messages.PASSWORD_AND_CONFIRM_NOT_MATCH);
        }

        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(newPassword, salt);
        String encodedPassword = passwordEncoder.encode(hashedPassword);

        AppUser updateUser = user.get();
        updateUser.setPassword(encodedPassword);
        updateUser.setSalt(salt);
        updateUser.setUpdatedDate(LocalDateTime.now());
        userRepository.save(updateUser);
    }
}
