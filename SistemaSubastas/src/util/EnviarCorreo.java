package util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * Clase para enviar correo
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 *
 */

public class EnviarCorreo
{
	
	/**
	 * M�todo para enviar el correo y generar la nueva constrase�a del usuario
	 * @param correo
	 * @return contrase�a nueva
	 */
	public static String sendEmail(String correo)
	{
		
		String passwordInt = "" + (int) (Math.random() * (100000000 - 100) + 100);
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");

		Session sesion = Session.getDefaultInstance(prop, null);

		try
		{
			Message mensaje = new MimeMessage(sesion);

			mensaje.setSubject("Nueva contrase�a - ACME inc");
			mensaje.setFrom(new InternetAddress("acme.unbosque@gmail.com"));
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
			
			
			mensaje.setText("Empresas ACME env�a su nueva contrase�a la cual es: " + passwordInt);
			Transport t = sesion.getTransport("smtp");
			t.connect("acme.unbosque@gmail.com", "acme12345");
			t.sendMessage(mensaje, mensaje.getAllRecipients());
			t.close();
		} catch (MessagingException me)
		{
		}
		return passwordInt;
	}
}
