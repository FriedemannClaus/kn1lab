import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class Receive_Mail {
	public static void main(String[] args) throws Exception {
		fetchMail();
	}
	
	public static void fetchMail() {
		try {
			Properties properties = new Properties();
			properties.put("mail.pop3.host", "localhost");
			properties.put("mail.pop3.port", "110");
			properties.put("mail.pop3.starttls.enable", "false");
			properties.put("mail.store.protocol", "pop3");
			Session session = Session.getInstance(properties);
			Store store = session.getStore("pop3");
			store.connect("localhost", "labrat", "kn1lab");
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);

			Message[] messages = folder.getMessages();
			System.out.println("Nachrichtenanzahl: " + messages.length);
			for (int i = 0; i < messages.length; i++) {
				StringBuilder builder = new StringBuilder(i + ": ");
				builder.append("Von: ")
						.append(messages[i].getFrom()[0].toString())
						.append(" Betreff: ")
						.append(messages[i].getSubject())
						.append(" Versanddatum: ")
						.append(messages[i].getSentDate())
						.append(" Inhalt: ")
						.append(messages[i].getContent().toString());
				System.out.println(builder);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
