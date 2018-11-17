package service;

import java.util.List;

import dao.AuditDAO;
import dao.AuditDAOImpl;
import entity.Audit;

/**
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */
public class AuditService
{
	/**
	 * DAO
	 */
	AuditDAO audit = new AuditDAOImpl();
	
	/**
	 * Método para crear auditoría
	 * @param auditoria
	 */
	public void nuevo(Audit auditoria)
	{
		audit.nuevo(auditoria);
	}
	
	/**
	 * Método para obtener la lista de auditorías de un usuario específico
	 * @param userName
	 * @return lista
	 */
	public List<Audit> auditUser(String userName)
	{
		return audit.getAuditUser(userName);
	}
	
	/**
	 * Método para obtener la lista de auditorías de una operación CRUD específica
	 * @param operationCrud
	 * @return lista
	 */
	public List<Audit> auditCrud(String operationCrud)
	{
		return audit.getAuditCrud(operationCrud);
	}
	
	/**
	 * Método para modificar
	 * @param auditoria
	 */
	public void actualizar(Audit auditoria)
	{
		audit.actualizar(auditoria);
	}
	
	/**
	 * Método para eliminar
	 * @param auditoria
	 */
	public void eliminar(Audit auditoria)
	{
		audit.eliminar(auditoria);
	}
	
	/**
	 * Método que devuelve una lista con todas las auditorías
	 * @return lista
	 */
	public List<Audit> lista()
	{
		return audit.lista();
	}
	
	/**
	 * Método con el que podemos filtrar cualquier lista
	 * @param filtro
	 * @return lista por el filtro correspondiente
	 */
	public List<Audit> getAuditFilter(String filtro)
	{
		return audit.getAuditFilter(filtro);
	}
}
