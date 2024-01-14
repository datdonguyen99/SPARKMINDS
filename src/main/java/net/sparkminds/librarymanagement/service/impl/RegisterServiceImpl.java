package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Role;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.OtpDto;
import net.sparkminds.librarymanagement.payload.RegisterDto;
import net.sparkminds.librarymanagement.repository.RoleRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.repository.VerificationOtpRepository;
import net.sparkminds.librarymanagement.repository.VerificationTokenRepository;
import net.sparkminds.librarymanagement.service.MailSenderService;
import net.sparkminds.librarymanagement.service.RegisterService;
import net.sparkminds.librarymanagement.utils.RoleName;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;
import java.util.Calendar;
import java.util.Collections;

import static net.sparkminds.librarymanagement.utils.AppConstants.*;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final VerificationTokenRepository tokenRepository;

    private final VerificationOtpRepository otpRepository;

    private final MailSenderService mailSenderService;

    private Random random = new Random();

    private static String decimalFormat = "000000";

    @Override
    public void register(RegisterDto registerDTO) {
        // check for email exists in db
        boolean existEmail = userRepository.existsByEmail(registerDTO.getEmail());
        if (existEmail) {
            throw new ResourceInvalidException("Email already existed in the system", "user.email.email-existed");
        }

        // create user entity using Builder pattern
        User user = User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .enabled(false)
                .build();

        // set role is USER by default
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not existed", "user.role.role-not-existed"));
        user.setRoles(Collections.singleton(role));

        // save user entity to db
        userRepository.save(user);

        // create and save token corresponding to user_id in db
        VerificationToken vToken = generateNewVerificationToken(UUID.randomUUID().toString());
        createVerificationTokenForUser(user, vToken.getToken());

        // create and save otp corresponding to user_id in db
        VerificationOtp vOtp = generateNewVerificationOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        createVerificationOtpForUser(user, vOtp.getOtp());

        // send email
        mailSenderService.sendVerificationEmail(user, SITE_URL);
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(user, token);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);

        if (vToken != null) {
            vToken.updateToken(UUID.randomUUID().toString());
        } else {
            vToken = new VerificationToken(UUID.randomUUID().toString());
        }

        return vToken;
    }

    @Override
    public String validateVerificationToken(final String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        final Calendar cal = Calendar.getInstance();

        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        if (verificationToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
            return TOKEN_EXPIRE;
        }

        return TOKEN_VALID;
    }

    @Override
    public String verifyToken(String token) {
        String validateToken = validateVerificationToken(token);

        if (validateToken.equals(TOKEN_INVALID)) {
            return TOKEN_INVALID;
        } else if (validateToken.equals(TOKEN_EXPIRE)) {
            User user = userRepository.findByVerificationToken(token);
            if (user != null && !user.isEnabled()) {
                // update new token for user
                tokenRepository.save(generateNewVerificationToken(token));

                // resend email with new token
                mailSenderService.sendVerificationEmail(user, SITE_URL);

                return TOKEN_EXPIRE;
            }
        } else {
            User user = userRepository.findByVerificationToken(token);
            if (user != null && !user.isEnabled()) {
                user.setEnabled(true);
                userRepository.save(user);

                return TOKEN_VALID;
            }
        }

        return "";
    }

    @Override
    public void createVerificationOtpForUser(final User user, final String otp) {
        final VerificationOtp myOtp = new VerificationOtp(user, otp);
        otpRepository.save(myOtp);
    }

    @Override
    public VerificationOtp generateNewVerificationOtp(final String existingVerificationOtp) {
        VerificationOtp vOtp = otpRepository.findByOtp(existingVerificationOtp);

        if (vOtp != null) {
            vOtp.updateOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        } else {
            vOtp = new VerificationOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        }

        return vOtp;
    }

    @Override
    public String validateVerificationOtp(final String otp) {
        final VerificationOtp verificationOtp = otpRepository.findByOtp(otp);
        final Calendar cal = Calendar.getInstance();

        if (verificationOtp == null) {
            return OTP_INVALID;
        }

        if ((verificationOtp.getExpiryDate().getTime() - cal.getTime().getTime() <= 0)) {
            return OTP_EXPIRE;
        }

        return OTP_VALID;
    }

    @Override
    public String verifyOtp(OtpDto otpDto) {
        String validateOtp = validateVerificationOtp(otpDto.getOtp());

        if (validateOtp.equals(OTP_INVALID)) {
            return OTP_INVALID;
        } else if (validateOtp.equals(OTP_EXPIRE)) {
            User user = userRepository.findByVerificationOtp(otpDto.getOtp());
            if (user != null && !user.isEnabled()) {
                // update new OTP for user
                otpRepository.save(generateNewVerificationOtp(otpDto.getOtp()));

                // resend email with new OTP
                mailSenderService.sendVerificationEmail(user, SITE_URL);

                return OTP_EXPIRE;
            }
        } else {
            User user = userRepository.findByVerificationOtp(otpDto.getOtp());
            if (user != null && !user.isEnabled()) {
                user.setEnabled(true);
                userRepository.save(user);

                return OTP_VALID;
            }
        }

        return "";
    }
}
