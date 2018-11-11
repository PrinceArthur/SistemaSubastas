package bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;


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
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("proveedor");
		Date now = new Date();
		user.setDateLastPassword(now);
		return "/administrador/registrarProvedor.xhtml";
	}
	
	public String prepararAdicionarPostor()
	{
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("postor");
		Date now = new Date();
		user.setDateLastPassword(now);
		return "/postor/registrarPostor.xhtml";
	}

	public String prepararModificarUser()
	{
		user = (User) (listaUser.getRowData());
		String correo = user.getEmailAddress();
		String[] x = correo.split("@");
		email1 = x[0];
		email2 = x[1];
		return "/administrador/modificarProveedor";
	}

	public String prepararAdmin()
	{
		UserService service = new UserService();
		userAdmin = service.getUser("admin");

		return "/administrador/datosAdmin";
	}

	public String prepararRecuperarContraseña()
	{
		userPass = new User();
		return "recuperarContraseña";
	}

	public String prepararCambioContraseña()
	{
		UserService service = new UserService();
		userPass = service.getUser(loginUser.getUserName());
		return "cambiarContraseña";
	}

	public String prepararIngresoProveedor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaProveedor = inicializarListaProveedor(loginUser.getUserName());
		return "/proveedor/indexProveedor";
	}

	public String prepararDatosProveedor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		return "/proveedor/datosProveedor";
	}
	public String prepararIngresoPostor() {
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaOfertaPostor = inicializarListaOfertaPostor(loginUser.getUserName());
		return "/postor/indexPostor";
	}
	
	public String prepararDatosPostor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		return "/postor/datosPostor";
	}

	public String prepararAdicionarSubasta()
	{
		ParameterService serviceP= new ParameterService();
		sale = new Salesueb();
		nombre = user.getUserName();
		sale.setPhotoProduct(serviceP.getParameter("RutaImagen").getTextValue());
		return "/proveedor/nuevaSubasta";
	}
	
	public String prepararSubasta()
	{
		sale = (Salesueb) listaSubastas.getRowData();
		return "/postor/subasta";
	}
	
	public void prepararAgregarOferta()
	{
		idSale = sale.getId();
		oferta.setWinner("W");
		oferta.setDateOffer(new Date());
		nombre = loginUser.getUserName();
	}

	public String adicionarUser()
	{
		UserService service = new UserService();
		user.setEmailAddress(email1 + email2);
		boolean repetido = false;
		Iterator<User> it = listaUser.iterator();
		while (it.hasNext() && repetido == false)
		{
			if (it.next().getUserName().equals(user.getUserName())
					|| it.next().getEmailAddress().equals(user.getEmailAddress()))
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

			audit.adicionarAudit("Admin", "CREATE", "User", user.getId());

		} else if (repetido)
		{
			mensajeError = "Ese Usuario ya existe";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			return "";
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		return "/administrador/indexAdmin";
	}

	public String modificarUser()
	{
		UserService service = new UserService();
		user.setEmailAddress(email1 + email2);
		service.actualizar(user);
		audit.adicionarAudit("Admin", "UPDATE", "User", user.getId());

		return "/administrador/indexAdmin";
	}

	public void eliminarUser()
	{
		User usuarioTemp = (User) (listaUser.getRowData());
		UserService service = new UserService();
		Date now = new Date();
		if (usuarioTemp.getActive().equalsIgnoreCase("ACTIVE"))
		{
			usuarioTemp.setActive("INACTIVE");
			audit.adicionarAudit("Admin", "DELETE", "User", usuarioTemp.getId());
		} else if (usuarioTemp.getActive().equalsIgnoreCase("INACTIVE"))
		{
			String pass = EnviarCorreo.sendEmail(usuarioTemp.getEmailAddress());
			usuarioTemp.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			usuarioTemp.setFailedAttempts(0);
			usuarioTemp.setDateLastPassword(now);
			usuarioTemp.setActive("ACTIVE");
		}
		service.actualizar(usuarioTemp);
	}

	public String login()
	{
		logger.trace("Método de login");

		String pagina = "";
		UserService service = new UserService();
		User usuarioTemp = service.getUser(loginUser.getUserName());
		ParameterService serviceP= new ParameterService();
		int dias = (int) ((new Date().getTime()-usuarioTemp.getDateLastPassword().getTime())/86400000);
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

			if (usuarioTemp.getPassword().endsWith("$") || (dias >= serviceP.getParameter("Fecha").getNumberValue() && serviceP.getParameter("Fecha").getState().equalsIgnoreCase("ACTIVE") ))
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
						pagina = prepararCambioContraseña();
					} else
					{
						mensajeError = "Verificación del CAPTCHA invalida";
					}
				} catch (Exception e)
				{
				}
			} else if (usuarioTemp.getPassword()
					.equals(Cifrado.getStringMessageDigest(loginUser.getPassword(), Cifrado.MD5))&& usuarioTemp.getActive().equals("ACTIVE"))
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
							pagina = prepararIngresoProveedor();
						} else
						{
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
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
							pagina = prepararIngresoPostor();
						} else
						{
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
					}
				}else if (usuarioTemp.getUserType().equalsIgnoreCase("ADMIN"))
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
							pagina = "/administrador/inicioAdmin";
						} else
						{
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
					}
				}

			} else if(!usuarioTemp.getPassword().equals(Cifrado.getStringMessageDigest(loginUser.getPassword(), Cifrado.MD5)))
			{
				if(usuarioTemp.getFailedAttempts() < serviceP.getParameter("Intentos").getNumberValue() && serviceP.getParameter("Intentos").getState().equalsIgnoreCase("ACTIVE") ) 
				{
					mensajeError = "Contraseña o Usuario inválido";
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
					usuarioTemp.setFailedAttempts(usuarioTemp.getFailedAttempts()+1);
					service.actualizar(usuarioTemp); 
					audit.adicionarAudit(usuarioTemp.getUserName(), "FAILLOGIN", "User", 0);
				}
			}
			
			if(usuarioTemp.getFailedAttempts() == serviceP.getParameter("Intentos").getNumberValue() || usuarioTemp.getActive().equals("INACTIVE")) 
			{
				usuarioTemp.setActive("INACTIVE");
				service.actualizar(usuarioTemp);
				mensajeError = "Su cuenta se encuentra bloqueada, por favor comuniquese con un administrador.";
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
			}
			
			audit.adicionarAudit(usuarioTemp.getUserName(), "LOGIN", "---", 0);
			
		} else
		{
			mensajeError = "Contraseña o Usuario inválido";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}

		

		return pagina;
	}
	
	/**
	 * Método para hacer el cambio obligatorio de contraseña
	 * @throws IOException
	 */

	public void recuperarContraseña() throws IOException
	{
		UserService service = new UserService();
		String pass = userPass.getPassword();
		userPass.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5));
		userPass.setDateLastPassword(new Date());
		service.actualizar(userPass);
		
		audit.adicionarAudit(userPass.getUserName(), "UPDATE", "User", userPass.getId());

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
	}
	
	/**
	 * Método para link De olvidar contraseña
	 * @throws IOException
	 */

	public void cambiarContraseña() throws IOException
	{
		UserService service = new UserService();
		User userTemp = new User();
		userPass = service.getUser(loginUser.getUserName());
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
			
			audit.adicionarAudit(userTemp.getUserName(), "UPDATE", "User", userTemp.getId());

		} else
		{
			mensajeError = "No se encontró un usuario con ese correo.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");

	}

	public void logOut() throws IOException
	{

		UserService service = new UserService();
		User usuarioTemp = service.getUser(loginUser.getUserName());

		audit.adicionarAudit(usuarioTemp.getUserName(), "LOGOUT", "---", 0);

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
	}
	
	public String agregarOferta()
	{
		OfferersaleService service = new OfferersaleService();
		oferta.setIdentification(nombre);
		if(oferta.getValueOffer() <= sale.getValueBase())
		{
			mensajeError = "La oferta debe ser mayor que la oferta actual";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}else {
			sale.setValueCurrent(oferta.getValueOffer());
			modificarSubasta();
			service.nuevo(oferta);
			audit.adicionarAudit(nombre, "CREATE", "Offerersales", 0);
			audit.adicionarAudit(nombre, "UPDATE", "Salesueb", sale.getId());
		}
		return "/postor/subasta";
	}
	
	public void modificarSubasta()
	{
		SalesuebService service = new SalesuebService();
		service.actualizar(sale);
	}

	public String adicionarSubasta()
	{
		SalesuebMB saleB = new SalesuebMB();
		
		saleB.agregarSubasta(nombre, sale.getDateStart(), sale.getDateEnd(), sale.getPhotoProduct(), sale.getDescriptionProduct(),
				sale.getName(), sale.getValueBase());
		audit.adicionarAudit(nombre, "CREATE", "Salesueb", sale.getId());
		return "/proveedor/indexProveedor";
	}
	
	public void adicionarPostor() {
		UserService service = new UserService();
		boolean repetido = false;
		Iterator<User> it = getListarUser().iterator();
		while (it.hasNext() && repetido == false) {
			if (it.next().getUserName().equals(user.getUserName())) {
				repetido = true;
			}
		}

		if (repetido == false) {
			user.setDateLastPassword(new Date());
			user.setEmailAddress(email1 + email2);
			String pass = EnviarCorreo.sendEmail(user.getEmailAddress());
			user.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			service.nuevo(user);

			audit.adicionarAudit("Admin", "CREATE", "User", user.getId());

		} else if (repetido) {
			mensajeError = "Ese Usuario ya existe";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.invalidateSession();
		try {
			ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		List<Salesueb> lista = new SalesuebService().getSalesueb(userSales);
		listaProveedor = new ListDataModel<>(lista);
		return listaProveedor;
	}
	
	public DataModel getListaProveedor()
	{
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
	
	public DataModel getListaSubastas() {
		List<Salesueb> lista = new SalesuebService().lista();
		listaSubastas = new ListDataModel(lista);
		return listaSubastas;
	}

	public void setListaSubastas(DataModel listaSubastas) {
		this.listaSubastas = listaSubastas;
	}
	
	public DataModel inicializarListaOfertaPostor(String postor) {
		List<Offerersale> lista = new OfferersaleService().getOfferersale(postor);
		listaOfertaPostor = new ListDataModel(lista);
		return listaOfertaPostor;
	}

	public DataModel getListaOfertaPostor() {
		return listaOfertaPostor;
	}

	public void setListaOfertaPostor(DataModel listaOfertaPostor) {
		this.listaOfertaPostor = listaOfertaPostor;
	}
	
	public DataModel getListaOfertaPostor()
	{
		UserService service = new UserService();
		user = service.getUser(loginUser.getUserName());
		listaOfertaPostor = inicializarListaOfertaPostor(loginUser.getUserName());
		return listaOfertaPostor;
	}
	

}
