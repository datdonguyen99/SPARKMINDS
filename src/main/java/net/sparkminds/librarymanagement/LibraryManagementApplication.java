package net.sparkminds.librarymanagement;

import jakarta.annotation.PostConstruct;
import net.sparkminds.constants.DefaultProfileUtil;
import net.sparkminds.constants.LibraryManagementConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@SpringBootApplication
public class LibraryManagementApplication {
    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApplication.class);

    private final Environment env;

    public LibraryManagementApplication(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());

        if (activeProfiles.contains(LibraryManagementConstants.SPRING_PROFILE_DEVELOPMENT) &&
                activeProfiles.contains(LibraryManagementConstants.SPRING_PROFILE_PRODUCTION)) {
            logger.error("You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(LibraryManagementConstants.SPRING_PROFILE_DEVELOPMENT) &&
                activeProfiles.contains(LibraryManagementConstants.SPRING_PROFILE_STAGING)) {
            logger.error("You have misconfigured your application! It should not run " + "with both the 'dev' and 'stag' profiles at the same time.");
        }
        if (activeProfiles.contains(LibraryManagementConstants.SPRING_PROFILE_PRODUCTION) &&
                activeProfiles.contains(LibraryManagementConstants.SPRING_PROFILE_STAGING)) {
            logger.error("You have misconfigured your application! It should not run " + "with both the 'prod' and 'stag' profiles at the same time.");
        }
    }

    public static void main(String[] args) {
//        SpringApplication.run(LibraryManagementApplication.class, args);
        SpringApplication app = new SpringApplication(LibraryManagementApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store"))
                .map(key -> "https")
                .orElse("http");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path"))
                .filter(StringUtils::isNotBlank)
                .orElse("/");
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("The host name could not be determined, using `localhost` as fallback");
        }

        logger.info(
                "\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );
    }
}
