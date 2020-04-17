package cz.metacentrum.perun.spRegistration.service.mails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailsConfiguration {

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.smtp.auth}")
    private boolean auth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${mail.smtp.connectiontimeout}")
    private int connectiontimeout;

    @Value("${mail.smtp.timeout}")
    private int timeout;

    @Value("${mail.smtp.writetimeout}")
    private int writetimeout;

    @Value("${mail.debug}")
    private boolean debug;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.auth.username:@null}")
    private String username;

    @Value("${mail.auth.password:@null}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        if (username != null) {
            mailSender.setUsername(username);
        }

        if (password != null) {
            mailSender.setPassword(password);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.connectiontimeout", connectiontimeout);
        props.put("mail.smtp.timeout", timeout);
        props.put("mail.smtp.writetimeout", writetimeout);
        props.put("mail.debug", debug);

        return mailSender;
    }
}
