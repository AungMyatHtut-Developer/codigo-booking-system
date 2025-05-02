package codigo.amh.codetest.bookingsystem.codigobookingsystem.util;

import java.security.SecureRandom;

public final class OtpUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final int MIN_OTP_VALUE = 100_000;
    private static final int MAX_OTP_VALUE = 999_999;

    public static String generateOtp() {
        int otpValue = SECURE_RANDOM.nextInt(MAX_OTP_VALUE - MIN_OTP_VALUE + 1) + MIN_OTP_VALUE;
        return String.format("%0" + OTP_LENGTH + "d", otpValue);
    }


}
