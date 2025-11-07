package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private static final String FROM_EMAIL = "pratikparhad27@gmail.com";  // your gmail
    private static final String PASSWORD = "xsglhvwufglqesyo";       // 16-char app password

    public static void sendEmail(String toEmail, String subject, String messageBody) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            System.out.println("✅ Email sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public static void sendAlertEmail(String toEmail, String city, String disasterType, String details) {
        String subject = "⚠️ Disaster Alert: " + disasterType + " near " + city;
        String message = "Dear User,\n\nA " + disasterType + " has been detected near your location (" + city + ").\n" +
                "Details: " + details + "\n\nPlease stay safe and follow official updates.\n\n— Disaster Dashboard System";
        sendEmail(toEmail, subject, message);
    }
}
