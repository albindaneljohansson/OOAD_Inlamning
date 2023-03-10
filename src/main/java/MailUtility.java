import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailUtility {

    public static void sendMail(String recipient, String subject, String text) throws MessagingException {
        System.out.println("Preparing to send e-mail...");
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String myAccountEmail = "albin.java@gmail.com";
        String password = "xxxxxxxxxxx";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        Message message = prepareMessage(session, myAccountEmail, recipient, subject, text);
        Transport.send(message);
        System.out.println("E-mail sent!");
    }
    private static Message prepareMessage(Session session, String myAccountEmail, String recipient, String subject, String text) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(text);
            return message;
        } catch (AddressException ex) {
            Logger.getLogger(MailUtility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

