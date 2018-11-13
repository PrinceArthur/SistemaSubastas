package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Audit;
import entity.Parameter;
import util.HibernateUtil;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefan�a P�rez
 * DAO
 */
public class ParameterDAOImpl implements ParameterDAO
{

	/**
	 * M�todo para crear un par�metro
	 * @param parameter
	 */
	@Override
	public void nuevo(Parameter parameter)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(parameter);
		t.commit();
	}

	/**
	 * M�todo que devuelve un par�metro espec�fico
	 * @param parameterCode
	 * @return parametro
	 */
	@Override
	public Parameter getParameter(String parameterCode)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Parameter where parameterCode = :parameterCode");
		q.setParameter("parameterCode", parameterCode);
		List lista = q.list();
		t.commit();
		session.close();
		if(!lista.isEmpty())
		{
			return (Parameter)lista.get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * M�todo para modificar un par�metro
	 * @param parameter
	 */
	@Override
	public void actualizar(Parameter parameter)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(parameter);
		t.commit();
	}

	/**
	 * M�todo para eliminar un par�metro
	 * @param parameter
	 */
	@Override
	public void eliminar(Parameter parameter)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(parameter);
		t.commit();
	}
	
	/**
	 * M�todo que devuelve la lista de los par�metros
	 * @return lista
	 */
	@Override
	public List<Parameter> lista()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Parameter").list();
		t.commit();
		return lista;
	}


}
