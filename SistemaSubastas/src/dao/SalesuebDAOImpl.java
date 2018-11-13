package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Parameter;
import entity.Salesueb;
import util.HibernateUtil;

public class SalesuebDAOImpl implements SalesuebDAO
{

	@Override
	public void nuevo(Salesueb salesueb)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(salesueb);
		t.commit();
	}

	@Override
	public List<Salesueb> getSalesueb(String userSales)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Salesueb where userSales = :userSales");
		q.setParameter("userSales", userSales);
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
	public void actualizar(Salesueb salesueb)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(salesueb);
		t.commit();
	}

	@Override
	public void eliminar(Salesueb salesueb)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(salesueb);
		t.commit();
	}

	@Override
	public List<Salesueb> lista()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Salesueb").list();
		t.commit();
		return lista;
	}

	@Override
	public List<Salesueb> listaActivas() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Salesueb where state = :state");
		q.setParameter("state", "ACTIVE");
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

}
