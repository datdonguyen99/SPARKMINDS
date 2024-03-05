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
import java.util.List;
import java.util.Map;

import static net.sparkminds.librarymanagement.utils.AppConstants.EMAIL_TEMPLATE_FILE_PATH;
import static net.sparkminds.librarymanagement.utils.AppConstants.SITE_URL;

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
    public void sendEmailToVerifyResetPassword(String oldEmail, String newPassword) {
        String toAddress = oldEmail;
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Change-Password-Mail";
        String subject = "Verify change account password";
        String verifyURL = SITE_URL + "/verify/reset-password?email=" + oldEmail + "&password=" + newPassword;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlContent = "<h2>Your account password have been change!</h2>"
                + "<p>Your email: " + oldEmail + "</p>"
                + "<p>Your new password: " + newPassword + "</p>"
                + "<br/>"
                + "<p>Click this link to verify change new password: " + verifyURL + "</p>"
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
    public void sendEmailToChangeEmail(String oldEmail, String newEmail, String token) {
        String toAddress = newEmail;
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Change-Email-Mail";
        String subject = "Account email have already been change!";
        String verifyURL = SITE_URL + "/verify/change-email?code=" + token + "&email=" + newEmail;

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

    @Override
    public void sendEmailToUserBorrowedBook(String email, String imgPath) {
        String toAddress = email;
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Borrow-Book-Mail";
        String subject = "Borrowed book!";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlContent = "<h2>Your account have been borrowed book successfully!</h2>"
                + "<p>Book's image: " + imgPath + "</p>"
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
    public void sendEmailMaintenanceToAllAccount(List<String> emails) {
        List<String> toAddresses = emails;
        String fromAddress = "datdn@automail.com";
        String senderName = "Automatic-Maintenance-Mode-Mail";
        String subject = "System Maintaining!";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String htmlContent = "<h2>System Maintenance Notification!</h2>"
                + "<p>Dear User, </p>"
                + "<p>This is to inform you that our system will be undergoing maintenance from [Start Time] to [End Time]. During this period, the system will be unavailable.</p>"
                + "<p>We apologize for any inconvenience this may cause and appreciate your patience.</p>"
                + "<p>Thank you,<br>"
                + "<br/><br/><p>Best regards,<br/>Your Company</p>";

        try {
            helper.setFrom(fromAddress, senderName);
            helper.setBcc(toAddresses.toArray(new String[0]));
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
