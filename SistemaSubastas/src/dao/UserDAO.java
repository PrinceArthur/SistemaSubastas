package dao;

import java.util.List;

import entity.User;

public interface UserDAO {

	public void nuevo(User user);
	public User getUser(String userName);
	public void actualizar(User user);
	public void eliminar(User user);
	public List<User> lista();
}
