package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.User;
import util.HibernateUtil;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * DAO
 */
public class UserDAOImpl implements UserDAO{

	/**
	 * Método para crear usuarios
	 * @param user
	 */
	@Override
	public void nuevo(User user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(user);
		t.commit();
		
	}

	/**
	 * Método que devuelve un usuario específico
	 * @param userName
	 * @return user
	 */
	@Override
	public User getUser(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from User where userName = :userName");
		q.setParameter("userName", userName);
		List lista = q.list();
		t.commit();
		session.close();
		if(!lista.isEmpty())
		{
			return (User)lista.get(0);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Método para modificar usuarios
	 * @param user
	 */
	@Override
	public void actualizar(User user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(user);
		t.commit();
		
	}
	
	/**
	 * Método para eliminar usuarios
	 * @param user
	 */
	@Override
	public void eliminar(User user)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(user);
		t.commit();
	}

	/**
	 * Método que devuelve una lista con todos los usuarios
	 * @return lista
	 */
	@Override
	public List<User> lista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from User").list();
		t.commit();
		return lista;
	}


}