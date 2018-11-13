package dao;

import java.util.List;

import entity.User;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * Interface del DAO de user
 */
public interface UserDAO {

	/**
	 * M�todo para crear usuarios
	 * @param user
	 */
	public void nuevo(User user);
	
	/**
	 * M�todo que devuelve un usuario espec�fico
	 * @param userName
	 * @return user
	 */
	public User getUser(String userName);
	
	/**
	 * M�todo para modificar usuarios
	 * @param user
	 */
	public void actualizar(User user);
	
	/**
	 * M�todo para eliminar usuarios
	 * @param user
	 */
	public void eliminar(User user);
	
	/**
	 * M�todo que devuelve una lista con todos los usuarios
	 * @return lista
	 */
	public List<User> lista();
}
