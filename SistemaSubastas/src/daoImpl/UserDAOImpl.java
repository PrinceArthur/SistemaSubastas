package daoImpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import dao.UserDAO;
import entities.User;

public class UserDAOImpl implements UserDAO{

	@Override
	public void save(User user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(user);
		t.commit();
		
	}

	@Override
	public User getUser(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		return (User) session.load(User.class, userName);
	}

	@Override
	public List<User> list() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from User").list();
		t.commit();
		return lista;
	}

	@Override
	public void remove(User user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(user);
		t.commit();
		
	}

	@Override
	public void update(User user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(user);
		t.commit();
		
	}

}
