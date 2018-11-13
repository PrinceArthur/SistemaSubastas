package dao;

import java.util.List;

import entity.User;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * Interface del DAO de user
 */
public interface UserDAO {

	/**
	 * Método para crear usuarios
	 * @param user
	 */
	public void nuevo(User user);
	
	/**
	 * Método que devuelve un usuario específico
	 * @param userName
	 * @return user
	 */
	public User getUser(String userName);
	
	/**
	 * Método para modificar usuarios
	 * @param user
	 */
	public void actualizar(User user);
	
	/**
	 * Método para eliminar usuarios
	 * @param user
	 */
	public void eliminar(User user);
	
	/**
	 * Método que devuelve una lista con todos los usuarios
	 * @return lista
	 */
	public List<User> lista();
}
