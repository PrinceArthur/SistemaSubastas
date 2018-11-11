package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Offerersale;
import entity.Parameter;
import util.HibernateUtil;

public class OfferersaleDAOImpl implements OfferersaleDAO
{

	@Override
	public void nuevo(Offerersale offerersale)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(offerersale);
		t.commit();
	}

	@Override
	public List<Offerersale> getOfferersale(String userName)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Offerersale where userName = :userName");
		q.setParameter("userName", userName);
		List lista = q.list();
		t.commit();
		session.close();
		if(!lista.isEmpty())
		{
			return lista;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void actualizar(Offerersale offerersale)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(offerersale);
		t.commit();
	}

	@Override
	public void eliminar(Offerersale oferrersale)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(oferrersale);
		t.commit();
	}

	@Override
	public List<Offerersale> lista()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Offerersale").list();
		t.commit();
		return lista;
	}

}
