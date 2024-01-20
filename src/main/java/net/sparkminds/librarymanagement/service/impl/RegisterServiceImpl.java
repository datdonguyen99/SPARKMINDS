package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import net.sparkminds.librarymanagement.entity.Role;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.exception.ResourceForbiddenException;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.exception.ResourceUnauthorizedException;
import net.sparkminds.librarymanagement.payload.request.OtpDto;
import net.sparkminds.librarymanagement.payload.request.RegisterDto;
import net.sparkminds.librarymanagement.payload.request.ResendDto;
import net.sparkminds.librarymanagement.repository.*;
import net.sparkminds.librarymanagement.service.MailSenderService;
import net.sparkminds.librarymanagement.service.RegisterService;
import net.sparkminds.librarymanagement.utils.RoleName;
import net.sparkminds.librarymanagement.utils.Status;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.Calendar;

import static net.sparkminds.librarymanagement.utils.AppConstants.*;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

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

        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .status(Status.INACTIVE)
                .phoneNumber(registerDTO.getPhoneNumber())
                .build();

        // set role is USER by default
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not existed", "user.role.role-not-existed"));
        user.setRole(role);

        // save user entity to db
        userRepository.save(user);

        // create and save token corresponding to user_id in db
        VerificationToken vToken = new VerificationToken(user, UUID.randomUUID().toString());
        tokenRepository.save(vToken);

        // create and save otp corresponding to user_id in db
        VerificationOtp vOtp = new VerificationOtp(user, new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        otpRepository.save(vOtp);

        // send email
        mailSenderService.sendVerificationEmail(user.getEmail(), SITE_URL);
    }

    private String getVerificationTokenStatus(final String token) {
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
    public void verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new ResourceNotFoundException("Not found token", "");
        }

        User userByToken = userRepository.findById(verificationToken.getAccount().getId()).orElseThrow(() -> new ResourceNotFoundException("Not found user", ""));

        String tokenStatus = getVerificationTokenStatus(token);

        if (userByToken.getStatus().equals(Status.ACTIVE)) {
            throw new ResourceInvalidException("User already enabled", "");
        }

        if (tokenStatus.equals(TOKEN_INVALID)) {
            throw new ResourceUnauthorizedException("Verify email fail, link invalid!", "");
        } else if (tokenStatus.equals(TOKEN_EXPIRE)) {
            VerificationToken vToken = tokenRepository.findByToken(token);
            tokenRepository.delete(vToken);
            throw new ResourceForbiddenException("Verify email fail, link expired!", "");
        } else {
            userByToken.setStatus(Status.ACTIVE);
            userRepository.save(userByToken);

            VerificationToken vToken = tokenRepository.findByToken(token);
            tokenRepository.delete(vToken);

            VerificationOtp vOtp = otpRepository.findByAccount(userByToken);
            otpRepository.delete(vOtp);
        }
    }

    @Override
    public void resendToken(ResendDto resendDto) {
        Account account = accountRepository.findByEmail(resendDto.getEmail());
        User user = (User) account;
        VerificationToken vToken = tokenRepository.findByAccount(account);

        if (vToken != null && !vToken.getExpiryDate().before(new Date())) {
            throw new ResourceInvalidException("token not expired, plz use link to authenticate", "token.token-not-expired");
        }

        if (vToken == null) {
            vToken = new VerificationToken(user, UUID.randomUUID().toString());
        } else {
            vToken.updateToken(UUID.randomUUID().toString());
        }
        tokenRepository.save(vToken);

        mailSenderService.sendVerificationEmail(resendDto.getEmail(), SITE_URL);
    }

    private String getVerificationOtpStatus(final String otp) {
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
    public void verifyOtp(OtpDto otpDto) {
        VerificationOtp verificationOtp = otpRepository.findByOtp(otpDto.getOtp());
        if (verificationOtp == null) {
            throw new ResourceNotFoundException("Not found OTP", "");
        }

        User userByOtp = (User) accountRepository.findByEmail(otpDto.getEmail());
        if (userByOtp == null) {
            throw new ResourceNotFoundException("Not found user with email " + otpDto.getEmail(), "");
        }

        String otpStatus = getVerificationOtpStatus(otpDto.getOtp());
        if (userByOtp.getStatus().equals(Status.ACTIVE)) {
            throw new ResourceInvalidException("User already enabled", "");
        }

        if (otpStatus.equals(OTP_INVALID)) {
            throw new ResourceUnauthorizedException("Verify email fail, OTP invalid!", "");
        } else if (otpStatus.equals(OTP_EXPIRE)) {
            VerificationOtp vOtp = otpRepository.findByOtp(otpDto.getOtp());
            otpRepository.delete(vOtp);
            throw new ResourceForbiddenException("Verify email fail, OTP expire!", "");
        } else {
            userByOtp.setStatus(Status.ACTIVE);
            userRepository.save(userByOtp);

            VerificationToken vToken = tokenRepository.findByAccount(userByOtp);
            tokenRepository.delete(vToken);

            VerificationOtp vOtp = otpRepository.findByOtp(otpDto.getOtp());
            otpRepository.delete(vOtp);
        }
    }

    @Override
    public void resendOTP(ResendDto resendDto) {
        Account account = accountRepository.findByEmail(resendDto.getEmail());
        User user = (User) account;
        VerificationOtp vOtp = otpRepository.findByAccount(account);

        if (vOtp != null && !vOtp.getExpiryDate().before(new Date())) {
            throw new ResourceInvalidException("OTP not expired, plz use OTP to authenticate", "otp.otp-not-expired");
        }

        if (vOtp == null) {
            vOtp = new VerificationOtp(user, new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        } else {
            vOtp.updateOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        }
        otpRepository.save(vOtp);

        mailSenderService.sendVerificationEmail(resendDto.getEmail(), SITE_URL);
    }
}
