package util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import dao.UserDAO;
import dao.UserDAOImpl;
import entity.User;

public class EnviarCorreo
{
	
	
	public static String sendEmail(String correo)
	{
		
		String passwordInt = "" + (int) (Math.random() * (100000 - 100) + 100) + "$";
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		Session sesion = Session.getDefaultInstance(prop, null);

		try
		{
			Message mensaje = new MimeMessage(sesion);

			mensaje.setSubject("Contraseña de login");
			mensaje.setFrom(new InternetAddress("acme.unbosque@gmail.com"));
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
			
			
			mensaje.setText("Su nueva contraseña es: " + passwordInt);
			Transport t = sesion.getTransport("smtp");
			t.connect("acme.unbosque@gmail.com", "acme12345");
			t.sendMessage(mensaje, mensaje.getAllRecipients());
			t.close();
		} catch (MessagingException me)
		{
			System.err.println(me.getMessage());
		}
		return "" + passwordInt;
	}
}
