package com.voxtelecom;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailUtil {
	
	public static void sendMail(String recipient) {
		System.out.println("Preparing to send mail");
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mal.smtp.port", "587");
		
		String myAccountEmail = "abcdef@gmail.com";
		String password = "HelloWorld";
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myAccountEmail, password);
			}
		});
		
		Message message = prepareMessage(session, myAccountEmail, recipient);
		
		try {
			Transport.send(message);
			System.out.println("Message sent successfully");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private static Message prepareMessage(Session session, String myAccountEmail, String recipient) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myAccountEmail));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject("Password Reset");
			message.setText(recipient);
			return message;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
