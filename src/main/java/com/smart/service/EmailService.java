package com.smart.service;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String message, String to) {

        boolean f = false;

        String from = "rahulgupta707062@gmail.com";  // correct email
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("rahulgupta707062@gmail.com","rdfpqcsilptpusoh"); // Use App Password
            }
        });

        session.setDebug(true);

        try {
            MimeMessage m = new MimeMessage(session);
            
            //from email
            m.setFrom(new InternetAddress(from));
            
            //adding recipient to message 
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
         
            //adding subject to message
            m.setSubject(subject);
            
            //adding text to message
           // m.setText(message);
            m.setContent(message,"text/html");
            
            

            Transport.send(m);
            System.out.println("Sent success...");
            f = true;

        } catch (MessagingException e) {
            e.printStackTrace();
            f = false;
        }

        return f; // return the result
    }
}
