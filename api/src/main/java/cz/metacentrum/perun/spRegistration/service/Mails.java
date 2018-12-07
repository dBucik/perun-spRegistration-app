package cz.metacentrum.perun.spRegistration.service;

import cz.metacentrum.perun.spRegistration.persistence.enums.RequestStatus;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

@SuppressWarnings("Duplicates")
public class Mails {

	private static final String HOST_KEY = "host";
	private static final String FROM_KEY = "from";
	private static final String CREATE_SUBJECT_KEY = "create.header";
	private static final String CREATE_MESSAGE_KEY = "create.message";
	private static final String UPDATE_SUBJECT_KEY = "update.header";
	private static final String UPDATE_MESSAGE_KEY = "update.message";
	private static final String FOOTER_KEY = "footer";

	private static final String REQUEST_ID_FIELD = "%REQUEST_ID%";
	private static final String NEW_STATUS_FIELD = "%NEW_STATUS%";
	private static final String SERVICE_NAME_FIELD = "%SERVICE_NAME%";


	private static boolean sendMail(String host, String from, List<String> to, String subject, String msg) {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(props);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			StringJoiner recipients = new StringJoiner(",");
			for (String recipient: to) {
				recipients.add(recipient);
			}
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients.toString()));

			message.setSubject(subject);

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			Transport.send(message);
		} catch (MessagingException e) {
			return false;
		}

		return true;
	}

	public static boolean updateStatusMail(Long requestId, RequestStatus status, List<String> recipients, Properties props) {
		String host = props.getProperty(HOST_KEY);
		String from = props.getProperty(FROM_KEY);

		String subject = props.getProperty(UPDATE_SUBJECT_KEY);
		if (subject.contains(REQUEST_ID_FIELD)) {
			subject = subject.replaceAll(REQUEST_ID_FIELD, requestId.toString());
		}

		String message = props.getProperty(UPDATE_MESSAGE_KEY);
		if (message.contains(REQUEST_ID_FIELD)) {
			message = message.replaceAll(REQUEST_ID_FIELD, requestId.toString());
		}
		if (message.contains(NEW_STATUS_FIELD)) {
			message = message.replaceAll(NEW_STATUS_FIELD, status.toString());
		}
		message = message.concat("\n").concat(props.getProperty(FOOTER_KEY));

		return sendMail(host, from, recipients, subject, message);
	}

	public static boolean createRequestMail(Long requestId, String serviceName, List<String> recipients, Properties props) {
		String host = props.getProperty(HOST_KEY);
		String from = props.getProperty(FROM_KEY);

		String subject = props.getProperty(CREATE_SUBJECT_KEY);
		if (subject.contains(REQUEST_ID_FIELD)) {
			subject = subject.replaceAll(REQUEST_ID_FIELD, requestId.toString());
		}

		String message = props.getProperty(CREATE_MESSAGE_KEY);
		if (message.contains(REQUEST_ID_FIELD)) {
			message = message.replaceAll(REQUEST_ID_FIELD, requestId.toString());
		}

		if (message.contains(SERVICE_NAME_FIELD)) {
			message = message.replaceAll(SERVICE_NAME_FIELD, serviceName);
		}
		message = message.concat("\n").concat(props.getProperty(FOOTER_KEY));

		return sendMail(host, from, recipients, subject, message);
	}

	public static boolean moveToProductionMail() {
		//TODO
		return false;
	}
}
