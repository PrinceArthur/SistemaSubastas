package service;

import java.util.List;

import dao.UserDAO;
import dao.UserDAOImpl;
import entity.User;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Service
 */
public class UserService
{
	private UserDAO dao = new UserDAOImpl();

	/**
	 * Método para crear usuarios
	 * @param user
	 */
	public void nuevo(User user)
	{
		dao.nuevo(user);
	}

	/**
	 * Método que devuelve un usuario específico
	 * @param userName
	 * @return user
	 */
	public User getUser(String userName)
	{
		return dao.getUser(userName);
	}

	/**
	 * Método para modificar usuarios
	 * @param user
	 */
	public void actualizar(User user)
	{
		dao.actualizar(user);
	}

	/**
	 * Método para eliminar usuarios
	 * @param user
	 */
	public void eliminar(User user)
	{
		dao.eliminar(user);
	}

	/**
	 * Método que devuelve una lista con todos los usuarios
	 * @return lista
	 */
	public List<User> lista()
	{
		return dao.lista();
	}

}
