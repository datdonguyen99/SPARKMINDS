package net.sparkminds.librarymanagement.service.impl;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.repository.VerificationOtpRepository;
import net.sparkminds.librarymanagement.repository.VerificationTokenRepository;
import net.sparkminds.librarymanagement.service.MailSenderService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static net.sparkminds.librarymanagement.utils.AppConstants.EMAIL_TEMPLATE_FILE_PATH;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;

    private final VerificationTokenRepository tokenRepository;

    private final VerificationOtpRepository otpRepository;

    private final AccountRepository accountRepository;

    @Override
    public void sendVerificationEmail(String email, String siteURL) {
        Account account = accountRepository.findByEmail(email);
        String toAddress = account.getEmail();
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Verify-Mail";
        String subject = "Please verify your registration!";
        String verifyURL = siteURL + "/verify/token?code=" + tokenRepository.findByAccount(account).getToken();
        String otp = otpRepository.findByAccount(account).getOtp();
        String emailContent = getEmailContent(account, verifyURL, otp);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
        } catch (MessagingException e) {
            throw new ResourceInvalidException(e.getMessage(), "MimeMessageHelper.MimeMessageHelper-configure-fail");
        } catch (UnsupportedEncodingException e) {
            throw new ResourceInvalidException(e.getMessage(), "Unsupported-encoding-error");
        }

        javaMailSender.send(message);
    }

    private String getEmailContent(Account account, String verifyURL, String otp) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        model.put("account", account);
        model.put("verifyURL", verifyURL);
        model.put("otp", otp);

        try {
            configuration.getTemplate(EMAIL_TEMPLATE_FILE_PATH).process(model, stringWriter);
        } catch (IOException e) {
            throw new ResourceNotFoundException(e.getMessage(), "template.template-not-found");
        } catch (TemplateException e) {
            throw new ResourceInvalidException(e.getMessage(), "template.template-process-fail");
        } finally {
            try {
                stringWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringWriter.getBuffer().toString();
    }

    @Override
    public void sendEmailToResetPassword(String email, String newPassword) {
        String toAddress = email;
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Change-Password-Mail";
        String subject = "Account password have already been change!";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlContent = "<h2>Your account password have been change!</h2>"
                + "<p>Your email: " + email + "</p>"
                + "<p>Your new password: " + newPassword + "</p>"
                + "<br/><br/><p>Best regards,<br/>Your Company</p>";

        try {
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new ResourceInvalidException(e.getMessage(), "MimeMessageHelper.MimeMessageHelper-configure-fail");
        } catch (UnsupportedEncodingException e) {
            throw new ResourceInvalidException(e.getMessage(), "Unsupported-encoding-error");
        }

        javaMailSender.send(message);
    }

    @Override
    public void sendEmailToChangeEmail(String oldEmail, String newEmail, String siteURL) {
        String toAddress = newEmail;
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Change-Email-Mail";
        String subject = "Account email have already been change!";
        Account account = accountRepository.findByEmail(oldEmail);
        String verifyURL = siteURL + "/verify-change-email?code=" + tokenRepository.findByAccount(account).getToken() + "&email=" + newEmail;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlContent = "<h2>Your account email have been change, plz click this link below to verify!</h2>"
                + "<p>Link verify: " + verifyURL + "</p>"
                + "<br/><br/><p>Best regards,<br/>Your Company</p>";

        try {
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new ResourceInvalidException(e.getMessage(), "MimeMessageHelper.MimeMessageHelper-configure-fail");
        } catch (UnsupportedEncodingException e) {
            throw new ResourceInvalidException(e.getMessage(), "Unsupported-encoding-error");
        }

        javaMailSender.send(message);
    }
}
