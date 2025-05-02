package codigo.amh.codetest.bookingsystem.codigobookingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static codigo.amh.codetest.bookingsystem.codigobookingsystem.util.MaskUtils.mask;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public boolean sendEmail(String otp, String email, String message) {
        try {
            logger.info("Send Email : {}, Message : {}, OTP : {}", email, message,mask(otp));
            return true;
        } catch (Exception e) {
            logger.error("Exception Occurred While Sending Email.",e);
            return false;
        }
    }

}
