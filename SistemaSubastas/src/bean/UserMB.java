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
	private User userPass = new User();
	private DataModel listaUser;
	private String mensajeError;
	private String email1;
	private String email2;

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
		UserService service = new UserService();
		boolean repetido = false;
		Iterator<User> it = getListarUser().iterator();
		while (it.hasNext() && repetido == false)
		{
			if (it.next().getUserName().equals(loginUser.getUserName()))
			{
				repetido = true;
			}
		}
 		if (repetido == true)
		{
			userPass = service.getUser(loginUser.getUserName());
			
			try
			{
				String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap().get("g-recaptcha-response");
				boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
				if (verify)
				{
					return "recuperarContraseña";
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
			mensajeError = "Este usuario no existe.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}
		
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
		return "/proveedor/indexProveedor";
	}

	public String prepararIngresoPostor(String userName)
	{
		UserService service = new UserService();
		user = service.getUser(userName);
		return "/postor/indexPostor";
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
			if(it.next().getEmailAddress().equals(user.getEmailAddress()))
			{
				repetido = true;
			}
		}

		if (repetido == false)
		{
			user.setDateLastPassword(new Date());
			user.setEmailAddress(email1 + email2);
			String pass = EnviarCorreo.sendEmail(user.getEmailAddress());
			user.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5));
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
		user.setEmailAddress(email1+email2);
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
		boolean existe = false;
		
		Iterator<User> it = getListarUser().iterator();
		while (it.hasNext() && existe == false)
		{
			if (it.next().getUserName().equals(loginUser.getUserName()))
			{
				existe = true;
			}
		}
		
		
		if(existe)
		{
			
			
			if(loginUser.getPassword().endsWith("$"))
			{
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
			}
			else if(usuarioTemp.getPassword().equals(Cifrado.getStringMessageDigest(loginUser.getPassword(), Cifrado.MD5)))
			{
				
				
				if(usuarioTemp.getUserType().equalsIgnoreCase("PROVEEDOR"))
				{
					try
					{
						String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
								.getRequestParameterMap().get("g-recaptcha-response");
						boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
						if (verify)
						{
							pagina = prepararIngresoProveedor();
						} else
						{
							mensajeError = "Verificación del CAPTCHA invalida";
						}
					} catch (Exception e)
					{
					}
				}
				else if(usuarioTemp.getUserType().equalsIgnoreCase("POSTOR"))
				{
					try
					{
						String gRecaptchaResponse = FacesContext.getCurrentInstance().getExternalContext()
								.getRequestParameterMap().get("g-recaptcha-response");
						boolean verify = VerifyRecaptcha.verify(gRecaptchaResponse);
						if (verify)
						{
							pagina = "/postor/indexPostor";
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
					mensajeError = "Contraseña o Usuario inválido";
				}
				
			}
			else if(usuarioTemp.getUserType().equalsIgnoreCase("ADMIN") && usuarioTemp.getPassword().equals(loginUser.getPassword()))
			{
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
			}
		}
		else
		{
			mensajeError = "Este usuario no existe";
		}
		
		
		
		
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		audit.adicionarAudit(usuarioTemp.getUserName(), "LOGIN", "---", 0);

		

		return pagina;
	}

	public String recuperarContraseña()
	{
		UserService service = new UserService();
		String pass = userPass.getPassword();
		userPass.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5));
		service.actualizar(userPass);
		
		if(userPass.getUserType().equalsIgnoreCase("proveedor"))
		{
			return prepararIngresoProveedor();
		}
		else
		{
			return prepararIngresoPostor(userPass.getUserName());
		}
	}

	public void cambiarContraseña()
	{
		UserService service = new UserService();
		User userTemp = new User();
		userPass.setEmailAddress(email1+email2);
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
		
		if(existe)
		{
			String pass = EnviarCorreo.sendEmail(userTemp.getEmailAddress());
			userTemp.setDateLastPassword(date);
			userTemp.setPassword(Cifrado.getStringMessageDigest(pass, Cifrado.MD5) + "$");
			service.actualizar(userTemp);
			
		}else
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

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	

}
