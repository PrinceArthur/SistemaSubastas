package bean;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
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
 * @author estef
 *
 */

@ManagedBean
@SessionScoped
public class UserMB
{
	private AuditMB audit = new AuditMB();
	private User user;
	private User loginUser = new User();
	private User userAdmin = new User();
	private User userPass = new User();
	private DataModel listaUser;
	private DataModel listaProveedor;
	private DataModel listaSubastas;
	private DataModel listaOfertaPostor;
	private DataModel listaSubastasActivas;
	private String mensajeError;
	private String email1;
	private String email2;
	private Salesueb sale;
	private String nombre;
	private Offerersale oferta = new Offerersale();
	private int idSale;


	private static Logger logger = Logger.getLogger(UserMB.class);

	public UserMB()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
	}

	public String prepararAdicionarUser()
	{
		logger.trace("Entra al método prepararAdicionarUser");
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("proveedor");
		Date now = new Date();
		user.setDateLastPassword(now);
		email1 = "";
		email2 = "";
		logger.info("Objeto user inicializado para agregar Proveedor y nos dirige a /administrador/registrarProvedor.xhtml");
		return "/administrador/registrarProvedor.xhtml";
	}

	public String prepararAdicionarPostor()
	{
		logger.trace("Entra al método prepararAdicionarPostor");
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("postor");
		Date now = new Date();
		user.setDateLastPassword(now);
		logger.info("Objeto user inicializado para agregar Postor y nos dirige a /postor/registrarPostor.xhtml");
		return "/postor/registrarPostor.xhtml";
	}

	public String prepararModificarUser()
	{
		logger.trace("Entra al método prepararModificarUser");
		user = (User) (listaUser.getRowData());
		String correo = user.getEmailAddress();
		String[] x = correo.split("@");
		email1 = x[0];
		email2 = x[1];
		logger.info("Objeto user inicializado con la fila de la listaUser y nos dirige a la página /administrador/modificarProveedor");
		return "/administrador/modificarProveedor";
	}

	public String prepararAdmin()
	{
		logger.trace("Entra al método prepararAdmin");
		UserService service = new UserService();
		userAdmin = service.getUser("admin");
		logger.info("objeto userAdmin iniicializado con el usuario de tipo admin y nos dirige a /administrador/datosAdmin");
		return "/administrador/datosAdmin";
	}

	public String prepararRecuperarContraseña()
	{
		logger.trace("Entra al método prepararRecuperarContraseña");
		userPass = new User();
		logger.info("Objeto userPass inicializado y nos dirige a la página recuperarContraseña");
		return "recuperarContraseña";
	}

	public String prepararCambioContraseña()
	{
		logger.trace("Entra al método prepararCambioContraseña");
		UserService service = new UserService();
		userPass = service.getUser(loginUser.getUserName());
		logger.info("Objeto userPass inicializado con el user de loginUser y nos dirige a cambiarContraseña");
		return "cambiarContraseña";
	}

	public String prepararIngresoProveedor()
	{
		logger.trace("Entra al método prepararIngresoProveedor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaProveedor = inicializarListaProveedor(loginUser.getUserName());
		logger.info("Objeto user inicializado por el user de loginUser y listaProveedor incializada. Nos dirige a /proveedor/indexProveedor");
		return "/proveedor/indexProveedor";
	}

	public String prepararDatosProveedor()
	{
		logger.trace("Entra al método prepararDatosProveedor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		logger.info("Objeto user inicializado al user de loginUser y  nos dirige a la página /proveedor/datosProveedor");
		return "/proveedor/datosProveedor";
	}

	public String prepararIngresoPostor()
	{
		logger.trace("Entra al método prepararIngresoPostor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaOfertaPostor = inicializarListaOfertaPostor(loginUser.getUserName());
		logger.info("Objeto user inicializado al user de loginUser y listaOfertaPostor inicializadas a la página /postor/indexPostor");
		return "/postor/indexPostor";
	}

	public String prepararDatosPostor()
	{
		logger.trace("Entra al método prepararDatosPostor");
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		logger.info("Ojeto user inicializado con el user de loginUser y nos dirige a la página /postor/datosPostor");
		return "/postor/datosPostor";
	}

	public String prepararAdicionarSubasta()
	{
		logger.trace("Entra a método prepararAdicionarSubasta");
		ParameterService serviceP = new ParameterService();
		sale = new Salesueb();
		nombre = user.getUserName();
		sale.setPhotoProduct(serviceP.getParameter("RutaImagen").getTextValue());
		logger.info("Objeto sale inicializado con Salesueb, objeto nombre inicializado con el userName de user y cambiamos la ruta de la imagen con el para metro 'Ruta imagen'. Nos dirige a la página /proveedor/nuevaSubasta ");
		return "/proveedor/nuevaSubasta";

	}

	public String prepararSubasta()
	{
		logger.trace("Entra al método prepararSubasta");
		sale = (Salesueb) listaSubastas.getRowData();
		logger.info("Objeto sale inicializado con la columna de la lista y nos dirige a la página /postor/subasta ");
		return "/postor/subasta";
	}

	public void prepararAgregarOferta()
	{
		logger.trace("Entra al método prepararAgregarOferta");
		idSale = sale.getId();
		oferta.setDateOffer(new Date());
		nombre = loginUser.getUserName();
		logger.info("Se incializa el objeto idSale con el id de la subasta, se asigna la fefcha a la oferta y asignamos a la variable nombre el valor del userName de loginUser.");
	}

	public String adicionarUser()
	{
		logger.trace("Entra al método adicionarUser");
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

	public String modificarUser()
	{
		logger.trace("Entra al método modificarUser");
		UserService service = new UserService();
		user.setEmailAddress(email1 + email2);
		service.actualizar(user);
		audit.adicionarAudit("Admin", "UPDATE", "User", user.getId());
		logger.info("Actualizamos el usuario en la base de datos ");
		return "/administrador/indexAdmin";
	}

	public void eliminarUser()
	{
		logger.trace("Entramos al método eliminarUser");
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

	public String login()
	{

		logger.trace("Método de login");

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
						logger.info("El usuario debe cambiar la contrasela por ingrresar con constraseña generada por el administrador o por que se vencio el tiempo para el cambio.");
						pagina = prepararCambioContraseña();
					} else
					{
						mensajeError = "Verificación del CAPTCHA invalida";
						logger.error("Validación del CAPTCHA incorrecta");
					}
				} catch (Exception e)
				{
					logger.error("Validación del CAPTCHA incorrecta");
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
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
						logger.error("Validación del CAPTCHA incorrecta");
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
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
						logger.error("Validación del CAPTCHA incorrecta");
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
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
						logger.error("Validación del CAPTCHA incorrecta");
					}
				}

			} else if (!usuarioTemp.getPassword()
					.equals(Cifrado.getStringMessageDigest(loginUser.getPassword(), Cifrado.MD5)))
			{
				if (usuarioTemp.getFailedAttempts() < serviceP.getParameter("Intentos").getNumberValue()
						&& serviceP.getParameter("Intentos").getState().equalsIgnoreCase("ACTIVE"))
				{
					mensajeError = "Contraseña o Usuario inválido";
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
					usuarioTemp.setFailedAttempts(usuarioTemp.getFailedAttempts() + 1);
					service.actualizar(usuarioTemp);
					logger.warn("El usuario ingreso mal su contraseña");
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
			mensajeError = "Contraseña o Usuario inválido";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("Está ingresando un usuario que no está registrado");
		}

		return pagina;
	}

	/**
	 * Método para hacer el cambio obligatorio de contraseña
	 * 
	 * @throws IOException
	 */

	public void recuperarContraseña() throws IOException
	{
		logger.trace("Entra la método recuperarContraseña");
		UserService service = new UserService();
		String pass = userPass.getPassword();
		userPass.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5));
		userPass.setDateLastPassword(new Date());
		logger.info("Sele genera una nueva contraseña al usuario y es enviada al correo que está registrado");
		service.actualizar(userPass);

		audit.adicionarAudit(userPass.getUserName(), "UPDATE", "User", userPass.getId());

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
	}

	/**
	 * Método para link De olvidar contraseña
	 * 
	 * @throws IOException
	 */

	public void cambiarContraseña() throws IOException
	{
		logger.trace("Entramos al método cambiarContraseña");
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
			logger.info("El usuario fue encontrado por su correo y se le ha enviado la nueva contraseña la correo.");
			audit.adicionarAudit(userTemp.getUserName(), "UPDATE", "User", userTemp.getId());

		} else
		{
			mensajeError = "No se encontró un usuario con ese correo.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			logger.warn("No se encontró ningun usuario por ese correo");
		}
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");

	}

	public void logOut() throws IOException
	{

		logger.trace("Entra al método logOut");
		
		UserService service = new UserService();
		User usuarioTemp = service.getUser(loginUser.getUserName());

		audit.adicionarAudit(usuarioTemp.getUserName(), "LOGOUT", "---", 0);

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
		
		logger.info("El usuario cierra seción");
	}

	public String agregarOferta()
	{
		logger.trace("Entra al método agregarOferta");
		OfferersaleService service = new OfferersaleService();
		oferta.setIdentification(nombre);

		if (oferta.getValueOffer() <= sale.getValueBase()  && oferta.getValueOffer() <= sale.getValueCurrent())
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
	
	public void actualizarOfertas(int idSales)
	{
		logger.trace("Entra al método actualizarOfertas");
		OfferersaleService service = new OfferersaleService();
		List<Offerersale> listaOfertas = new OfferersaleService().getOfertaDeSubasta(idSales);
		
		Iterator<Offerersale> it = listaOfertas.iterator();
		while(it.hasNext())
		{
			Offerersale m = it.next();
			m.setWinner("LOSER");
			service.actualizar(m);
		}
		
		logger.info("Actualiza el estado de todas las oferta a LOSER para que la nueva oferta sea la gabadora");
		
	}

	public void modificarSubasta()
	{
		logger.trace("Entra al método modificarSubasta");
		SalesuebService service = new SalesuebService();
		service.actualizar(sale);
		logger.info("Se modifica la subasta");
	}

	public String adicionarSubasta()
	{
		logger.trace("Entra al método adicionarSubasta");
		SalesuebMB saleB = new SalesuebMB();

		if (sale.getDateStart().getDay() >= sale.getDateEnd().getDay())
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
			audit.adicionarAudit(nombre, "CREATE", "Salesueb", sale.getId());
			logger.info("Se crea una nueva subasta");
			return "/proveedor/indexProveedor";
		}
	}

	public void adicionarPostor()
	{
		logger.trace("Entra al método adicionarPostor");
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

		logger.info("Verifica si el usuario no está registrado en el sistema");
		
		if (repetido == false)
		{
			user.setDateLastPassword(new Date());
			user.setEmailAddress(email1 + email2);
			String pass = EnviarCorreo.sendEmail(user.getEmailAddress());
			user.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			service.nuevo(user);
			
			logger.info("Se ha enviado contraseña del nuevo usuario a su correo");

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void archivoExcelOfertas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra al método archivoExcelOfertas");
		OfferersaleService service = new OfferersaleService();
		List<Offerersale> oferta = service.getOfferersale(loginUser.getUserName());

		HSSFWorkbook libro = new HSSFWorkbook();

		HSSFSheet hoja = libro.createSheet();

		HSSFRow fila = hoja.createRow(0);

		// Se crea una celda dentro de la fila
		HSSFCell id = fila.createCell((short) 0);
		HSSFCell operationCrud = fila.createCell((short) 1);
		HSSFCell tableName = fila.createCell((short) 2);
		HSSFCell tableId = fila.createCell((short) 3);
		HSSFCell createDate = fila.createCell((short) 4);

		id.setCellValue(new HSSFRichTextString("ID"));
		operationCrud.setCellValue(new HSSFRichTextString("Usuario de subasta"));
		tableName.setCellValue(new HSSFRichTextString("Valor Oferta"));
		tableId.setCellValue(new HSSFRichTextString("Fecha oferta"));
		createDate.setCellValue(new HSSFRichTextString("Ganador"));
		for (int i = 0; i < oferta.size(); ++i)
		{
			HSSFRow dataRow = hoja.createRow(i + 1);

			dataRow.createCell(0).setCellValue(oferta.get(i).getId());
			dataRow.createCell(1).setCellValue(oferta.get(i).getIdSales());
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

	public void archivoExcelSubastas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra al método archivoExcelSubastas");

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
		descripcion.setCellValue(new HSSFRichTextString("Descripción"));
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
			// File("C://ReportesGeneradosSistemaSubastas/Reporte de subastas - " + user.getUserName() + ".xls"));
			elFichero.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		logger.info("Se ha generado el arhcivo correctamente");
		
	}

	public void pdfSubastas() throws FileNotFoundException, DocumentException
	{
		logger.trace("Entra en el método pdfSubastas");

		Document pdfDoc = new Document(PageSize.A4);

		PdfWriter m = PdfWriter.getInstance(pdfDoc, new FileOutputStream(
				"C://ReportesGeneradosSistemaSubastas/Reporte de subastas - " + user.getUserName() + ".pdf"));

		pdfDoc.open();

		PdfPTable tableUsuario = new PdfPTable(5);
		PdfPCell titulo = new PdfPCell(new Phrase("REPORTE DE MIS SUBASTAS : " + loginUser.getUserName()));
		titulo.setColspan(7);
		titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
		titulo.setBorderWidth(0);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableUsuario.addCell(titulo);
		addTableHeaderSub(tableUsuario);
		addRowsUserSub(tableUsuario);
		pdfDoc.add(tableUsuario);
		
		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		
//		try
//		{
////			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/Reporte de subastas - " + user.getUserName() + ".pdf"));
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		pdfDoc.close();
		m.close();
		
		logger.info("Se ha generado correctamente el archivo");

	}

	public void addTableHeaderSub(PdfPTable table)
	{
		Stream.of("ID", "Producto", "Descripción", "Valor base", "Valor ofertado", "Fecha inicio","Fecha fin").forEach(columnTitle ->
		{
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(0);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	public void addRowsUserSub(PdfPTable table)
	{
		Iterator it = getListaSubastas().iterator();

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

	public void pdfOfertas() throws FileNotFoundException, DocumentException
	{

		logger.trace("Entra al método pdfOfertas");
		Document pdfDoc = new Document(PageSize.A4);

		PdfWriter m = PdfWriter.getInstance(pdfDoc, new FileOutputStream(
				"C://ReportesGeneradosSistemaSubastas/Reporte de ofertas - " + loginUser.getUserName() + ".pdf"));

		pdfDoc.open();

		PdfPTable tableUsuario = new PdfPTable(5);
		PdfPCell titulo = new PdfPCell(new Phrase("REPORTE DE MIS OFERTAS : " + loginUser.getUserName()));
		titulo.setColspan(5);
		titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
		titulo.setBorderWidth(0);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableUsuario.addCell(titulo);
		addTableHeaderCrud(tableUsuario);
		addRowsUserCrud(tableUsuario);
		pdfDoc.add(tableUsuario);
		
		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		
//			try
//			{
////				Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/"+crud+".pdf"));
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		pdfDoc.close();
		m.close();
		
		logger.info("Se genera correctamente el archivo");

	}

	public void addTableHeaderCrud(PdfPTable table)
	{
		Stream.of("ID", "Usuario de subasta", "Valor Oferta", "Fecha oferta", "Ganador").forEach(columnTitle ->
		{
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(0);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	public void addRowsUserCrud(PdfPTable table)
	{
		Iterator it = getListaOfertaPostor().iterator();

		Offerersale x;

		while (it.hasNext())
		{
			x = (Offerersale) it.next();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell userName = new PdfPCell(new Phrase(x.getIdentification()));
			PdfPCell valor = new PdfPCell(new Phrase("" + x.getValueOffer()));
			PdfPCell fecha = new PdfPCell(new Phrase("" + x.getDateOffer()));
			PdfPCell ganador = new PdfPCell(new Phrase("" + x.getWinner()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			userName.setBorder(Rectangle.NO_BORDER);
			userName.setHorizontalAlignment(Element.ALIGN_CENTER);
			valor.setBorder(Rectangle.NO_BORDER);
			valor.setHorizontalAlignment(Element.ALIGN_CENTER);
			fecha.setBorder(Rectangle.NO_BORDER);
			fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
			ganador.setBorder(Rectangle.NO_BORDER);
			ganador.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(userName);
			table.addCell(valor);
			table.addCell(fecha);
			table.addCell(ganador);
		}
	}
	
	public void actualizarSubastas()
	{
		
		logger.trace("Entra al método actualizarSubastas");
		
		List<Salesueb> lista = new SalesuebService().lista();
		Date actual = new Date();
		for(int i = 0; i < lista.size(); i++)
		{
			sale = lista.get(i);
			
			if(lista.get(i).getDateStart().before(actual) || lista.get(i).getDateStart().equals(actual))
			{
				lista.get(i).setState("ACTIVE");
				modificarSubasta();
				
			}
			if(lista.get(i).getDateEnd().before(actual))
			{
				lista.get(i).setState("INACTIVE");
				modificarSubasta();
			}
		}
		
		logger.info("El estado de las subastas ha sido actualizado a LOSER");
	}

	public User getUsuario()
	{
		return user;
	}

	public void setUsuario(User usuario)
	{
		this.user = usuario;
	}

	public User getLoginUser()
	{
		return loginUser;
	}

	public void setLoginUser(User loginUser)
	{
		this.loginUser = loginUser;
	}

	public User getUserAdmin()
	{
		return userAdmin;
	}

	public void setUserAdmin(User userAdmin)
	{
		this.userAdmin = userAdmin;
	}

	public DataModel getListarUser()
	{
		List<User> lista = new UserService().lista();
		listaUser = new ListDataModel(lista);
		return listaUser;
	}

	public User getUserPass()
	{
		return userPass;
	}

	public void setUserPass(User userPass)
	{
		this.userPass = userPass;
	}

	public String getMensajeError()
	{
		return mensajeError;
	}

	public void setMensajeError(String mensajeError)
	{
		this.mensajeError = mensajeError;
	}

	public String getEmail1()
	{
		return email1;
	}

	public void setEmail1(String email1)
	{
		this.email1 = email1;
	}

	public String getEmail2()
	{
		return email2;
	}

	public void setEmail2(String email2)
	{
		this.email2 = email2;
	}

	public Salesueb getSale()
	{
		return sale;
	}

	public void setSale(Salesueb sale)
	{
		this.sale = sale;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}

	public DataModel inicializarListaProveedor(String userSales)
	{
		logger.trace("Entra al método inicializarListaProveedor");
		List<Salesueb> lista = new SalesuebService().getSalesueb(userSales);
		listaProveedor = new ListDataModel<>(lista);
		logger.info("La lista de proveedor ha sido inicializada con sus subastas correspondientes");
		return listaProveedor;
	}

	public DataModel getListaProveedor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaProveedor = inicializarListaProveedor(loginUser.getUserName());
		return listaProveedor;
	}

	public void setListaProveedor(DataModel listaProveedor)
	{
		this.listaProveedor = listaProveedor;
	}

	public Offerersale getOferta()
	{
		return oferta;
	}

	public void setOferta(Offerersale oferta)
	{
		this.oferta = oferta;
	}

	public int getIdSale()
	{
		return idSale;
	}

	public void setIdSale(int idSale)
	{
		this.idSale = idSale;
	}

	public DataModel getListaSubastas()
	{
		actualizarSubastas();
		List<Salesueb> lista = new SalesuebService().lista();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}

	public void setListaSubastas(DataModel listaSubastas)
	{
		this.listaSubastas = listaSubastas;
	}

	public DataModel inicializarListaOfertaPostor(String postor)
	{
		
		logger.trace("Entra al método inicializarListaOfertaPostor");
		List<Offerersale> lista = new OfferersaleService().getOfferersale(postor);
		listaOfertaPostor = new ListDataModel(lista);
		logger.info("Lista de ofertas está inicializada");
		return listaOfertaPostor;
	}

	public void setListaOfertaPostor(DataModel listaOfertaPostor)
	{
		this.listaOfertaPostor = listaOfertaPostor;
	}

	public DataModel getListaOfertaPostor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaOfertaPostor = inicializarListaOfertaPostor(loginUser.getUserName());
		return listaOfertaPostor;
	}

	public DataModel getListaSubastasActivas() {
		actualizarSubastas();
		List<Salesueb> lista = new SalesuebService().listaActivas();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}

	public void setListaSubastasActivas(DataModel listaSubastasActivas) {
		this.listaSubastasActivas = listaSubastasActivas;
	}

	
}
