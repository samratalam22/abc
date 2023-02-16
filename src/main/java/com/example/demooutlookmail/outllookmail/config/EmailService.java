package com.example.demooutlookmail.outllookmail.config;

import com.example.demooutlookmail.outllookmail.Model.MailModel;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public static final String emailPassword = System.getenv("email_password");


    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("samrat.reddot@outlook.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        javaMailSender.send(message);
    }

    public List<MailModel> readMail() throws MessagingException, IOException {
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.imap.ssl.enable", "true");
            properties.setProperty("mail.store.protocol", "imap");
            properties.setProperty("mail.imap.ssl.enable", "true");
            properties.setProperty("mail.imaps.partialfetch", "false");
            properties.put("mail.mime.base64.ignoreerrors", "true");
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.debug", "true");
            properties.put("mail.pop3.host", "smtp.office365.com");
            properties.put("mail.pop3.port", "995");
            Session emailSession = Session.getDefaultInstance(properties);
            emailSession.setDebug(true);

            Store store = emailSession.getStore("imap");
            store.connect("smtp.office365.com", "samrat.reddot@outlook.com", emailPassword);


            Folder emailFolder = store.getFolder("Inbox");
            emailFolder.open(Folder.READ_WRITE);
            Message messages[] = emailFolder.getMessages();

            List<MailModel> listOfMail = new ArrayList<>();
            Iterable<MailModel> iterable = new ArrayList<>();

            System.out.println(messages.length);

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                MailModel mailModel = new MailModel();
                mailModel.setFrom(Arrays.toString(message.getFrom()));
                mailModel.setSubject(message.getSubject());
                Multipart multipart = (Multipart) message.getContent();

                mailModel.setMailBody(mailBodyRead(multipart)); //todo: here to parse mail body with format

                listOfMail.add(mailModel);
            }


            System.out.println(listOfMail);

            emailFolder.close(true);
            store.close();

            return listOfMail;
        } catch (NoSuchProviderException e) {
            System.out.println("NoSuchProviderException: " + e.getMessage());
            return null;
        } catch (MessagingException exception) {
            System.out.println("MessagingException: " + exception.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }

    }

    public List<String> mailBodyRead(Multipart multipart) throws MessagingException, IOException {
        List<String> result = new ArrayList<>();
        for (int x = 0; x < multipart.getCount(); x++) {
            BodyPart bodyPart = multipart.getBodyPart(x);

            String disposition = bodyPart.getDisposition();

            if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) {
                System.out.println("Mail have some attachment : ");

                DataHandler handler = bodyPart.getDataHandler();
                System.out.println("file name : " + handler.getName());
            } else {
                System.out.println(bodyPart.getContent());
                result.add((String) bodyPart.getContent());
            }
        }

        return result;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.office365.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("samrat.reddot@outlook.com");
        javaMailSender.setPassword("Sas1607022!");

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");


        return javaMailSender;
    }
}
