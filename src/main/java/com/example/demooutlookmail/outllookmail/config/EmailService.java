package com.example.demooutlookmail.outllookmail.config;

import com.example.demooutlookmail.outllookmail.Model.MailModel;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public static final String emailPassword = System.getenv("email_password");


    public void sendEmail(String to, String subject, String body, MultipartFile file) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("samrat.reddot@outlook.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        if (!file.isEmpty()) {
            helper.addAttachment(file.getOriginalFilename(), file);
        }

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
                mailModel.setTo("samrat.reddot@outlook.com");
                mailModel.setFrom(message.getFrom()[0].toString());
                mailModel.setSubject(message.getSubject());

                mailModel.setMailBody(getTextFromMail(message)); //todo: here to parse mail body with format

                listOfMail.add(mailModel);
            }


            //System.out.println(listOfMail);

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


    private String getTextFromMail(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }
        if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
            }
            result += this.parseBodyPart(bodyPart);
        }
        return result;
    }

    private String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/html")) {
            return "\n" + org.jsoup.Jsoup
                    .parse(bodyPart.getContent().toString())
                    .text();
        }
        if (bodyPart.getContent() instanceof MimeMultipart) {
            return getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }

        return "";
    }


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.office365.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("samrat.reddot@outlook.com");
        javaMailSender.setPassword(emailPassword);

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false");


        return javaMailSender;
    }
}
