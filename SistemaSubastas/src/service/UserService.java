package service;

import java.util.List;

import dao.UserDAO;
import dao.UserDAOImpl;
import entity.User;

public class UserService
{
	private UserDAO dao = new UserDAOImpl();

	public void nuevo(User user)
	{
		dao.nuevo(user);
	}

	public User getUser(String userName)
	{
		return dao.getUser(userName);
	}

	public void actualizar(User user)
	{
		dao.actualizar(user);
	}

	public void eliminar(User user)
	{
		dao.eliminar(user);
	}

	public List<User> lista()
	{
		return dao.lista();
	}

}
