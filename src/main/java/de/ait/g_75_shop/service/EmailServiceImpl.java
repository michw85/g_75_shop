package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.exceptions.types.EmailSendingException;
import de.ait.g_75_shop.service.interfaces.ConfirmationCodeService;
import de.ait.g_75_shop.service.interfaces.EmailService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of EmailService interface
 * Currently a stub - to be implemented with actual email sending logic
 *
 * Реализация интерфейса EmailService
 * В настоящее время заглушка - будет реализована с фактической логикой отправки email
 */
@Service
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;
    private final Configuration mailConfig;
    private final ConfirmationCodeService confirmationCodeService;
    private final String host;
    private final String port;
    private final String mailFrom;

    public EmailServiceImpl(
            ConfirmationCodeService confirmationCodeService,
            JavaMailSender mailSender,
            Configuration mailConfig,
            // хост, порт, почту отправителя берем из application-dev.yaml
            @Value("${server.host}") String host,
            @Value("${server.port}") String port,
            @Value("${spring.mail.username}") String mailFrom
    ) {
        this.confirmationCodeService = confirmationCodeService;
        this.mailSender = mailSender;
        this.mailConfig = mailConfig;
        this.host = host;
        this.port = port;
        this.mailFrom = mailFrom;

        // Configuration
        mailConfig.setDefaultEncoding("UTF-8");
        // Где искать шаблоны писем - /mail/ из папки resources
        // А занимается этим - EmailServiceImpl.
        // Загрузчик шаблонов:
        TemplateLoader loader = new ClassTemplateLoader(EmailServiceImpl.class, "/mail/");
        // Configuration ...
        mailConfig.setTemplateLoader(loader);
    }

    @Override
    public void sendConfirmationEmail(User user) {

        MimeMessage message = mailSender.createMimeMessage();
        // заполняем письмо с помощником
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        // текст письма генерируем:
        String text = generateConfirmationEmail(user);
        // наполняем письмо
        try {
            helper.setFrom(mailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("Registration confirmation");
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Email sending error", e);
        }

    }

    // Генерация текста письма
    private String generateConfirmationEmail(User user) {

        try {
            // Шаблон письма - Java-объект
            Template template = mailConfig.getTemplate("confirm_registration_mail.ftlh");
            // Генерим код подтверждения
            String code = confirmationCodeService.generateConfirmationCode(user);

            // ссылка о подтверждении регистрации
            // http://localhost:8081/users/confirm/f032scjs-sdad9asdas-asdasd
            // при клике на эту ссылку браузер будет отправлять GET-запрос на наш бэкенд
            String link = String.format("http://%s:%s/users/confirm/%s", host, port, code);

            // String - имя переменной
            Map<String, Object> mailValues = new HashMap<>();
            mailValues.put("name", user.getName());
            mailValues.put("link", link);

            // подставка всего в результат
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, mailValues);
        } catch (IOException | TemplateException e) {
            throw new EmailSendingException("Email text generation error", e);
        }
    }

    // Отправка письма пользователю


}
