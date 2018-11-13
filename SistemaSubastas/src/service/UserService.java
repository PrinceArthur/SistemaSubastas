package service;

import java.util.List;

import dao.UserDAO;
import dao.UserDAOImpl;
import entity.User;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Service
 */
public class UserService
{
	private UserDAO dao = new UserDAOImpl();

	/**
	 * M�todo para crear usuarios
	 * @param user
	 */
	public void nuevo(User user)
	{
		dao.nuevo(user);
	}

	/**
	 * M�todo que devuelve un usuario espec�fico
	 * @param userName
	 * @return user
	 */
	public User getUser(String userName)
	{
		return dao.getUser(userName);
	}

	/**
	 * M�todo para modificar usuarios
	 * @param user
	 */
	public void actualizar(User user)
	{
		dao.actualizar(user);
	}

	/**
	 * M�todo para eliminar usuarios
	 * @param user
	 */
	public void eliminar(User user)
	{
		dao.eliminar(user);
	}

	/**
	 * M�todo que devuelve una lista con todos los usuarios
	 * @return lista
	 */
	public List<User> lista()
	{
		return dao.lista();
	}

}
