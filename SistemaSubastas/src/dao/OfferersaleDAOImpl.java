package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Offerersale;
import entity.Parameter;
import util.HibernateUtil;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * DAO
 */
public class OfferersaleDAOImpl implements OfferersaleDAO
{

	/**
	 * M�todo que crear una oferta
	 * @param offerersale
	 */
	@Override
	public void nuevo(Offerersale offerersale)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(offerersale);
		t.commit();
	}

	/**
	 * M�tod que devuelve una lista de las ofertas de un usuario espec�fico
	 * @param userName
	 * @return lista
	 */
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
	
	/**
	 * M�todo que devuelve una lista de las ofertas hechas en una subasta espec�fica
	 * @param idSubasta
	 * @return list
	 */
	@Override
	public List<Offerersale> getOfertaDeSubasta(int idSales)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Offerersale where idSales = :idSales");
		q.setParameter("idSales", idSales);
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
	 * M�tdo para modificar una oferta
	 * @param offerersale
	 */
	@Override
	public void actualizar(Offerersale offerersale)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(offerersale);
		t.commit();
	}

	/**
	 * M�todo para eiminar una oferta
	 * @param oferrersale
	 */
	@Override
	public void eliminar(Offerersale oferrersale)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(oferrersale);
		t.commit();
	}

	/**
	 * M�todo que devuelve una lista de todas las ofertas
	 * @return
	 */
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
