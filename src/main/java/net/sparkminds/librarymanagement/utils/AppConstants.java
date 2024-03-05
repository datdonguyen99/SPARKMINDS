package net.sparkminds.librarymanagement.utils;

import java.util.Arrays;
import java.util.List;

public class AppConstants {
    public static final int EXPIRATION = 60;        // SECONDS
    public static final String TOKEN_INVALID = "TOKEN_INVALID";
    public static final String TOKEN_EXPIRE = "TOKEN_EXPIRE";
    public static final String TOKEN_VALID = "TOKEN_VALID";
    public static final String OTP_INVALID = "TOKEN_INVALID";
    public static final String OTP_EXPIRE = "TOKEN_EXPIRE";
    public static final String OTP_VALID = "TOKEN_VALID";
    public static final String EMAIL_TEMPLATE_FILE_PATH = "src/main/resources/templates/email.ftlh";
    public static final String SITE_URL = "http://localhost:8080/api/v1/common";
    public static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("csv");
    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "png", "jpeg");
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;        // 5MB
    public static final long MAX_IMAGE_FILE_SIZE = 1024 * 1024;        // 1MB
    public static final String CSV_FILE_PATH_READ = "C:\\Users\\datdo\\IdeaProjects\\";
    public static final List<String> EXPECTED_HEADERS = Arrays.asList("id", "title", "authorId", "publisherId", "publishedYear", "numberOfPages", "isbn", "quantity", "imagePath", "category");
    public static final String MAINTENANCE_MODE_KEY = "MAINTENANCE_MODE";

    private AppConstants() {
    }
}
