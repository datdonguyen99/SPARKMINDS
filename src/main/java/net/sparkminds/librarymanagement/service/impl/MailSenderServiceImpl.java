package net.sparkminds.librarymanagement.service.impl;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Override
    public void sendVerificationEmail(String email, String siteURL) {
        Account account = userRepository.findByEmail(email);
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
}
