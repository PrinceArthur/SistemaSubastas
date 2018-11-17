package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Audit;
import entity.Parameter;
import entity.Salesueb;
import util.HibernateUtil;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * DAO
 */
public class SalesuebDAOImpl implements SalesuebDAO
{

	/**
	 * Método que crea subastas
	 * @param salesueb
	 */
	@Override
	public void nuevo(Salesueb salesueb)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(salesueb);
		t.commit();
	}

	/**
	 * Método que devuelve una lista con las subastas de un proveedor específico
	 * @param userSales
	 * @return lista
	 */
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

	/**
	 * Método para modificar uan subasta
	 * @param salesueb
	 */
	@Override
	public void actualizar(Salesueb salesueb)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(salesueb);
		t.commit();
	}

	/**
	 * Método para eliminar una subasta
	 * @param salesueb
	 */
	@Override
	public void eliminar(Salesueb salesueb)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(salesueb);
		t.commit();
	}

	/**
	 * Método que devuelve una lista con todas las subastas
	 * @return lista
	 */
	@Override
	public List<Salesueb> lista()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Salesueb").list();
		t.commit();
		return lista;
	}

	/**
	 * Método que devuelve una lista con las subastas activas
	 * @return
	 */
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

	@Override
	public Salesueb listaSalesuebID(int id)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Salesueb a = (Salesueb) session.load(Salesueb.class,id);
		return a;
	}

	@Override
	public List<Salesueb> getSaleFilter(String filtro)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Salesueb where " + filtro).list();
		t.commit();
		return lista;
	}
	
	

}
