package codigo.amh.codetest.bookingsystem.codigobookingsystem.common;

public interface Messages {
    //error related messages
    String EMAIL_ALREADY_REGISTERED = "your provided email is already registered!";
    String EMAIL_OR_PASSWORD_IS_NOT_VALID = "your provided email or password is not valid";
    String CURRENT_PASSWORD_NOT_CORRECT = "password didn't correct";
    String INVALID_REQUEST = "invalid request";
    String PASSWORD_AND_CONFIRM_NOT_MATCH = "password and confirm password didn't match";
    String OTP_VERIFICATION_NOT_CORRECT = "your provided otp is not correct";
    String VERIFY_FIRST = "your account is still not verified yet, please verify first our sent otp into your mail";
    String ACCOUNT_LOCKED = "your account has been locked for security reasons. please contact admin team";
    String ALREADY_VERIFIED = "your account already verified";
    String OTP_EXPIRED = "otp expired!";
    String USER_NOT_FOUND = "user not found with provided email";
    String OTP_USED = "otp is already used and generate new one";
    String PACKAGES_NOT_FOUND = "package not found";
    String CREDIT_CARD_NOT_VALID = "your provided credit card not valid";
    String INSUFFICIENT_BALANCE ="your provided credit card is not sufficient for payment" ;
    String CLASS_NOT_FOUND = "your requested class not found";
    String CLASS_WAS_ENDED = "you can't book the class and class already ended";
    String TIME_CONFLICT = "you have already booked a class at this time";
    String INSUFFICIENT_CREDITS =  "your credit is not sufficient to book the class";
    String DIFFERENT_COUNTRY = "you can't book different country's class";
    String ALREADY_BOOKED = "this class is already booked!";
    String BOOKING_NOT_FOUND ="booking class not found" ;
    String CHECK_IN_NOT_AVAILABLE = "check in not available";

    //success related messages
    String REGISTRATION_SUCCESS = "your account registration is success and check validation email";
    String VERIFICATION_SUCCESS = "your account has been verified and please login again";
    String OTP_SUCCESSFULLY_GENERATED = "otp has been sent to your mail. please check and verify with otp";
    String OTP_SUCCESSFULLY_GENERATED_FOR_FORGOT_PASSWORD = "otp has been sent to your mail. please otp to change password";
    String PASSWORD_CHANGE_SUCCESS = "your new password has been changed";
    String  USER_PURCHASED_PACKAGE_SUCCESS = "you purchased package successfully";
    String BOOKING_SUCCESS = "booking successful" ;
    String BOOKING_CANCEL = "booking cancelled successful";
    String CHECK_IN_SUCCESS ="check in successful";
}
