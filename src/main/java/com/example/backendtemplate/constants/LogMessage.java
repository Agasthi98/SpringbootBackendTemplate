package com.example.backendtemplate.constants;

public class LogMessage {

    private LogMessage(){}

    //log prefix
    public static final String USER_REGISTRATION_PREFIX = "User Registration -> {}";
    public static final String USER_ALREADY_EXIST="User Already Exist";
    public static final String USER_REGISTERED_SUCCESSFULLY="User Registered Successfully";
    public static final String USER_VALIDATION = "validate the user -> {}";
    public static final String EXCEPTION = "Exception -> {}";
    public static final String USER_REGISTRATION_FAILED = "User registration failed -> {}";
    public static final String USER_REGISTRATION_SUCCESS = "User registration success -> {}";

    public static final String LOG_PREFIX_BAD_REQUEST_HANDLER = "BadRequestHandler -> {}";

    public static final String OTP_VERIFY = "otpVerify -> {}";
    public static final String OTP_SEND = "sendOtpByEmailAndSms -> ";
    public static final String SAVE_OTP = "saveOtp -> {}";
    public static final String UPDATE_OTP = "updateOtp -> {}";
    public static final String SEND_OTP = "sendOtp -> {}";
    public static final String FORGOT_OTP = "forgotOtp -> {}";

    public static final String OTP_ATTEMPTS_BALANCE = "Otp Attempts remaining -> {}";
    public static final String FOREIGN_NUMBER = "foreign number -> {}";
    public static final String REQUEST = "Request -> {}";
    public static final String RESPONSE = "Response -> {}";
    public static final String URL = "Url -> {}";


    public static final String REGISTER = "Register  -> {}";

    public static final String ENCRYPT_DATA_EC = "Encrypt Data With EC -> {}";
    public static final String ENCRYPT_DATA_AES = "Encrypt Data With Aes -> {}";
    public static final String DECRYPT_DATA_AES = "Decrypt Data With Aes -> {}";
    public static final String DECRYPT_DATA = "Decrypt Data -> {}";
    public static final String ENCRYPT_DATA = "Encrypt Data -> {}";
    public static final String PUBLIC_KEY = "Get Public Key -> {}";
    public static final String PRIVATE_KEY = "Get Private Key -> {}";

    public static final String GENERATE_SIGNATURE = "generate Signature -> {}";

    public static final String SAVE_NEW_DEVICE = "save new device -> {}";
    public static final String SAVE_DEVICE = "save device -> {}";
    public static final String LOGIN = "Login mobile -> {}";


    //log content
    public static final String INVALID_CREDENTIAL = "Invalid credentials.";
    public static final String USER_DISABLED = "User Disabled";
    public static final String USER_LOCKED = "User Locked";
    public static final String USER_NOT_ACTIVE = "User not active";
    public static final String DEVICE_ID_MISMATCH = "Device-ID mismatch";
    public static final String DEVICE_ID_NOT_FOUND = "Device-ID header not found";
    public static final String BAD_REQUEST = "Bad request";
    public static final String ERROR_OCCURRED = "Error occurred in the process";
    public static final String CAN_NOT_FIND_USER = "Cannot find User";
    public static final String INPUT_VALIDATION_ERROR = "input validation error";

    // SEND OTP
    public static final String IS_OTP_SENT = "OTP email sent: {}";
    public static final String SEND_OTP_TO_EMAIL = "Trying to send an OTP to the email: {}";
    public static final String SEND_OTP_TO_MOBILE = "Trying to send an OTP to the mobile: {}";


    //ReturnResponseUtil response specification
    public static final String RETURN_RESPONSE_UTIL = "ReturnResponseUtil -> {}";
    public static final String SUCCESS_RESPONSE = "success response";
    public static final String FAILED_RESPONSE = "failed response";
    public static final String INTERNAL_SERVER_ERROR_RESPONSE = "internal server error response";


    public static final String NOT_FOUND = "Not found -> {}";
    public static final String CHALLENGE_ERROR = "There is no biometric challenge";
    public static final String APP_USER_NOT_FOUND = "AppUser not found";
    public static final String IB_USER_NOT_FOUND = "IbUser not found";
    public static final String USER_ALREADY_LOGGED_IN_ANOTHER_DEVICE = "User already logged in another device.";

}
