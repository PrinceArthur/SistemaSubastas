package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Cifra las contraseñas
 * @author 
 */
public class Cifrado
{
	/**
	 * Algoritmo MD5
	 */
	public static String MD5 = "MD5";

	/**
	 * Pasa texto a hexadecimal
	 * 
	 * @param digest
	 * @return hash
	 */
	private static String toHexadecimal(byte[] digest)
	{
		String hash = "";
		for (byte aux : digest)
		{
			int b = aux & 0xff;
			if (Integer.toHexString(b).length() == 1)
				hash += "0";
			hash += Integer.toHexString(b);
		}
		return hash;
	}

	/**
	 * Devuelve el cifrado en una cadena de caracteres
	 * @param message
	 * @param algorithm
	 * @return
	 */
	public static String getStringMessageDigest(String message, String algorithm)
	{
		byte[] digest = null;
		byte[] buffer = message.getBytes();
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.reset();
			messageDigest.update(buffer);
			digest = messageDigest.digest();
		} catch (NoSuchAlgorithmException ex)
		{
		}
		return toHexadecimal(digest);
	}

}