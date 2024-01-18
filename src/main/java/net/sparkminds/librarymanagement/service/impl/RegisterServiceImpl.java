package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import net.sparkminds.librarymanagement.entity.Role;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.OtpDto;
import net.sparkminds.librarymanagement.payload.request.RegisterDto;
import net.sparkminds.librarymanagement.payload.request.ResendDto;
import net.sparkminds.librarymanagement.repository.RoleRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.repository.VerificationOtpRepository;
import net.sparkminds.librarymanagement.repository.VerificationTokenRepository;
import net.sparkminds.librarymanagement.service.MailSenderService;
import net.sparkminds.librarymanagement.service.RegisterService;
import net.sparkminds.librarymanagement.utils.RoleName;
import net.sparkminds.librarymanagement.utils.Status;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;
import java.util.Calendar;

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
        VerificationToken vToken = generateNewVerificationToken(UUID.randomUUID().toString());
        createVerificationTokenForUser(user, vToken.getToken());

        // create and save otp corresponding to user_id in db
        VerificationOtp vOtp = generateNewVerificationOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        createVerificationOtpForUser(user, vOtp.getOtp());

        // send email
        mailSenderService.sendVerificationEmail(user.getEmail(), SITE_URL);
    }

    private void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(user, token);
        tokenRepository.save(myToken);
    }

    private VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);

        if (vToken != null) {
            vToken.updateToken(UUID.randomUUID().toString());
        } else {
            vToken = new VerificationToken(UUID.randomUUID().toString());
        }

        return vToken;
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
    public String verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new ResourceNotFoundException("Not found token", "");
        }

        User userByToken = userRepository.findById(verificationToken.getAccount().getId()).orElseThrow(() -> new ResourceNotFoundException("Not found user", ""));

        String tokenStatus = getVerificationTokenStatus(token);

        if (userByToken.getStatus().equals(Status.ACTIVE)) {
            return "";
        }

        if (tokenStatus.equals(TOKEN_EXPIRE)) {
            VerificationToken vToken = tokenRepository.findByToken(token);
            tokenRepository.delete(vToken);
            return TOKEN_EXPIRE;
        }

        if (tokenStatus.equals(TOKEN_VALID)) {
            userByToken.setStatus(Status.ACTIVE);
            userRepository.save(userByToken);

            VerificationToken vToken = tokenRepository.findByToken(token);
            tokenRepository.delete(vToken);

            VerificationOtp vOtp = otpRepository.findByAccount(userByToken);
            otpRepository.delete(vOtp);

            return TOKEN_VALID;
        }

        return TOKEN_INVALID;
    }

    @Override
    public void resendToken(ResendDto resendTokenDto) {
        Account account = userRepository.findByEmail(resendTokenDto.getEmail());
        User user = (User) account;

        VerificationToken vToken = tokenRepository.findByAccount(account);
        if (vToken != null) {
            throw new ResourceInvalidException("invalid token, token exist", "");
        }
        vToken = generateNewVerificationToken(UUID.randomUUID().toString());
        createVerificationTokenForUser(user, vToken.getToken());

        mailSenderService.sendVerificationEmail(resendTokenDto.getEmail(), SITE_URL);

    }

    private void createVerificationOtpForUser(final User user, final String otp) {
        final VerificationOtp myOtp = new VerificationOtp(user, otp);
        otpRepository.save(myOtp);
    }

    private VerificationOtp generateNewVerificationOtp(final String existingVerificationOtp) {
        VerificationOtp vOtp = otpRepository.findByOtp(existingVerificationOtp);

        if (vOtp != null) {
            vOtp.updateOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        } else {
            vOtp = new VerificationOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        }

        return vOtp;
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
    public String verifyOtp(OtpDto otpDto) {
        VerificationOtp verificationOtp = otpRepository.findByOtp(otpDto.getOtp());
        if (verificationOtp == null) {
            throw new ResourceNotFoundException("Not found OTP", "");
        }

        User userByOtp = (User) userRepository.findByEmail(otpDto.getEmail());
//        User userByOtp = userRepository.findById(verificationOtp.getAccount().getId()).orElseThrow(() -> new ResourceNotFoundException("Not found user", ""));

        String otpStatus = getVerificationOtpStatus(otpDto.getOtp());

        if (userByOtp.getStatus().equals(Status.ACTIVE)) {
            return "";
        }

        if (otpStatus.equals(OTP_EXPIRE)) {
            VerificationOtp vOtp = otpRepository.findByOtp(otpDto.getOtp());
            otpRepository.delete(vOtp);
            return OTP_EXPIRE;
        }

        if (otpStatus.equals(OTP_VALID)) {
            userByOtp.setStatus(Status.ACTIVE);
            userRepository.save(userByOtp);

            VerificationToken vToken = tokenRepository.findByAccount(userByOtp);
            tokenRepository.delete(vToken);

            VerificationOtp vOtp = otpRepository.findByOtp(otpDto.getOtp());
            otpRepository.delete(vOtp);

            return OTP_VALID;
        }

        return OTP_INVALID;
    }

    @Override
    public void resendOTP(ResendDto resendDto) {
        Account account = userRepository.findByEmail(resendDto.getEmail());
        User user = (User) account;

        VerificationOtp vOtp = otpRepository.findByAccount(user);
        if (vOtp != null) {
            throw new ResourceInvalidException("OTP exist", "");
        }

        vOtp = generateNewVerificationOtp(new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        createVerificationOtpForUser(user, vOtp.getOtp());

        mailSenderService.sendVerificationEmail(resendDto.getEmail(), SITE_URL);
    }
}
