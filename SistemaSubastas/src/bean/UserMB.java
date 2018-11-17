package bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.lf5.viewer.LogFactor5InputDialog;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.log.SysoCounter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

import util.Cifrado;
import util.EnviarCorreo;
import util.VerifyRecaptcha;
import entity.Audit;
import entity.Offerersale;
import entity.Salesueb;
import entity.User;
import service.AuditService;
import service.OfferersaleService;
import service.ParameterService;
import service.SalesuebService;
import service.UserService;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez Managed Bean que se
 *         encarga de gestionar los usuarios y egenrar reportes.
 *
 */

@ManagedBean
@SessionScoped
public class UserMB
{
	/**
	 * Instancia de el bean de Auditorias
	 */
	private AuditMB audit = new AuditMB();

	/**
	 * Usuario principal
	 */
	private User user;

	/**
	 * Usuario que se utiliza al realizar un login
	 */
	private User loginUser = new User();

	/**
	 * Usuario administrador
	 */
	private User userAdmin = new User();

	/**
	 * Usuario oara generar nueva contrase�a
	 */
	private User userPass = new User();

	/**
	 * DataModel que lista los usaurios del sistema
	 */
	private DataModel listaUser;

	/**
	 * DataModel que lista los usuarios de tipo proveedor.
	 */
	private DataModel listaProveedor;

	/**
	 * DataModel que lista las subastas en el sistema
	 */
	private DataModel listaSubastas;

	/**
	 * DataModel que lista las ofertas hechas por un postor especificado
	 */
	private DataModel listaOfertaPostor;
	
	private List listaOfertaPostorRangoFechas;
	
	private List listaProveedores;

	/**
	 * DataModel que lista las ofertas de una subasta
	 */
	private DataModel listaOfertaSubasta;

	/**
	 * DataModel que lista las subastas en estado activo
	 */
	private DataModel listaSubastasActivas;

	/**
	 * Mensaje de error para los validaciones del cliente
	 */
	private String mensajeError;

	/**
	 * Primera parte del email
	 */
	private String email1;

	/**
	 * Segunda parte del email
	 */
	private String email2;

	/**
	 * Subasta utilizada en el sistema
	 */
	private Salesueb sale;

	/**
	 * Para obtener el userName de un usuario
	 */
	private String nombre;

	/**
	 * Instancia del bean de ofertas
	 */
	private Offerersale oferta = new Offerersale();

	/**
	 * Para obtener la id de la subasta.
	 */
	private int idSale;
	
	private Date inicio;
	private Date fin;
	private String userPostor;
	private StreamedContent file;

	/**
	 * Logger del sistema
	 */
	private static Logger logger = Logger.getLogger(UserMB.class);

	/**
	 * Construstor de la clase
	 */
	public UserMB()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
	}

	/**
	 * M�todo que inicializa el usuario para ser adicionado
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararAdicionarUser()
	{
		logger.trace("Entra al m�todo prepararAdicionarUser");
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("proveedor");
		Date now = new Date();
		user.setDateLastPassword(now);
		email1 = "";
		email2 = "";
		logger.info(
				"Objeto user inicializado para agregar Proveedor y nos dirige a /administrador/registrarProvedor.xhtml");
		return "/administrador/registrarProvedor.xhtml";
	}

	/**
	 * M�todo que inicializa el usuario postor para ser adicionado
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararAdicionarPostor()
	{
		logger.trace("Entra al m�todo prepararAdicionarPostor");
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("postor");
		Date now = new Date();
		user.setDateLastPassword(now);
		logger.info("Objeto user inicializado para agregar Postor y nos dirige a /postor/registrarPostor.xhtml");
		return "/postor/registrarPostor.xhtml";
	}

	/**
	 * M�todo que inicializa el usuario desde la lista para ser modificado
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararModificarUser()
	{
		logger.trace("Entra al m�todo prepararModificarUser");
		user = (User) (listaUser.getRowData());
		String correo = user.getEmailAddress();
		String[] x = correo.split("@");
		email1 = x[0];
		email2 = x[1];
		logger.info(
				"Objeto user inicializado con la fila de la listaUser y nos dirige a la p�gina /administrador/modificarProveedor");
		return "/administrador/modificarProveedor";
	}

	/**
	 * M�todo que inicializa el usuario administrador para obtener sus datos
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararAdmin()
	{
		logger.trace("Entra al m�todo prepararAdmin");
		UserService service = new UserService();
		userAdmin = service.getUser("admin");
		logger.info(
				"objeto userAdmin iniicializado con el usuario de tipo admin y nos dirige a /administrador/datosAdmin");
		return "/administrador/datosAdmin";
	}

	/**
	 * M�todo que inicializa el userPass para recuperar la contrase�a
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararRecuperarContrase�a()
	{
		logger.trace("Entra al m�todo prepararRecuperarContrase�a");
		userPass = new User();
		logger.info("Objeto userPass inicializado y nos dirige a la p�gina recuperarContrase�a");
		return "recuperarContrase�a";
	}

	/**
	 * M�todo que inicializa el userPass desde a lista para cambiar su contrase�a
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararCambioContrase�a()
	{
		logger.trace("Entra al m�todo prepararCambioContrase�a");
		UserService service = new UserService();
		userPass = service.getUser(loginUser.getUserName());
		userPass.setPassword("");
		logger.info("Objeto userPass inicializado con el user de loginUser y nos dirige a cambiarContrase�a");
		return "cambiarContrase�a";
	}

	/**
	 * M�todo que inicializa el usuario proveedor desde la lista para su ingreso
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararIngresoProveedor()
	{
		logger.trace("Entra al m�todo prepararIngresoProveedor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaProveedor = inicializarListaProveedor(loginUser.getUserName());
		logger.info(
				"Objeto user inicializado por el user de loginUser y listaProveedor incializada. Nos dirige a /proveedor/indexProveedor");
		return "/proveedor/indexProveedor";
	}

	/**
	 * M�todo que inicializa el usuario proveedor desde las lista para obtener sus
	 * datos
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararDatosProveedor()
	{
		logger.trace("Entra al m�todo prepararDatosProveedor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		logger.info(
				"Objeto user inicializado al user de loginUser y  nos dirige a la p�gina /proveedor/datosProveedor");
		return "/proveedor/datosProveedor";
	}

	/**
	 * M�todo que inicializa el usuario postor desde la lista para su ingreso
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararIngresoPostor()
	{
		logger.trace("Entra al m�todo prepararIngresoPostor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaOfertaPostor = inicializarListaOfertaPostor(loginUser.getUserName());
		logger.info(
				"Objeto user inicializado al user de loginUser y listaOfertaPostor inicializadas a la p�gina /postor/indexPostor");
		return "/postor/indexPostor";
	}

	/**
	 * M�todo que inicializa el usuario para ser adicionado
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararDatosPostor()
	{
		logger.trace("Entra al m�todo prepararDatosPostor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		logger.info("Ojeto user inicializado con el user de loginUser y nos dirige a la p�gina /postor/datosPostor");
		return "/postor/datosPostor";
	}

	/**
	 * M�todo que inicializa la subasta para ser adicionada
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararAdicionarSubasta()
	{
		logger.trace("Entra a m�todo prepararAdicionarSubasta");
		ParameterService serviceP = new ParameterService();
		sale = new Salesueb();
		nombre = user.getUserName();
		sale.setPhotoProduct(serviceP.getParameter("RutaImagen").getTextValue());
		logger.info(
				"Objeto sale inicializado con Salesueb, objeto nombre inicializado con el userName de user y cambiamos la ruta de la imagen con el para metro 'Ruta imagen'. Nos dirige a la p�gina /proveedor/nuevaSubasta ");
		return "/proveedor/nuevaSubasta";

	}

	/**
	 * M�todo que inicializa la subasta desde la lista para obtener sus datos
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String prepararSubasta()
	{
		logger.trace("Entra al m�todo prepararSubasta");
		sale = (Salesueb) listaSubastasActivas.getRowData();
		logger.info("Objeto sale inicializado con la columna de la lista y nos dirige a la p�gina /postor/subasta ");
		return "/postor/subasta";
	}

	/**
	 * M�todo que inicializa la oferta para ser adicionada
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public void prepararAgregarOferta()
	{
		logger.trace("Entra al m�todo prepararAgregarOferta");
		idSale = sale.getId();
		oferta.setDateOffer(new Date());
		nombre = loginUser.getUserName();
		logger.info(
				"Se incializa el objeto idSale con el id de la subasta, se asigna la fefcha a la oferta y asignamos a la variable nombre el valor del userName de loginUser.");
	}
	
	public void prepararReportesDeSubasta()
	{
		sale = (Salesueb) getListaSubastas().getRowData();
		inicializarListaOfertas(sale.getId());
	}

	/**
	 * M�todo que adiciona los usuarios administrador y proveedor
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String adicionarUser()
	{
		logger.trace("Entra al m�todo adicionarUser");
		UserService service = new UserService();
		user.setEmailAddress(email1 + email2);
		boolean repetido = false;
		Iterator<User> it = getListarUser().iterator();
		while (it.hasNext() && repetido == false)
		{
			User x = it.next();

			if (x.getUserName().equals(user.getUserName()) || x.getEmailAddress().equals(email1 + email2))
			{
				repetido = true;
			}
		}

		if (repetido == false)
		{
			user.setDateLastPassword(new Date());
			user.setEmailAddress(email1 + email2);
			String pass = EnviarCorreo.sendEmail(user.getEmailAddress());
			user.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			service.nuevo(user);

			logger.info("Usuario agregado a la base de datos");

			audit.adicionarAudit("Admin", "CREATE", "User", user.getId());

		} else if (repetido)
		{
			mensajeError = "Ese Usuario ya existe";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("El usuario ya existe.");
			return "";
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		return "/administrador/indexAdmin";
	}

	/**
	 * M�todo que modifica los usuarios
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String modificarUser()
	{
		logger.trace("Entra al m�todo modificarUser");
		UserService service = new UserService();
		user.setEmailAddress(email1 + email2);
		service.actualizar(user);
		audit.adicionarAudit("Admin", "UPDATE", "User", user.getId());
		logger.info("Actualizamos el usuario en la base de datos ");
		return "/administrador/indexAdmin";
	}

	/**
	 * M�todo que elimina los usuarios
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public void eliminarUser()
	{
		logger.trace("Entramos al m�todo eliminarUser");
		User usuarioTemp = (User) (listaUser.getRowData());
		UserService service = new UserService();
		Date now = new Date();
		if (usuarioTemp.getActive().equalsIgnoreCase("ACTIVE"))
		{
			usuarioTemp.setActive("INACTIVE");
			audit.adicionarAudit("Admin", "DELETE", "User", usuarioTemp.getId());
			logger.info("Estado del usuario cambiado a INACTIVO");
		} else if (usuarioTemp.getActive().equalsIgnoreCase("INACTIVE"))
		{
			String pass = EnviarCorreo.sendEmail(usuarioTemp.getEmailAddress());
			usuarioTemp.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			usuarioTemp.setFailedAttempts(0);
			usuarioTemp.setDateLastPassword(now);
			usuarioTemp.setActive("ACTIVE");
			logger.info("Estado del usuario cambiado a ACTIVO");
		}
		service.actualizar(usuarioTemp);
	}

	/**
	 * M�todo para iniciar sesi�n
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String login()
	{

		logger.trace("M�todo de login");

		String pagina = "";
		UserService service = new UserService();
		User usuarioTemp = service.getUser(loginUser.getUserName());
		ParameterService serviceP = new ParameterService();
		int dias = (int) ((new Date().getTime() - usuarioTemp.getDateLastPassword().getTime()) / 86400000);
		boolean existe = false;

		Iterator<User> it = getListarUser().iterator();
		while (it.hasNext() && existe == false)
		{
			if (it.next().getUserName().equals(loginUser.getUserName()))
			{
				existe = true;
			}
		}

		if (existe)
		{

			logger.info("El usuario si existe");

			if (usuarioTemp.getPassword().endsWith("$") || (dias >= serviceP.getParameter("Fecha").getNumberValue()
					&& serviceP.getParameter("Fecha").getState().equalsIgnoreCase("ACTIVE")))
			{
				try
				{
					String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
							.getRequestParameterMap().get("g-recaptcha-response");
					boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
					if (verify)
					{
						usuarioTemp.setFailedAttempts(0);
						service.actualizar(usuarioTemp);
						logger.info(
								"El usuario debe cambiar la contrasela por ingrresar con constrase�a generada por el administrador o por que se vencio el tiempo para el cambio.");
						pagina = prepararCambioContrase�a();
					} else
					{
						mensajeError = "Verificaci�n del CAPTCHA invalida";
						logger.error("Validaci�n del CAPTCHA incorrecta");
					}
				} catch (Exception e)
				{
					logger.error("Validaci�n del CAPTCHA incorrecta");
				}
			} else if (usuarioTemp.getPassword()
					.equals(Cifrado.getStringMessageDigest(loginUser.getPassword(), Cifrado.MD5))
					&& usuarioTemp.getActive().equals("ACTIVE"))
			{

				if (usuarioTemp.getUserType().equalsIgnoreCase("PROVEEDOR"))
				{
					try
					{
						String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
								.getRequestParameterMap().get("g-recaptcha-response");
						boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
						if (verify)
						{
							usuarioTemp.setFailedAttempts(0);
							service.actualizar(usuarioTemp);
							logger.info("Ingresa un usuario de tipo PROVEEDOR");
							pagina = prepararIngresoProveedor();
						} else
						{
							mensajeError = "Verificaci�n del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
						logger.error("Validaci�n del CAPTCHA incorrecta");
					}
				} else if (usuarioTemp.getUserType().equalsIgnoreCase("POSTOR"))
				{
					try
					{
						String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
								.getRequestParameterMap().get("g-recaptcha-response");
						boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
						if (verify)
						{
							usuarioTemp.setFailedAttempts(0);
							service.actualizar(usuarioTemp);
							logger.info("Ingresa un usuario de tipo POSTOR");
							pagina = prepararIngresoPostor();
						} else
						{
							mensajeError = "Verificaci�n del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
						logger.error("Validaci�n del CAPTCHA incorrecta");
					}
				} else if (usuarioTemp.getUserType().equalsIgnoreCase("ADMIN"))
				{
					try
					{
						String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
								.getRequestParameterMap().get("g-recaptcha-response");
						boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
						if (verify)
						{
							usuarioTemp.setFailedAttempts(0);
							service.actualizar(usuarioTemp);
							logger.trace("Ingresa el ADMINISTRADOR");
							pagina = "/administrador/inicioAdmin";
						} else
						{
							mensajeError = "Verificaci�n del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
						logger.error("Validaci�n del CAPTCHA incorrecta");
					}
				}

			} else if (!usuarioTemp.getPassword()
					.equals(Cifrado.getStringMessageDigest(loginUser.getPassword(), Cifrado.MD5)))
			{
				if (usuarioTemp.getFailedAttempts() < serviceP.getParameter("Intentos").getNumberValue()
						&& serviceP.getParameter("Intentos").getState().equalsIgnoreCase("ACTIVE"))
				{
					mensajeError = "Contrase�a o Usuario inv�lido";
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
					usuarioTemp.setFailedAttempts(usuarioTemp.getFailedAttempts() + 1);
					service.actualizar(usuarioTemp);
					logger.warn("El usuario ingreso mal su contrase�a");
					audit.adicionarAudit(usuarioTemp.getUserName(), "FAILLOGIN", "User", 0);
				}
			}

			if (usuarioTemp.getFailedAttempts() == serviceP.getParameter("Intentos").getNumberValue()
					|| usuarioTemp.getActive().equals("INACTIVE"))
			{
				usuarioTemp.setActive("INACTIVE");
				service.actualizar(usuarioTemp);
				audit.adicionarAudit(usuarioTemp.getUserName(), "DELETELOGIN", "User", 0);
				mensajeError = "Su cuenta se encuentra bloqueada, por favor comuniquese con un administrador.";
				FacesContext context = FacesContext.getCurrentInstance();
				logger.warn("El usuario se ha bloquedo por exceder intentos de ingreso");
				context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			}

			audit.adicionarAudit(usuarioTemp.getUserName(), "LOGIN", "---", 0);

		} else
		{
			mensajeError = "Contrase�a o Usuario inv�lido";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("Est� ingresando un usuario que no est� registrado");
		}

		return pagina;
	}

	/**
	 * M�todo para hacer el cambio obligatorio de contrase�a
	 * 
	 * @throws IOException
	 */

	public void recuperarContrase�a() throws IOException
	{
		logger.trace("Entra la m�todo recuperarContrase�a");
		UserService service = new UserService();
		String pass = userPass.getPassword();
		userPass.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5));
		userPass.setDateLastPassword(new Date());
		logger.info("Sele genera una nueva contrase�a al usuario y es enviada al correo que est� registrado");
		service.actualizar(userPass);

		audit.adicionarAudit(userPass.getUserName(), "UPDATE", "User", userPass.getId());

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
	}

	/**
	 * M�todo para link De olvidar contrase�a
	 * 
	 * @throws IOException
	 */

	public void cambiarContrase�a() throws IOException
	{
		logger.trace("Entramos al m�todo cambiarContrase�a");
		UserService service = new UserService();
		User userTemp = new User();
		userPass.setEmailAddress(email1 + email2);
		Date date = new Date();
		String usu = "";

		boolean existe = false;
		Iterator<User> it = getListarUser().iterator();

		while (it.hasNext() && existe == false)
		{
			User x = it.next();
			if (x.getEmailAddress().equals(userPass.getEmailAddress()))
			{
				existe = true;
				usu = x.getUserName();
			}
		}

		userTemp = service.getUser(usu);

		if (existe)
		{
			String pass = EnviarCorreo.sendEmail(userTemp.getEmailAddress());
			userTemp.setDateLastPassword(date);
			userTemp.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			userTemp.setFailedAttempts(0);
			service.actualizar(userTemp);
			logger.info("El usuario fue encontrado por su correo y se le ha enviado la nueva contrase�a la correo.");
			audit.adicionarAudit(userTemp.getUserName(), "UPDATE", "User", userTemp.getId());

		} else
		{
			mensajeError = "No se encontr� un usuario con ese correo.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("No se encontr� ningun usuario por ese correo");
		}
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");

	}

	/**
	 * M�todo que cierra la sesi�n de un usuario
	 */
	public void logOut() throws IOException
	{

		logger.trace("Entra al m�todo logOut");

		UserService service = new UserService();
		User usuarioTemp = service.getUser(loginUser.getUserName());

		audit.adicionarAudit(usuarioTemp.getUserName(), "LOGOUT", "---", 0);

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");

		logger.info("El usuario cierra seci�n");
	}

	/**
	 * M�todo que crea una oferta nueva
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String agregarOferta()
	{
		logger.trace("Entra al m�todo agregarOferta");
		OfferersaleService service = new OfferersaleService();
		oferta.setIdentification(nombre);

		if (oferta.getValueOffer() <= sale.getValueBase() && oferta.getValueOffer() <= sale.getValueCurrent())
		{
			mensajeError = "La oferta debe ser mayor que la oferta actual";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("La oferta ingresada no es valida por ser menor al valor base o el valor actual");
		} else
		{
			sale.setValueCurrent(oferta.getValueOffer());
			oferta.setIdSales(sale.getId());
			oferta.setWinner("WINNER");
			actualizarOfertas(sale.getId());
			modificarSubasta();
			service.nuevo(oferta);
			audit.adicionarAudit(nombre, "CREATE", "Offerersales", 0);
			audit.adicionarAudit(nombre, "UPDATE", "Salesueb", sale.getId());
			logger.info("Se crea una nueva oferta y se actualiza el valor actual en la subasta");
		}
		return "/postor/subasta";
	}

	/**
	 * M�todo que actualiza las ofertas
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public void actualizarOfertas(int idSales)
	{
		logger.trace("Entra al m�todo actualizarOfertas");
		OfferersaleService service = new OfferersaleService();
		List<Offerersale> listaOfertas = new OfferersaleService().getOfertaDeSubasta(idSales);

		Iterator<Offerersale> it = listaOfertas.iterator();
		while (it.hasNext())
		{
			Offerersale m = it.next();
			m.setWinner("LOSER");
			service.actualizar(m);
		}

		logger.info("Actualiza el estado de todas las oferta a LOSER para que la nueva oferta sea la gabadora");

	}

	/**
	 * M�todo que modifica las subastas
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public void modificarSubasta()
	{
		logger.trace("Entra al m�todo modificarSubasta");
		SalesuebService service = new SalesuebService();
		service.actualizar(sale);
		logger.info("Se modifica la subasta");
	}

	/**
	 * M�todo que crea las subastas
	 * 
	 * @return redirecci�n de la p�gina
	 */
	public String adicionarSubasta()
	{
		logger.trace("Entra al m�todo adicionarSubasta");
		SalesuebMB saleB = new SalesuebMB();

		int dias = (int) ((sale.getDateEnd().getTime() - sale.getDateStart().getTime()) / 86400000);

		if (dias < 0)
		{
			mensajeError = "La fecha de inicio debe ser menor que la fecha final";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("La fecha final de la Subasta debe ser mayor que la de inicio");
			return "";
		} else
		{
			saleB.agregarSubasta(nombre, sale.getDateStart(), sale.getDateEnd(), sale.getPhotoProduct(),
					sale.getDescriptionProduct(), sale.getName(), sale.getValueBase());
			int id = darIdNuevaSubasta(nombre);
			audit.adicionarAudit(nombre, "CREATE", "Salesueb", id);
			logger.info("Se crea una nueva subasta");
			return "/proveedor/indexProveedor";
		}
	}
	
	public int darIdNuevaSubasta(String name)
	{
		int id = 0;
		
		SalesuebService servicio = new SalesuebService();
		List<Salesueb> temp = servicio.lista();
		
		id = temp.size();
		
		return id;
	}

	/**
	 * M�todo que crea un usuario postor
	 */
	public void adicionarPostor()
	{
		logger.trace("Entra al m�todo adicionarPostor");
		UserService service = new UserService();
		boolean repetido = false;
		Iterator<User> it = getListarUser().iterator();
		while (it.hasNext() && repetido == false)
		{
			if (it.next().getUserName().equals(user.getUserName()))
			{
				repetido = true;
			}
		}

		logger.info("Verifica si el usuario no est� registrado en el sistema");

		if (repetido == false)
		{
			user.setDateLastPassword(new Date());
			user.setEmailAddress(email1 + email2);
			String pass = EnviarCorreo.sendEmail(user.getEmailAddress());
			user.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			service.nuevo(user);

			logger.info("Se ha enviado contrase�a del nuevo usuario a su correo");

			audit.adicionarAudit("Admin", "CREATE", "User", user.getId());

		} else if (repetido)
		{
			logger.warn("El usuario ya se encuentra en el sistema y no se puede resitrar");
			mensajeError = "Ese Usuario ya existe";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		try
		{
			ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	// REPORTES EN EXCEL ----------------------------------------------------------------
	

	/**
	 * M�todo que genera el reporte en formato excel de las ofertas en un rango de fechas
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void excelOfertasFechas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra al m�todo excelOfertasFechas");
		List<Offerersale> oferta = getListaOfertaPostorRangoFechas();

		HSSFWorkbook libro = new HSSFWorkbook();

		HSSFSheet hoja = libro.createSheet();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
		String date = sdf.format(new Date());
		
		Row row  =  hoja.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(date);
		
		hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
		HSSFRow fila = hoja.createRow(0);

		HSSFCell id = fila.createCell((short) 0);
		HSSFCell operationCrud = fila.createCell((short) 1);
		HSSFCell tableName = fila.createCell((short) 2);
		HSSFCell tableId = fila.createCell((short) 3);
		HSSFCell createDate = fila.createCell((short) 4);

		id.setCellValue(new HSSFRichTextString("ID"));
		operationCrud.setCellValue(new HSSFRichTextString("Producto"));
		tableName.setCellValue(new HSSFRichTextString("Valor Oferta"));
		tableId.setCellValue(new HSSFRichTextString("Fecha oferta"));
		createDate.setCellValue(new HSSFRichTextString("Ganador"));
		
		String producto = "";
		SalesuebService service = new SalesuebService();
		
		for (int i = 0; i < oferta.size(); ++i)
		{
			HSSFRow dataRow = hoja.createRow(i + 1);
			producto = service.listaSalesuebID(oferta.get(i).getIdSales()).getName();
			dataRow.createCell(0).setCellValue(oferta.get(i).getId());
			dataRow.createCell(1).setCellValue(producto);
			dataRow.createCell(2).setCellValue(oferta.get(i).getValueOffer());
			dataRow.createCell(3).setCellValue(oferta.get(i).getDateOffer().toString());
			dataRow.createCell(4).setCellValue(oferta.get(i).getWinner());
		}

		try
		{
			FileOutputStream elFichero = new FileOutputStream(
					"C://ReportesGeneradosSistemaSubastas/Reporte de ofertas - " + user.getUserName() + ".xls");
			libro.write(elFichero);
			// Desktop.getDesktop().open(new
			// File("C://ReportesGeneradosSistemaSubastas/"+crud+".xls"));
			elFichero.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		logger.info("Se genera el archivo excel de las ofertas");
	}

	/**
	 * M�todo que genera el reporte en formato excel de las subastas
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void archivoExcelSubastas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra al m�todo archivoExcelSubastas");

		SalesuebService service = new SalesuebService();
		List<Salesueb> subasta = service.getSalesueb(loginUser.getUserName());

		HSSFWorkbook libro = new HSSFWorkbook();

		HSSFSheet hoja = libro.createSheet();

		HSSFRow fila = hoja.createRow(0);

		// Se crea una celda dentro de la fila
		HSSFCell id = fila.createCell((short) 0);
		HSSFCell name = fila.createCell((short) 1);
		HSSFCell descripcion = fila.createCell((short) 2);
		HSSFCell base = fila.createCell((short) 3);
		HSSFCell current = fila.createCell((short) 4);
		HSSFCell fechaStar = fila.createCell((short) 5);
		HSSFCell fechaEnd = fila.createCell((short) 6);

		id.setCellValue(new HSSFRichTextString("ID"));
		name.setCellValue(new HSSFRichTextString("Producto"));
		descripcion.setCellValue(new HSSFRichTextString("Descripci�n"));
		base.setCellValue(new HSSFRichTextString("Valor base"));
		current.setCellValue(new HSSFRichTextString("Valor ofertado"));
		fechaStar.setCellValue(new HSSFRichTextString("Fecha inicio"));
		fechaEnd.setCellValue(new HSSFRichTextString("Fecha fin"));
		for (int i = 0; i < subasta.size(); ++i)
		{
			HSSFRow dataRow = hoja.createRow(i + 1);

			dataRow.createCell(0).setCellValue(subasta.get(i).getId());
			dataRow.createCell(1).setCellValue(subasta.get(i).getName());
			dataRow.createCell(2).setCellValue(subasta.get(i).getDescriptionProduct());
			dataRow.createCell(3).setCellValue(subasta.get(i).getValueBase());
			dataRow.createCell(4).setCellValue(subasta.get(i).getValueCurrent());
			dataRow.createCell(5).setCellValue(subasta.get(i).getDateStart());
			dataRow.createCell(6).setCellValue(subasta.get(i).getDateEnd());
		}

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		try
		{
			FileOutputStream elFichero = new FileOutputStream(
					"C://ReportesGeneradosSistemaSubastas/Reporte de subastas - " + user.getUserName() + ".xls");
			libro.write(elFichero);
			// Desktop.getDesktop().open(new
			// File("C://ReportesGeneradosSistemaSubastas/Reporte de subastas - " +
			// user.getUserName() + ".xls"));
			elFichero.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		logger.info("Se ha generado el arhcivo correctamente");

	}

	/**
	 * M�todo para generar reporte excel de todas las subastas
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void excelSubastas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra al m�todo ExcelSubastas");

		SalesuebService service = new SalesuebService();
		List<Salesueb> subasta = service.lista();

		HSSFWorkbook libro = new HSSFWorkbook();

		HSSFSheet hoja = libro.createSheet();

		HSSFRow fila = hoja.createRow(0);

		// Se crea una celda dentro de la fila
		HSSFCell id = fila.createCell((short) 0);
		HSSFCell name = fila.createCell((short) 1);
		HSSFCell descripcion = fila.createCell((short) 2);
		HSSFCell base = fila.createCell((short) 3);
		HSSFCell current = fila.createCell((short) 4);
		HSSFCell fechaStar = fila.createCell((short) 5);
		HSSFCell fechaEnd = fila.createCell((short) 6);

		id.setCellValue(new HSSFRichTextString("ID"));
		name.setCellValue(new HSSFRichTextString("Producto"));
		descripcion.setCellValue(new HSSFRichTextString("Descripci�n"));
		base.setCellValue(new HSSFRichTextString("Valor base"));
		current.setCellValue(new HSSFRichTextString("Valor ofertado"));
		fechaStar.setCellValue(new HSSFRichTextString("Fecha inicio"));
		fechaEnd.setCellValue(new HSSFRichTextString("Fecha fin"));
		for (int i = 0; i < subasta.size(); ++i)
		{
			HSSFRow dataRow = hoja.createRow(i + 1);

			dataRow.createCell(0).setCellValue(subasta.get(i).getId());
			dataRow.createCell(1).setCellValue(subasta.get(i).getName());
			dataRow.createCell(2).setCellValue(subasta.get(i).getDescriptionProduct());
			dataRow.createCell(3).setCellValue(subasta.get(i).getValueBase());
			dataRow.createCell(4).setCellValue(subasta.get(i).getValueCurrent());
			dataRow.createCell(5).setCellValue(subasta.get(i).getDateStart());
			dataRow.createCell(6).setCellValue(subasta.get(i).getDateEnd());
		}

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		try
		{
			FileOutputStream elFichero = new FileOutputStream(
					"C://ReportesGeneradosSistemaSubastas/Reporte de subastas.xls");
			libro.write(elFichero);
			// Desktop.getDesktop().open(new
			// File("C://ReportesGeneradosSistemaSubastas/Reporte de subastas.xls"));
			elFichero.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		logger.info("Se ha generado el arhcivo correctamente");

	}
	
	//---------------------------------------------------------------------------
	// REPORTES EN PDF
	//---------------------------------------------------------------------------

	public void pdfOfertasSubasta() throws DocumentException
	{
		logger.trace("Entra en el m�todo pdfofertasSubastas");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in;
		
		Document document = new Document();

		PdfWriter.getInstance(document, out);
		
		Rectangle tama�o = PageSize.A4;
		document.setPageSize(tama�o);
		document.open();

		Font f = new Font();
		f.setStyle(Font.BOLDITALIC);
		f.setSize(30);
		f.setColor(244, 92, 66);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
		String date = sdf.format(new Date());

		Paragraph tituloEmpresa = new Paragraph();
		tituloEmpresa.setFont(f);
		tituloEmpresa.add("ACME inc");
		document.add(tituloEmpresa);
		document.add(new Paragraph(date));
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);

		Font boldFont = new Font();
		boldFont.setStyle(Font.BOLD);
		boldFont.setSize(18);

		SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy");
		
		Paragraph titulo = new Paragraph();
		titulo.setFont(boldFont);
		titulo.add("REPORTE DE OFERTAS DE LA SUBASTA DE " + sale.getName().toUpperCase());
		titulo.setIndentationLeft(150);
		document.add(titulo);

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		PdfPTable tableUsuario = new PdfPTable(5);
		TableHeaderOfer(tableUsuario);
		RowsUserOfer(tableUsuario);
		document.add(tableUsuario);
		document.close();

		in = new ByteArrayInputStream(out.toByteArray());
		file = new DefaultStreamedContent(in, "application/pdf", "ReporteOfertasDeSubasta.pdf");

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		logger.info("Se ha generado correctamente el archivo");
	}
	
	public void TableHeaderOfer(PdfPTable table)
	{
		Stream.of("ID", "Producto", "Valor Oferta", "Fecha oferta", "Ganador").forEach(columnTitle ->
		{
			PdfPCell header = new PdfPCell();
			header.setBorderWidth(1);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}
	
	public void RowsUserOfer(PdfPTable table)
	{
		
		Iterator it = getListaOfertaSubasta().iterator();

		Offerersale x;
		String producto = "";
		SalesuebService service = new SalesuebService();
		while (it.hasNext())
		{
			x = (Offerersale) it.next();
			producto = service.listaSalesuebID(x.getIdSales()).getName();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell produc = new PdfPCell(new Phrase(producto));
			PdfPCell valor = new PdfPCell(new Phrase("" + x.getValueOffer()));
			PdfPCell fecha = new PdfPCell(new Phrase("" + x.getDateOffer()));
			PdfPCell ganador = new PdfPCell(new Phrase("" + x.getWinner()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			produc.setBorder(Rectangle.NO_BORDER);
			produc.setHorizontalAlignment(Element.ALIGN_CENTER);
			valor.setBorder(Rectangle.NO_BORDER);
			valor.setHorizontalAlignment(Element.ALIGN_CENTER);
			fecha.setBorder(Rectangle.NO_BORDER);
			fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
			ganador.setBorder(Rectangle.NO_BORDER);
			ganador.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(produc);
			table.addCell(valor);
			table.addCell(fecha);
			table.addCell(ganador);
		}
	}
	
	
	/**
	 * M�todo que genera el reporte en formato pdf de las subastas activas del momento
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void pdfSubastasActivas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra en el m�todo pdfSubastasActivas");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in;
		
		Document document = new Document();

		PdfWriter.getInstance(document, out);
		
		Rectangle tama�o = PageSize.A4;
		document.setPageSize(tama�o);
		document.open();

		Font f = new Font();
		f.setStyle(Font.BOLDITALIC);
		f.setSize(30);
		f.setColor(244, 92, 66);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
		String date = sdf.format(new Date());

		Paragraph tituloEmpresa = new Paragraph();
		tituloEmpresa.setFont(f);
		tituloEmpresa.add("ACME inc");
		document.add(tituloEmpresa);
		document.add(new Paragraph(date));
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);

		Font boldFont = new Font();
		boldFont.setStyle(Font.BOLD);
		boldFont.setSize(18);

		SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy");
		
		Paragraph titulo = new Paragraph();
		titulo.setFont(boldFont);
		titulo.add("REPORTE SUBASTAS ACTIVAS ");
		titulo.setAlignment(Element.ALIGN_CENTER);
		titulo.setIndentationLeft(100);
		document.add(titulo);

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		PdfPTable tableUsuario = new PdfPTable(7);
		TableHeaderSub(tableUsuario);
		RowsUserSub(tableUsuario);
		document.add(tableUsuario);
		document.close();

		in = new ByteArrayInputStream(out.toByteArray());
		file = new DefaultStreamedContent(in, "application/pdf", "ReporteSubastasActivas.pdf");

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		logger.info("Se ha generado correctamente el archivo");

	}

	/**
	 * M�todo que agrega los encabezados del archivo pdf de reporte de todas
	 * susbastas activas
	 * 
	 * @param table
	 */
	public void TableHeaderSub(PdfPTable table)
	{
		Stream.of("ID", "Producto", "Descripci�n", "Valor base", "Valor ofertado", "Fecha inicio", "Fecha fin")
				.forEach(columnTitle ->
				{
					PdfPCell header = new PdfPCell();
					header.setBorderWidth(1);
					header.setHorizontalAlignment(Element.ALIGN_CENTER);
					header.setPhrase(new Phrase(columnTitle));
					table.addCell(header);
				});
	}

	/**
	 * M�todo qe agrega las filas del archivo pdf de reporte de todas subastas activas
	 * 
	 * @param table
	 */
	public void RowsUserSub(PdfPTable table)
	{
		Iterator it = getListaSubastasActivas().iterator();

		Salesueb x;

		while (it.hasNext())
		{
			x = (Salesueb) it.next();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell Name = new PdfPCell(new Phrase(x.getName()));
			PdfPCell descripcion = new PdfPCell(new Phrase("" + x.getDescriptionProduct()));
			PdfPCell base = new PdfPCell(new Phrase("" + x.getValueBase()));
			PdfPCell actual = new PdfPCell(new Phrase("" + x.getValueCurrent()));
			PdfPCell fIncio = new PdfPCell(new Phrase(x.getDateStart().toString()));
			PdfPCell fFin = new PdfPCell(new Phrase(x.getDateEnd().toString()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			Name.setBorder(Rectangle.NO_BORDER);
			Name.setHorizontalAlignment(Element.ALIGN_CENTER);
			descripcion.setBorder(Rectangle.NO_BORDER);
			descripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
			base.setBorder(Rectangle.NO_BORDER);
			base.setHorizontalAlignment(Element.ALIGN_CENTER);
			actual.setBorder(Rectangle.NO_BORDER);
			actual.setHorizontalAlignment(Element.ALIGN_CENTER);
			fIncio.setBorder(Rectangle.NO_BORDER);
			fIncio.setHorizontalAlignment(Element.ALIGN_CENTER);
			fFin.setBorder(Rectangle.NO_BORDER);
			fFin.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(Name);
			table.addCell(descripcion);
			table.addCell(base);
			table.addCell(actual);
			table.addCell(fIncio);
			table.addCell(fFin);
		}
	}
	
	public void pdfProveedores() throws DocumentException
	{
		logger.trace("Entra al m�todo pdfProveedores");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in;
		
		Document document = new Document();

		PdfWriter.getInstance(document, out);
		
		Rectangle tama�o = PageSize.A4;
		document.setPageSize(tama�o);
		document.open();

		Font f = new Font();
		f.setStyle(Font.BOLDITALIC);
		f.setSize(30);
		f.setColor(244, 92, 66);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
		String date = sdf.format(new Date());

		Paragraph tituloEmpresa = new Paragraph();
		tituloEmpresa.setFont(f);
		tituloEmpresa.add("ACME inc");
		document.add(tituloEmpresa);
		document.add(new Paragraph(date));
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);

		Font boldFont = new Font();
		boldFont.setStyle(Font.BOLD);
		boldFont.setSize(18);

		SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy");
		
		Paragraph titulo = new Paragraph();
		titulo.setFont(boldFont);
		titulo.add("REPORTE DE PROVEEDORES EN EL SISTEMA");
		titulo.setAlignment(Element.ALIGN_CENTER);
		titulo.setIndentationLeft(100);
		document.add(titulo);

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		PdfPTable tableUsuario = new PdfPTable(4);
		addTableHeaderProveedores(tableUsuario);
		addRowsUserProveedores(tableUsuario);
		document.add(tableUsuario);
		document.close();

		in = new ByteArrayInputStream(out.toByteArray());
		file = new DefaultStreamedContent(in, "application/pdf", "ReporteProveedores.pdf");

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		logger.info("Se genera correctamente el archivo");
	}
	
	public void addTableHeaderProveedores(PdfPTable table)
	{
		Stream.of("ID", "Usuario", "Nombre", "Correo Electronico").forEach(columnTitle ->
		{
			PdfPCell header = new PdfPCell();
			header.setBorderWidth(1);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}
	
	public void addRowsUserProveedores(PdfPTable table)
	{
		
		Iterator it = getListaProveedores().iterator();

		User x;
		while (it.hasNext())
		{
			x = (User) it.next();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell userName = new PdfPCell(new Phrase(x.getUserName()));
			PdfPCell fullName = new PdfPCell(new Phrase(x.getFullName()));
			PdfPCell correo = new PdfPCell(new Phrase(x.getEmailAddress()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			userName.setBorder(Rectangle.NO_BORDER);
			userName.setHorizontalAlignment(Element.ALIGN_CENTER);
			fullName.setBorder(Rectangle.NO_BORDER);
			fullName.setHorizontalAlignment(Element.ALIGN_CENTER);
			correo.setBorder(Rectangle.NO_BORDER);
			correo.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(userName);
			table.addCell(fullName);
			table.addCell(correo);
		}
	}

	/**
	 * M�todo para generar el documento PDF de las ofertas por un rango de fecha
	 * 
	 * @throws DocumentException
	 * @throws IOException 
	 */
	public void pdfOfertasFechas() throws DocumentException, IOException
	{

		logger.trace("Entra al m�todo pdfOfertas");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in;
		
		Document document = new Document();

		PdfWriter.getInstance(document, out);
		
		Rectangle tama�o = PageSize.A4;
		document.setPageSize(tama�o);
		document.open();

		Font f = new Font();
		f.setStyle(Font.BOLDITALIC);
		f.setSize(30);
		f.setColor(244, 92, 66);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
		String date = sdf.format(new Date());

		Paragraph tituloEmpresa = new Paragraph();
		tituloEmpresa.setFont(f);
		tituloEmpresa.add("ACME inc");
		document.add(tituloEmpresa);
		document.add(new Paragraph(date));
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);

		Font boldFont = new Font();
		boldFont.setStyle(Font.BOLD);
		boldFont.setSize(18);

		SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy");
		
		Paragraph titulo = new Paragraph();
		titulo.setFont(boldFont);
		titulo.add("REPORTE DE OFERTAS DE " + userPostor + " ENTRE " + format.format(inicio) + " - " + format.format(fin) + ".");
		titulo.setIndentationLeft(150);
		document.add(titulo);

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		PdfPTable tableUsuario = new PdfPTable(5);
		addTableHeaderOfertasFechas(tableUsuario);
		addRowsUserOfertasFechas(tableUsuario);
		document.add(tableUsuario);
		document.close();

		in = new ByteArrayInputStream(out.toByteArray());
		file = new DefaultStreamedContent(in, "application/pdf", "ReporteDeOfertas.pdf");

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		logger.info("Se genera correctamente el archivo");

	}

	/**
	 * M�todo para agregar el encabezado del reporte PDF de las ofertas por un rango de fechas
	 * 
	 * @param table
	 */
	public void addTableHeaderOfertasFechas(PdfPTable table)
	{
		Stream.of("ID", "Producto", "Valor Oferta", "Fecha oferta", "Ganador").forEach(columnTitle ->
		{
			PdfPCell header = new PdfPCell();
			header.setBorderWidth(1);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	/**
	 * M�todo para agregar las filas al reporte pdf de las ofertas por un rango de fechas
	 * 
	 * @param table
	 */
	public void addRowsUserOfertasFechas(PdfPTable table)
	{
		
		Iterator it = getListaOfertaPostorRangoFechas().iterator();

		Offerersale x;
		String producto = "";
		SalesuebService service = new SalesuebService();
		while (it.hasNext())
		{
			x = (Offerersale) it.next();
			producto = service.listaSalesuebID(x.getIdSales()).getName();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell produc = new PdfPCell(new Phrase(producto));
			PdfPCell valor = new PdfPCell(new Phrase("" + x.getValueOffer()));
			PdfPCell fecha = new PdfPCell(new Phrase("" + x.getDateOffer()));
			PdfPCell ganador = new PdfPCell(new Phrase("" + x.getWinner()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			produc.setBorder(Rectangle.NO_BORDER);
			produc.setHorizontalAlignment(Element.ALIGN_CENTER);
			valor.setBorder(Rectangle.NO_BORDER);
			valor.setHorizontalAlignment(Element.ALIGN_CENTER);
			fecha.setBorder(Rectangle.NO_BORDER);
			fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
			ganador.setBorder(Rectangle.NO_BORDER);
			ganador.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(produc);
			table.addCell(valor);
			table.addCell(fecha);
			table.addCell(ganador);
		}
	}

	/**
	 * M�todo para actualizar todas las ofertas cuando ingrese un usuario
	 */
	public void actualizarSubastas()
	{

		logger.trace("Entra al m�todo actualizarSubastas");

		List<Salesueb> lista = new SalesuebService().lista();
		Date actual = new Date();
		for (int i = 0; i < lista.size(); i++)
		{
			sale = lista.get(i);

			if (lista.get(i).getDateStart().before(actual) || lista.get(i).getDateStart().equals(actual))
			{
				lista.get(i).setState("ACTIVE");
				modificarSubasta();

			}
			if (lista.get(i).getDateEnd().before(actual))
			{
				lista.get(i).setState("INACTIVE");
				modificarSubasta();
			}
		}

		logger.info("El estado de las subastas ha sido actualizado a LOSER");
	}

	/**
	 * Getter de user
	 * 
	 * @return user
	 */
	public User getUsuario()
	{
		return user;
	}

	/**
	 * Setter de user
	 * 
	 * @param user
	 */
	public void setUsuario(User usuario)
	{
		this.user = usuario;
	}

	/**
	 * getter de loginUser
	 * 
	 * @return loginUser
	 */
	public User getLoginUser()
	{
		return loginUser;
	}

	/**
	 * Setter de loginUser
	 * 
	 * @param loginUser
	 */
	public void setLoginUser(User loginUser)
	{
		this.loginUser = loginUser;
	}

	/**
	 * Getter de userAdmin
	 * 
	 * @return userAdmin
	 */
	public User getUserAdmin()
	{
		return userAdmin;
	}

	/**
	 * Setter de userAdmin
	 * 
	 * @param userAdmin
	 */
	public void setUserAdmin(User userAdmin)
	{
		this.userAdmin = userAdmin;
	}

	/**
	 * Getter de listaUser
	 * 
	 * @return listaUser
	 */
	public DataModel getListarUser()
	{
		List<User> lista = new UserService().lista();
		listaUser = new ListDataModel(lista);
		return listaUser;
	}

	/**
	 * Getter de userPass
	 * 
	 * @return userPass
	 */
	public User getUserPass()
	{
		return userPass;
	}

	/**
	 * Setter de userPass
	 * 
	 * @param userPass
	 */
	public void setUserPass(User userPass)
	{
		this.userPass = userPass;
	}

	/**
	 * Getter de mensaje Error
	 * 
	 * @return mensajeError
	 */
	public String getMensajeError()
	{
		return mensajeError;
	}

	/**
	 * Setter de mensajeError
	 * 
	 * @param mensajeError
	 */
	public void setMensajeError(String mensajeError)
	{
		this.mensajeError = mensajeError;
	}

	/**
	 * Getter de email1
	 * 
	 * @return email1
	 */
	public String getEmail1()
	{
		return email1;
	}

	/**
	 * Setter de email1
	 * 
	 * @param email1
	 */
	public void setEmail1(String email1)
	{
		this.email1 = email1;
	}

	/**
	 * Getter de email2
	 * 
	 * @return email2
	 */
	public String getEmail2()
	{
		return email2;
	}

	/**
	 * Setter de email2
	 * 
	 * @param email2
	 */
	public void setEmail2(String email2)
	{
		this.email2 = email2;
	}

	/**
	 * Getter de sale
	 * 
	 * @return sale
	 */
	public Salesueb getSale()
	{
		return sale;
	}

	/**
	 * Setter de sale
	 * 
	 * @param sale
	 */
	public void setSale(Salesueb sale)
	{
		this.sale = sale;
	}

	/**
	 * Getter de nombre
	 * 
	 * @return nombre
	 */
	public String getNombre()
	{
		return nombre;
	}

	/**
	 * Setter de nombre
	 * 
	 * @param nombre
	 */
	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}

	/**
	 * M�todo que inicializa la listaProveedor
	 * 
	 * @param userSales
	 * @return listaProveedor
	 */
	public DataModel inicializarListaProveedor(String userSales)
	{
		logger.trace("Entra al m�todo inicializarListaProveedor");
		List<Salesueb> lista = new SalesuebService().getSalesueb(userSales);
		listaProveedor = new ListDataModel<>(lista);
		logger.info("La lista de proveedor ha sido inicializada con sus subastas correspondientes");
		return listaProveedor;
	}

	/**
	 * Getter de listaProveedor
	 * 
	 * @return listaProveedor
	 */
	public DataModel getListaProveedor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaProveedor = inicializarListaProveedor(loginUser.getUserName());
		return listaProveedor;
	}

	/**
	 * Setter de listaProveedor
	 * 
	 * @param listaProveedor
	 */
	public void setListaProveedor(DataModel listaProveedor)
	{
		this.listaProveedor = listaProveedor;
	}

	/**
	 * Getter de oferta
	 * 
	 * @return oferta
	 */
	public Offerersale getOferta()
	{
		return oferta;
	}

	/**
	 * Setter de oferta
	 * 
	 * @param oferta
	 */
	public void setOferta(Offerersale oferta)
	{
		this.oferta = oferta;
	}

	/**
	 * Getter de idSale
	 * 
	 * @return
	 */
	public int getIdSale()
	{
		return idSale;
	}

	/**
	 * Setter de idSale
	 * 
	 * @param idSale
	 */
	public void setIdSale(int idSale)
	{
		this.idSale = idSale;
	}

	/**
	 * Getter de listaSubastas
	 * 
	 * @return listaSubastas
	 */
	public DataModel getListaSubastas()
	{
		actualizarSubastas();
		List<Salesueb> lista = new SalesuebService().lista();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}

	/**
	 * Getter de listaSubastas
	 * 
	 * @param listaSubastas
	 */
	public void setListaSubastas(DataModel listaSubastas)
	{
		this.listaSubastas = listaSubastas;
	}

	/**
	 * M�todo que inicializa la listaOfertaPostor
	 * 
	 * @param postor
	 * @return listaOfertaPostor
	 */
	public DataModel inicializarListaOfertaPostor(String postor)
	{

		logger.trace("Entra al m�todo inicializarListaOfertaPostor");
		List<Offerersale> lista = new OfferersaleService().getOfferersale(postor);
		listaOfertaPostor = new ListDataModel(lista);
		logger.info("Lista de ofertas est� inicializada");
		return listaOfertaPostor;
	}

	/**
	 * Setter de listaOfertaPostor
	 * 
	 * @param listaOfertaPostor
	 */
	public void setListaOfertaPostor(DataModel listaOfertaPostor)
	{
		this.listaOfertaPostor = listaOfertaPostor;
	}

	/**
	 * Getter de listaOfertaPostor
	 * 
	 * @return listaOfertaPostor
	 */
	public DataModel getListaOfertaPostor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaOfertaPostor = inicializarListaOfertaPostor(loginUser.getUserName());
		return listaOfertaPostor;
	}

	/**
	 * Getter de listaSubastaActivas
	 * 
	 * @return listaSubastasActivas
	 */
	public DataModel getListaSubastasActivas()
	{
		actualizarSubastas();
		List<Salesueb> lista = new SalesuebService().listaActivas();
		listaSubastasActivas = new ListDataModel(lista);
		return listaSubastasActivas;
	}

	/**
	 * Setter de listaSubastasActivas
	 * 
	 * @param listaSubastasActivas
	 */
	public void setListaSubastasActivas(DataModel listaSubastasActivas)
	{
		this.listaSubastasActivas = listaSubastasActivas;
	}

	/**
	 * Inicializa la lista de ofertas de una subasta
	 */
	public void inicializarListaOfertas(int idSubasta)
	{
		OfferersaleService servicio = new OfferersaleService();
		List<Offerersale> lista = servicio.getOfertaDeSubasta(idSubasta);
		listaOfertaSubasta = new ListDataModel<>(lista);
	}

	/**
	 * Da la lista de ofertas por una subasta
	 * 
	 * @return listaOfertaSubasta
	 */
	public DataModel getListaOfertaSubasta()
	{
		inicializarListaOfertas(sale.getId());
		return listaOfertaSubasta;
	}
	
	/**
	 * Modifica la lista de las ofertas de una subasta
	 * 
	 * @param listaOfertaSubasta
	 */
	public void setListaOfertaSubasta(DataModel listaOfertaSubasta)
	{
		this.listaOfertaSubasta = listaOfertaSubasta;
	}
	
	public List inicializarListaOfertasPostorRangos()
	{
		UserService servicio = new UserService();
		Iterator<User> it = servicio.lista().iterator();
		boolean encontrado = false;

		while(it.hasNext())
		{
			if(it.next().getUserName().equalsIgnoreCase(userPostor))
			{
				encontrado = true;
			}
		}
		
		if(encontrado == true)
		{
			listaOfertaPostorRangoFechas = new ArrayList<>();
			OfferersaleService service = new OfferersaleService();
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
			listaOfertaPostorRangoFechas = service.listaFiltrada("dateOffer BETWEEN '" + formato.format(inicio) + "'AND'" + formato.format(fin) + "' AND userName = '" + userPostor + "'");
			return listaOfertaPostorRangoFechas;
		}
		else
		{
			mensajeError = "El usuario " + userPostor + " no existe.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			return null;
		}
		
		
	}

	public List getListaOfertaPostorRangoFechas()
	{
		listaOfertaPostorRangoFechas = inicializarListaOfertasPostorRangos();
		return listaOfertaPostorRangoFechas;
	}

	public void setListaOfertaPostorRangoFechas(List listaOfertaPostorRangoFechas)
	{
		this.listaOfertaPostorRangoFechas = listaOfertaPostorRangoFechas;
	}

	public Date getInicio()
	{
		return inicio;
	}

	public void setInicio(Date inicio)
	{
		this.inicio = inicio;
	}

	public Date getFin()
	{
		return fin;
	}

	public void setFin(Date fin)
	{
		this.fin = fin;
	}

	public String getUserPostor()
	{
		return userPostor;
	}

	public void setUserPostor(String userPostor)
	{
		this.userPostor = userPostor;
	}

	public StreamedContent getFile()
	{
		return file;
	}

	public void setFile(StreamedContent file)
	{
		this.file = file;
	}

	public List getListaProveedores()
	{
		UserService servicio = new UserService();
		listaProveedores = servicio.listaProveedores("userType = 'proveedor'");
		return listaProveedores;
	}

	public void setListaProveedores(List listaProveedores)
	{
		this.listaProveedores = listaProveedores;
	}

	
	
}
