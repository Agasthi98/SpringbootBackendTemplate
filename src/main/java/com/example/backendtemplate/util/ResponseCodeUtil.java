package com.example.backendtemplate.util;


public class ResponseCodeUtil {

    private ResponseCodeUtil(){
        throw new IllegalStateException("Utility class");
    }

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR";

    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_CODE_WITHOUT_DATA = "0000";
    public static final String INTERNAL_SERVER_ERROR_CODE = "1010";
    public static final String FAILED_CODE = "3000";
    public static final String DECRYPTION_FAILED = "2020";
    public static final String MESSAGE_CONTENT_NOT_FOUND = "2025";
    public static final String PARAMETER_MISSING = "2026";
    public static final String INPUT_VALIDATION_ERROR_CODE = "3000";

    public static final String NIC_VERIFICATION_FAILED = "4000";
    public static final String MOBILE_NUMBER_EMPTY = "4010";
    public static final String OTP_SENDING_FAILED = "4020";

    public static final String ALREADY_EXISTS = "2030";
    public static final String CANNOT_FIND_ACCOUNT = "4030";
    public static final String INVALID_SESSION = "4035";
    public static final String OTP_MISMATCH = "4040";
    public static final String OTP_ATTEMPTS_EXCEEDED = "4050";
    public static final String FAILED_TO_SAVE_TOKEN = "4060";
    public static final String FAILED_TO_FETCH_ACCOUNT_DETAILS = "4062";
    public static final String INVALID_BANK_TYPE = "4065";
    public static final String INVALID_BILLER_CHANNEL = "4066";

    public static final String NOT_IN_ACTIVATED_STATE = "4070";

    public static final String AUTHENTICATION_ERROR_OCCURRED = "Error occurred while authenticating request";

    public static final String NOT_FOUND = "4080";

    public static final String EMPTY_RESPONSE = "4090";
    public static final String INSUFFICIENT_BALANCE = "4091";
    public static final String INSUFFICIENT_BALANCE_ERROR = "Insufficient Account Balance";
    public static final String INSUFFICIENT_POINTS_ERROR = "Insufficient UPoints Balance";

    public static final String MISSING_OTP = "5000";
    public static final String OTP_EXPIRED = "5050";
    public static final String EMPTY_PARAMETER = "5080";
}
