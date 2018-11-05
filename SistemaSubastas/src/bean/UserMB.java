package bean;

import java.io.IOException;
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
import entity.User;
import service.AuditService;
import service.UserService;

@ManagedBean
@SessionScoped
public class UserMB
{
	private AuditMB audit = new AuditMB();
	private User user;
	private User loginUser = new User();
	private User userAdmin = new User();
	private User userPass;
	private DataModel listaUser;
	private String mensajeError;

	public String prepararAdicionarUser()
	{
		user = new User();
		user.setActive("ACTIVE");
		user.setUserType("proveedor");
		Date now = new Date();
		user.setDateLastPassword(now);
		return "/administrador/registrarProvedor.xhtml";
	}

	public String prepararModificarUser()
	{
		user = (User) (listaUser.getRowData());
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
		return "/usuarios/recuperarContraseña";
	}

	public String prepararCambioContraseña()
	{
		userPass = new User();
		return "/usuarios/cambiarContraseña";
	}

	public String prepararIngresoProveedor(String userName)
	{
		UserService service = new UserService();
		user = service.getUser(userName);
		return "/usuarios/indexProveedor";
	}

	public String prepararIngresoPostor(String userName)
	{
		UserService service = new UserService();
		user = service.getUser(userName);
		return "/usuarios/indexPostor";
	}

	public String adicionarUser()
	{
		UserService service = new UserService();
		boolean repetido = false;
		Iterator<User> it = listaUser.iterator();
		while (it.hasNext() && repetido == false)
		{
			if (it.next().getUserName().equals(user.getUserName()))
			{
				repetido = true;
			}
		}

		if (repetido == false)
		{
			user.setDateLastPassword(new Date());
			String pass = EnviarCorreo.sendEmail(user.getEmailAddress());
			user.setPassword(pass);
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
			usuarioTemp.setPassword(pass);
			usuarioTemp.setDateLastPassword(now);
			usuarioTemp.setActive("ACTIVE");
		}
		service.actualizar(usuarioTemp);
	}

	public String login()
	{

		String pagina = "";
		UserService service = new UserService();
		User usuarioTemp = service.getUser(loginUser.getUserName());
		System.out.println(usuarioTemp.getUserType() + " - " + usuarioTemp.getPassword());
		String pass = loginUser.getPassword();
		loginUser.setPassword(Cifrado.getStringMessageDigest("" + pass, Cifrado.MD5) + "%");
		String password = loginUser.getPassword();

		if (usuarioTemp.getPassword().endsWith("$"))
		{

			usuarioTemp.setFailedAttempts(0);

			try
			{
				String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap().get("g-recaptcha-response");
				boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
				if (verify)
				{
					pagina = prepararCambioContraseña();
				} else
				{
					mensajeError = "Verificación del CAPTCHA invalida";
				}
			} catch (Exception e)
			{
			}

			pagina = prepararCambioContraseña();
		} else
		{

			System.out.println("Entra al else         " + usuarioTemp.getUserType() + "   " + loginUser.getPassword());
			
			if (usuarioTemp.getUserType().equalsIgnoreCase("admin") && usuarioTemp.getPassword().equals(loginUser.getPassword()))
			{
				userAdmin = service.getUser("admin");
				System.out.println("Entra bien");
				try
				{
					String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
							.getRequestParameterMap().get("g-recaptcha-response");
					boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
					if (verify)
					{
						pagina = "/administrador/indexAdmin";
					} else
					{
						mensajeError = "Verificación del CAPTCHA invalida";
					}
				} catch (Exception e)
				{
				}

			}else if (usuarioTemp.getUserType().equalsIgnoreCase("POSTOR")
					&& usuarioTemp.getPassword().equalsIgnoreCase(password)
					&& usuarioTemp.getActive().equalsIgnoreCase("ACTIVO"))
			{

				try
				{
					String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
							.getRequestParameterMap().get("g-recaptcha-response");
					boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
					if (verify)
					{
						pagina = prepararIngresoPostor(usuarioTemp.getUserName());
					} else
					{
						mensajeError = "Verificación del CAPTCHA invalida";
					}
				} catch (Exception e)
				{
				}

			} else if (usuarioTemp.getUserType().equalsIgnoreCase("PROVEEDOR")
					&& usuarioTemp.getPassword().equalsIgnoreCase(password)
					&& usuarioTemp.getActive().equalsIgnoreCase("ACTIVO"))
			{

				try
				{
					String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
							.getRequestParameterMap().get("g-recaptcha-response");
					boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
					if (verify)
					{
						pagina = prepararIngresoProveedor(usuarioTemp.getUserName());
					} else
					{
						mensajeError = "Verificación del CAPTCHA invalida";
					}
				} catch (Exception e)
				{
				}
			}
			else
			{
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
				mensajeError = "Validación de usuario o contraseña incorrectas";
				usuarioTemp.setFailedAttempts(+1);
			}
		}
		if (usuarioTemp.getFailedAttempts() == 3)
		{
			mensajeError = "Señor usuario usted exedio el número de intentos de ingreso al sistema. Su estado ahora es INACTIVO";
			usuarioTemp.setActive("INACTIVE");
			audit.adicionarAudit(usuarioTemp.getUserName(), "DELETE", "User", 0);
		}

		audit.adicionarAudit(usuarioTemp.getUserName(), "LOGIN", "---", 0);

		

		return pagina;
	}

	public String recuperarContraseña()
	{
		UserService service = new UserService();
		User userTemp = service.getUser(userPass.getUserName());
		service.actualizar(userTemp);
		return "/administrador/indexAdmin";

	}

	public void cambiarContraseña()
	{
		UserService service = new UserService();
		User userTemp = new User();
		userTemp = service.getUser(userPass.getUserName());
		Date date = new Date();

		if (userTemp.getEmailAddress().equals(userPass.getEmailAddress()))
		{
			EnviarCorreo.sendEmail(userTemp.getEmailAddress());
			userTemp.setDateLastPassword(date);
			service.actualizar(userTemp);
		} else
		{
			mensajeError = "El correo es incorrecto";
		}

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

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

}