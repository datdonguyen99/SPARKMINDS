package net.sparkminds.librarymanagement.utils;

public class AppConstants {
    public static final int EXPIRATION = 30;        // SECONDS
    public static final String TOKEN_INVALID = "TOKEN_INVALID";
    public static final String TOKEN_EXPIRE = "TOKEN_EXPIRE";
    public static final String TOKEN_VALID = "TOKEN_VALID";
    public static final String OTP_INVALID = "TOKEN_INVALID";
    public static final String OTP_EXPIRE = "TOKEN_EXPIRE";
    public static final String OTP_VALID = "TOKEN_VALID";
    public static final String EMAIL_TEMPLATE_FILE_PATH = "src/main/resources/templates/email.ftlh";
    public static final String SITE_URL = "http://localhost:8080/api/v1/auth";
    private AppConstants(){}
}
