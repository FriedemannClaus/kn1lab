import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage; 

public class Send_Mail {
	public static void main(String[] args) {
		sendMail();   
	}
	
	public static void sendMail() {
		try {
			String msgSubject = "Die beste BESTE Mail";
			String msgRecipient = "labrat@localhost";
			String msgTxt = "Es gibt nichts zu sagen. ";

			try {
				Properties prop = new Properties();
				prop.put("mail.smtp.host", "localhost");
				prop.put("mail.smtp.port", "25");
				prop.put("mail.from", "ich_bin@bundeskanzler.de");
				Session session = Session.getInstance(prop);

				Message msg = new MimeMessage(session);
				msg.setSubject(msgSubject);
				msg.setSentDate(new Date());
				msg.setFrom();
				msg.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(msgRecipient, false));
				msg.setText(msgTxt);
				Transport.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
