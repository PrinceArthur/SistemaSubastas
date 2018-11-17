package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import entity.Audit;
import util.HibernateUtil;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 * DAO
 */
public class AuditDAOImpl implements AuditDAO{

	/**
	 * Método para crear auditoría
	 * @param auditoria
	 */
	@Override
	public void nuevo(Audit auditoria) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.save(auditoria);
		t.commit();
		
	}

	/**
	 * Método para obtener la lista de auditorías de un usuario específico
	 * @param userName
	 * @return lista
	 */
	@Override
	public List<Audit> getAuditUser(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Audit where userName = :userName");
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
	 * Método para obtener la lista de auditorías de una operación CRUD específica
	 * @param operationCrud
	 * @return lista
	 */
	@Override
	public List<Audit> getAuditCrud(String operationCrud)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		Query q=session.createQuery("from Audit where operationCrud = :operationCrud");
		q.setParameter("operationCrud", operationCrud);
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
	 * Método con el que podemos filtrar cualquier lista
	 * @param filtro
	 * @return lista por el filtro correspondiente
	 */
	public List<Audit> getAuditFilter(String filtro)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Audit where " + filtro).list();
		t.commit();
		session.close();
		return lista;
	}
	
	/**
	 * Método para modificar
	 * @param auditoria
	 */
	@Override
	public void actualizar(Audit auditoria) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.update(auditoria);
		t.commit();
		
	}
	
	/**
	 * Método para eliminar
	 * @param auditoria
	 */
	@Override
	public void eliminar(Audit auditoria)
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		session.delete(auditoria);
		t.commit();
	}

	/**
	 * Método que devuelve una lista con todas las auditorías
	 * @return lista
	 */
	@Override
	public List<Audit> lista() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		List lista = session.createQuery("from Audit").list();
		t.commit();
		return lista;
	}


}
