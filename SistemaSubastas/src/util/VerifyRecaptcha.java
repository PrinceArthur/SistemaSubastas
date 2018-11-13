package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * 
 * Clase de verificación del reCAPTCHA google
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */

public class VerifyRecaptcha
{
	/**
	 * Dirección de verificación
	 */
	public static final String url = "https://www.google.com/recaptcha/api/siteverify";

	/**
	 * Llave secreta
	 */
	public static final String secret = "6Le1UXcUAAAAAPMYEOMGVTuWJxH0hwua-3qMz7Wd";
	
	/**
	 * Agente
	 */
	private final static String USER_AGENT = "Mozilla/5.0";

	/**
	 * Método para verificar el recaptcha
	 * @param gRecaptchaResponse
	 * @return
	 * @throws IOException
	 */
	public static boolean verify(String gRecaptchaResponse) throws IOException
	{
		if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse))
		{
			return false;
		}
		try
		{
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + postParams);
			System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();
			JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

}
