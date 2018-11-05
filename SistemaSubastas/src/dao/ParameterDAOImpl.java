package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Audit;
import entity.Parameter;
import util.HibernateUtil;

public class ParameterDAOImpl implements ParameterDAO
{

	@Override
	public void nuevo(Parameter parameter)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(parameter);
		t.commit();
	}

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

	@Override
	public void actualizar(Parameter parameter)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(parameter);
		t.commit();
	}

	@Override
	public void eliminar(Parameter parameter)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(parameter);
		t.commit();
	}
	
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
