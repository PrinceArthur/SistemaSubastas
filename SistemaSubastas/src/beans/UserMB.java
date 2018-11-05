package beans;

import entities.User;
import service.UserService;

import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

@ManagedBean
@SessionScoped
public class UserMB {
	
	private User user;
	private DataModel listaUser;
	
	public String prepararAdicionarUser() {
		user = new User();
		user.setActive("A");
		return "agregarProveedor";
	}
	
	public String prepararModificarUser() {
		user = (User) (listaUser.getRowData());
		return "modificarProveedor";
	}
	
	public String eliminarUser() {
		User usuarioTemp = (User) (listaUser.getRowData());
		UserService dao = new UserService();
		usuarioTemp.setActive("I");
		dao.update(usuarioTemp);
		//dao.remove(usuarioTemp);		
		return "indexAdmin";
	}
	
	public String login()
	{
		
		String pagina = "";
		user = new User();
		UserService dao = new UserService();
		User usuarioTemp = dao.getUser(user.getUserName());
		
		if(usuarioTemp.getUserType().equals("admin"))
		{
			pagina = "indexAdmin";
		}
		else
		{
			pagina = "error";
		}
		
		return pagina;
	}
	
	public String adicionarUser() {
		UserService dao = new UserService();
		boolean repetido = false;
		Iterator<User> it = listaUser.iterator();
		while(it.hasNext() && repetido == false)
		{
			if(it.next().getUserName().equals(user.getUserName()))
			{
				repetido = true;
			}
		}
		if(repetido == false)
		{
			dao.save(user);		
		}
		else {
			return "error";
		}
		return "indexAdmin";
	}
	
	public String modificarUser() {
		UserService dao = new UserService();
		dao.update(user);
		return "indexAdmin";
	}
	
	public User getUsuario() {
		return user;
	}

	public void setUsuario(User usuario) {
		this.user = usuario;
	}
	
	
	public DataModel getListarUser() {
		List<User> lista = new UserService().list();
		listaUser = new ListDataModel(lista);
		return listaUser;
	}
}
