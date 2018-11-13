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
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */

public class EnviarCorreo
{
	
	/**
	 * Método para enviar el correo y generar la nueva constraseña del usuario
	 * @param correo
	 * @return contraseña nueva
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

			mensaje.setSubject("Nueva contraseña - ACME inc");
			mensaje.setFrom(new InternetAddress("acme.unbosque@gmail.com"));
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
			
			
			mensaje.setText("Empresas ACME envía su nueva contraseña la cual es: " + passwordInt);
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
